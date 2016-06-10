package com.mrl.debugger;

import com.mrl.debugger.layers.base.MrlLocationLayer;
import rescuecore2.Constants;
import rescuecore2.Timestep;
import rescuecore2.messages.control.KVTimestep;
import rescuecore2.misc.Pair;
import rescuecore2.score.ScoreFunction;
import rescuecore2.standard.components.StandardViewer;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.view.RenderedObject;
import rescuecore2.view.ViewComponent;
import rescuecore2.view.ViewLayer;
import rescuecore2.view.ViewListener;
import rescuecore2.worldmodel.EntityID;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import static rescuecore2.misc.java.JavaTools.instantiate;

/**
 * A simple viewer.
 */
public class MrlViewer extends StandardViewer{
    private static org.apache.log4j.Logger Logger = org.apache.log4j.Logger.getLogger(MrlViewer.class);
    public static EntityID CHECK_ID = new EntityID(0);
    public static EntityID CHECK_ID2 = new EntityID(0);


    private static final int FONT_SIZE = 20;
    private static final int MAX_PROPERTIES = 21;
    private MrlAnimatedWorldModelViewer viewer;
    private int worldTime = 0;
    private JLabel worldTimeLabel = new JLabel("/" + worldTime + "   ");
    private JLabel timeLabel;
    private JLabel scoreLabel;
    private JPanel panel = new JPanel(new BorderLayout());
    private JPanel topPanel = new JPanel(new BorderLayout());
    private JPanel bottomPanel = new JPanel(new BorderLayout());
    private JSplitPane centerPanel;
    private final JTable propertiesTable = new JTable(new Object[MAX_PROPERTIES][2], new String[]{"Property", "Value"});
    JComboBox<StandardEntity> agentCombo = new JComboBox<StandardEntity>();
    public static EntityID agentSelected = null;
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Agents Data");
    JTree tree = new JTree(rootNode);
    JPopupMenu popup = new JPopupMenu("popupMenu");
    StandardEntity selectedObject;
    boolean shouldUpdateAgentData = true;
    JCheckBox jCheckBoxUpdateAgentData = new JCheckBox("update Agent Data", shouldUpdateAgentData);
    public static long randomValue;
    boolean pause = false;
    int time;
    private NumberFormat format;
    private ScoreFunction scoreFunction;
    Map<Integer, KVTimestep> dataMap = new HashMap<>();
    boolean isSetToManual = false;
    int currentTime;
    private String scoreValue;
    private boolean showInLaunch = false;
    private JTree layerTree;
    public static Map<EntityID, StandardEntity> BUILDINGS;//TODO :just for test

    public MrlViewer(boolean showInLaunch) {
        this.showInLaunch = showInLaunch;
    }

    public MrlViewer() {
    }

    public void setXY(int x, int y) {
        MrlLocationLayer.xy = new Pair<Integer, Integer>(x, y);
    }

    public void selectObject(Integer id) {
        for (int i = 0; i < MAX_PROPERTIES; i++) {
            propertiesTable.setValueAt("", i, 0);
            propertiesTable.setValueAt("", i, 1);
        }
        if (id == null || id == -1) {
            int i = 0;
            int channelCount = config.getIntValue("comms.channels.count");
            propertiesTable.setValueAt("com channels", i, 0);
            propertiesTable.setValueAt(channelCount, i++, 1);
            for (int j = 0; j < channelCount; j++) {
                String key = "comms.channels." + j + ".type";
                propertiesTable.setValueAt("com." + j + "." + config.getValue(key), i, 0);
                String range = config.getValue("comms.channels." + j + ".range", "-1");
                if (range.equals("-1")) {
                    range = "";
                } else {
                    range = "range:" + range;
                }
                String size = config.getValue("comms.channels." + j + ".messages.size", "-1");
                if (size.equals("-1")) {
                    size = "";
                } else {
                    size = " size:" + size;
                }
                String mPC = config.getValue("comms.channels." + j + ".messages.max", "-1");
                if (mPC.equals("-1")) {
                    mPC = "";
                } else {
                    mPC = " mpc:" + mPC;
                }
                String noise = config.getValue("comms.channels." + j + ".noise.input.dropout.use", "-1");
                String dropOut = "";
                if (noise.equals("-1")) {
                    noise = "";
                } else {
                    noise = " noise:" + noise;
                    dropOut = "(" + config.getValue("comms.channels." + j + ".noise.input.dropout.p", "-1") + ")";
                }
                String bandwidth = config.getValue("comms.channels." + j + ".bandwidth", "-1");
                if (bandwidth.equals("-1")) {
                    bandwidth = "";
                } else {
                    bandwidth = " bandwidth:" + bandwidth;
                }
                propertiesTable.setValueAt(range + size + bandwidth + mPC + noise + dropOut, i++, 1);
            }
            propertiesTable.setValueAt("floor height", i, 0);
            propertiesTable.setValueAt(config.getValue("collapse.floor-height", "-1"), i++, 1);
            propertiesTable.setValueAt("random ignition", i, 0);
            propertiesTable.setValueAt(config.getValue("ignition.random.lambda", "-1"), i++, 1);
            propertiesTable.setValueAt("cycles", i, 0);
            propertiesTable.setValueAt(config.getValue("kernel.timesteps", "-1"), i++, 1);
            propertiesTable.setValueAt("FBs", i, 0);
            propertiesTable.setValueAt(model.getEntitiesOfType(StandardEntityURN.FIRE_BRIGADE).size(), i++, 1);
            propertiesTable.setValueAt("ATs", i, 0);
            propertiesTable.setValueAt(model.getEntitiesOfType(StandardEntityURN.AMBULANCE_TEAM).size(), i++, 1);
            propertiesTable.setValueAt("PFs", i, 0);
            propertiesTable.setValueAt(model.getEntitiesOfType(StandardEntityURN.POLICE_FORCE).size(), i++, 1);
            return;
        }
        selectedObject = model.getEntity(new EntityID(id));
        CHECK_ID = new EntityID(id);
        StaticViewProperties.selectedObject = selectedObject;
        prepareExtraDataPanel();

        repaintSelectedObject();

        int i = 0;
        propertiesTable.setValueAt(selectedObject.getURN().replace("urn:rescuecore2.standard:entity:", ""), i, 0);
        propertiesTable.setValueAt(selectedObject.getID().getValue(), i++, 1);
        propertiesTable.setValueAt("x", i, 0);
        propertiesTable.setValueAt(selectedObject.getProperty(MrlConstants.X_URN).getValue(), i++, 1);
        propertiesTable.setValueAt("y", i, 0);
        propertiesTable.setValueAt(selectedObject.getProperty(MrlConstants.Y_URN).getValue(), i++, 1);

       /* if (selectedObject instanceof Human) {
            propertiesTable.setValueAt("position", i, 0);
            propertiesTable.setValueAt(selectedObject.getProperty(POSITION_URN).getValue(), i++, 1);
            propertiesTable.setValueAt("hp", i, 0);
            propertiesTable.setValueAt(selectedObject.getProperty(HP_URN).getValue(), i++, 1);
            propertiesTable.setValueAt("damage", i, 0);
            propertiesTable.setValueAt(selectedObject.getProperty(DAMAGE_URN).getValue(), i++, 1);
            propertiesTable.setValueAt("buriedness", i, 0);
            propertiesTable.setValueAt(selectedObject.getProperty(BURIEDNESS_URN).getValue(), i++, 1);
            propertiesTable.setValueAt("stamina", i, 0);
            propertiesTable.setValueAt(selectedObject.getProperty(STAMINA_URN).getValue(), i++, 1);
            agentCombo.setSelectedItem(selectedObject);
            agentSelected = selectedObject.getID();
            if (selectedObject instanceof FireBrigade) {
                propertiesTable.setValueAt("waterquantity", i, 0);
                propertiesTable.setValueAt(selectedObject.getProperty(WATER_QUANTITY_URN).getValue(), i++, 1);

            }
            if (selectedObject instanceof Civilian) {
                List<Integer> list = civilianPassableBuildingMap.get(selectedObject.getID());
                propertiesTable.setValueAt("possible buildings", i, 0);
                propertiesTable.setValueAt(list, i++, 1);
            } else {
                MrlPlatoonAgent platoonAgent = PLATOON_AGENTS_FOR_VIEWER.get(selectedObject.getID());
                if (platoonAgent != null) {
                    propertiesTable.setValueAt("Is HardWalking", i, 0);
                    propertiesTable.setValueAt(platoonAgent.isHardWalking(), i++, 1);
                    propertiesTable.setValueAt("Target", i, 0);
                    propertiesTable.setValueAt(AGENT_TARGET.get(platoonAgent.getID()), i++, 1);
                    propertiesTable.setValueAt("Stuck", i, 0);
                    propertiesTable.setValueAt(platoonAgent.isStuck(), i++, 1);
                }
            }
        } else if (selectedObject instanceof Area) {
            propertiesTable.setValueAt("neighbours", i, 0);
//            Property p = selectedObject.getProperty(EDGES_URN);
            propertiesTable.setValueAt(((Area) selectedObject).getNeighbours(), i++, 1);
//            Collection edges = (Collection) p.getValue();
//            for (Object o : edges) {
//                if (((Edge) o).isPassable()) {
//                    propertiesTable.setValueAt(propertiesTable.getValueAt(i, 1).toString() + ((Edge) o).getNeighbour() + ", ", 3, 1);
//                }
//            }
            EntityID agent = selectedObject.getID();
            if (agentCombo.getSelectedItem() != null) {
                agent = ((StandardEntity) agentCombo.getSelectedItem()).getID();
            }
            if (selectedObject instanceof Building) {
                propertiesTable.setValueAt("areaground", i, 0);
                propertiesTable.setValueAt(selectedObject.getProperty("urn:rescuecore2.standard:property:buildingareaground").getValue(), i++, 1);
                propertiesTable.setValueAt("totalarea", i, 0);
                propertiesTable.setValueAt(selectedObject.getProperty("urn:rescuecore2.standard:property:buildingareatotal").getValue(), i++, 1);
                propertiesTable.setValueAt("floors", i, 0);
                propertiesTable.setValueAt(selectedObject.getProperty("urn:rescuecore2.standard:property:floors").getValue(), i++, 1);
                propertiesTable.setValueAt("temperature", i, 0);
                propertiesTable.setValueAt(selectedObject.getProperty(TEMPERATURE_URN).getValue(), i++, 1);
                propertiesTable.setValueAt("ignition", i, 0);
                propertiesTable.setValueAt(selectedObject.getProperty("urn:rescuecore2.standard:property:ignition").getValue(), i++, 1);
                propertiesTable.setValueAt("fieriness", i, 0);
                propertiesTable.setValueAt(selectedObject.getProperty(FIERYNESS_URN).getValue(), i++, 1);
                propertiesTable.setValueAt("brokenness", i, 0);
                propertiesTable.setValueAt(selectedObject.getProperty("urn:rescuecore2.standard:property:brokenness").getValue(), i++, 1);
                if (VIEWER_BUILDINGS_MAP.containsKey(agent)) {
                    MrlBuilding mrlBuilding = VIEWER_BUILDINGS_MAP.get(agent).get(selectedObject.getID());
                    propertiesTable.setValueAt("reachable", i, 0);
                    propertiesTable.setValueAt(mrlBuilding.isReachable(), i++, 1);
                    propertiesTable.setValueAt("visitable", i, 0);
                    propertiesTable.setValueAt(mrlBuilding.isVisitable(), i++, 1);
                    propertiesTable.setValueAt("visited", i, 0);
                    propertiesTable.setValueAt(mrlBuilding.isVisited(), i++, 1);
                    propertiesTable.setValueAt("Sensed Civilians", i, 0);
                    propertiesTable.setValueAt(mrlBuilding.getCivilians(), i++, 1);
                    propertiesTable.setValueAt("EstimatedTemp", i, 0);
                    propertiesTable.setValueAt(mrlBuilding.getEstimatedTemperature(), i++, 1);
                    propertiesTable.setValueAt("Estimated fieriness", i, 0);
                    propertiesTable.setValueAt(mrlBuilding.getEstimatedFieryness(), i++, 1);
                    propertiesTable.setValueAt("fuel", i, 0);
                    propertiesTable.setValueAt(mrlBuilding.getFuel(), i++, 1);
                    propertiesTable.setValueAt("energy", i, 0);
                    propertiesTable.setValueAt(mrlBuilding.getEnergy(), i++, 1);
                    propertiesTable.setValueAt("Ignition Time", i, 0);
                    propertiesTable.setValueAt(mrlBuilding.getIgnitionTime(), i++, 1);
                }


            } else if (selectedObject instanceof Road) {

                propertiesTable.setValueAt("blockades", i, 0);
                propertiesTable.setValueAt(selectedObject.getProperty("urn:rescuecore2.standard:property:blockades").getValue(), i++, 1);
                if (VIEWER_ROADS_MAP.containsKey(agent)) {
                    MrlRoad mrlRoad = VIEWER_ROADS_MAP.get(agent).get(selectedObject.getID());
                    propertiesTable.setValueAt("groundArea", i, 0);
                    propertiesTable.setValueAt(mrlRoad.getGroundArea(), i++, 1);
                    propertiesTable.setValueAt("blockade seen", i, 0);
                    propertiesTable.setValueAt(mrlRoad.getParent().getBlockades(), i++, 1);
                    propertiesTable.setValueAt("last seen", i, 0);
                    propertiesTable.setValueAt(mrlRoad.getLastSeenTime(), i++, 1);
                    propertiesTable.setValueAt("need update", i, 0);
                    propertiesTable.setValueAt(mrlRoad.isNeedUpdate(), i++, 1);
                    propertiesTable.setValueAt("open edges", i, 0);
                    propertiesTable.setValueAt(mrlRoad.getOpenEdges().size(), i++, 1);
                    propertiesTable.setValueAt("MrlBlockades", i, 0);
                    propertiesTable.setValueAt(mrlRoad.getMrlBlockades().size(), i++, 1);
                    propertiesTable.setValueAt("last update time", i, 0);
                    propertiesTable.setValueAt(mrlRoad.getLastUpdateTime(), i++, 1);
                    propertiesTable.setValueAt("last reset time", i, 0);
                    propertiesTable.setValueAt(mrlRoad.getLastResetTime(), i++, 1);
                }
            }
        } else if (selectedObject instanceof Blockade) {
            propertiesTable.setValueAt("repair cost", i, 0);
            propertiesTable.setValueAt(selectedObject.getProperty("urn:rescuecore2.standard:property:repaircost").getValue(), i++, 1);
            MrlBlockade mrlBlockade = new MrlBlockade(null, (Blockade) selectedObject, Util.getPolygon(((Blockade) selectedObject).getApexes()));
            propertiesTable.setValueAt("ground area", i, 0);
            propertiesTable.setValueAt(mrlBlockade.getGroundArea(), i++, 1);

        }
*/
    }

    private void repaintSelectedObject() {
        viewer.repaint();
    }

    @Override
    protected void postConnect() {
        super.postConnect();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        viewer = new MrlAnimatedWorldModelViewer();
        viewer.initialise(config);
        viewer.view(model);
        // CHECKSTYLE:OFF:MagicNumber
//        viewer.setPreferredSize(new Dimension(900, 500));
        // CHECKSTYLE:ON:MagicNumber
        timeLabel = new JLabel("Time: Not started", JLabel.CENTER);
//        timeLabel.setBackground(Color.WHITE);
        timeLabel.setOpaque(true);
        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.PLAIN, FONT_SIZE));
        scoreFunction = makeScoreFunction();
        format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(5);
        scoreLabel = new JLabel("   Score: Not started", JLabel.CENTER);
//        scoreLabel.setBackground(Color.WHITE);
        scoreLabel.setOpaque(true);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.PLAIN, FONT_SIZE));

        topPanel.setPreferredSize(new Dimension(1000, 25));
        topPanel.add(timeLabel, BorderLayout.CENTER);
        topPanel.add(scoreLabel, BorderLayout.WEST);

        addAllInCombo(agentCombo, new ArrayList<>(model.getEntitiesOfType(StandardEntityURN.FIRE_BRIGADE)));
        addAllInCombo(agentCombo, new ArrayList<>(model.getEntitiesOfType(StandardEntityURN.POLICE_FORCE)));
        addAllInCombo(agentCombo, new ArrayList<>(model.getEntitiesOfType(StandardEntityURN.AMBULANCE_TEAM)));
        JScrollPane c = new JScrollPane(agentCombo);
        c.setMinimumSize(new Dimension(80, 30));
        c.setBorder(null);
        JScrollPane s = new JScrollPane(propertiesTable);
        s.setMinimumSize(new Dimension(80, 180));
//        s.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        s.setBorder(null);

        BUILDINGS = new HashMap<>();
        int i = 0;
        for (StandardEntity entity : model.getEntitiesOfType(StandardEntityURN.BUILDING)) {
            BUILDINGS.put(entity.getID(), entity);
        }

        DefaultMutableTreeNode root = processHierarchy(viewer.getLayers());
        layerTree = new JTree(root);
        layerTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        layerTree.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean focused) {
                Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object object = node.getUserObject();
                if (object instanceof LayerComponent) {
                    LayerComponent layerComponent = (LayerComponent) object;
                    JMenuItem menuItem = layerComponent.getMenuItem();
                    setIcon(menuItem.getIcon());
                    tree.repaint();
                }
                return c;
            }
        });
        layerTree.setLargeModel(true);
        layerTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTree tree = ((JTree) e.getSource());
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Object object = node.getUserObject();
                    if (object instanceof LayerComponent) {
                        LayerComponent layerComponent = (LayerComponent) object;
                        Action action = layerComponent.getMenuItem().getAction();
                        action.actionPerformed(null);
                        viewer.repaint();
                    }
                }
            }
        });

        JScrollPane t = new JScrollPane(layerTree);
        t.setMinimumSize(new Dimension(80, 650));
        t.setSize(new Dimension(80, 650));
        t.setAutoscrolls(true);


        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pane.setDividerSize(5);
        pane.setDividerLocation(140);
        pane.setAutoscrolls(true);
        pane.setTopComponent(s);
        pane.setBottomComponent(t);
        JSplitPane pane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        pane2.setDividerSize(5);
        pane2.setDividerLocation(30);
        pane2.setTopComponent(c);
        pane2.setBottomComponent(pane);
        panel.add(pane2);
        panel.setPreferredSize(new Dimension(80, 550));
        centerPanel = new JSplitPane();
        centerPanel.setLeftComponent(viewer);
        centerPanel.setRightComponent(panel);
        centerPanel.setDividerSize(5);

        bottomPanel.setPreferredSize(new Dimension(1000, 25));

        jCheckBoxUpdateAgentData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                shouldUpdateAgentData = !shouldUpdateAgentData;
            }
        });
        bottomPanel.add(jCheckBoxUpdateAgentData, BorderLayout.EAST);

        JTextField timeTextField = new JTextField();
        timeTextField.setPreferredSize(new Dimension(40, 20));
        timeTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JTextField field = (JTextField) actionEvent.getSource();
                try {
                    manualTimeStep(Integer.parseInt(field.getText()));
                } catch (Exception ignore) {
                }
            }
        });
        JButton nextTimeButton = new JButton("next time");
        nextTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    manualNextTimeStep();
                } catch (Exception ignore) {
                }
            }
        });

        JTextField idTextField = new JTextField();
        idTextField.setPreferredSize(new Dimension(60, 20));
        idTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JTextField field = (JTextField) actionEvent.getSource();
                try {
                    selectObject(Integer.parseInt(field.getText()));
                } catch (Exception ignore) {
                    //System.out.println("THISSSSSSSSSSSS");
                }
            }
        });

        JTextField checkIdTextField = new JTextField();
        checkIdTextField.setPreferredSize(new Dimension(60, 20));
        checkIdTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JTextField field = (JTextField) actionEvent.getSource();
                try {
                    CHECK_ID2 = new EntityID(Integer.parseInt(field.getText()));
                } catch (Exception ignore) {
                }
            }
        });
        JTextField xyTextField = new JTextField();
        xyTextField.setPreferredSize(new Dimension(70, 20));
        xyTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JTextField field = (JTextField) actionEvent.getSource();
                try {
                    String[] s = field.getText().split(",");
                    setXY((int) Double.parseDouble(s[0].trim()), (int) Double.parseDouble(s[1].trim()));
                    viewer.repaint();
                } catch (Exception ignore) {
                }
            }
        });


        final String[] indexComboVal = {"Building", "AmbulanceTeam", "FireBrigade", "PoliceForce", "Road"};
        final JComboBox indexTypeCombo = new JComboBox(indexComboVal);

        JCheckBox jCheckBoxPause = new JCheckBox("Pause");
        jCheckBoxPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                pause = !pause;
            }
        });

        JButton refreshColor = new JButton("refresh color");
        refreshColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                randomValue = System.currentTimeMillis();
                viewer.repaint();
            }
        });
        JPanel bPanel = new JPanel();
        bPanel.setLayout(new BoxLayout(bPanel, BoxLayout.X_AXIS));

        bPanel.add(Box.createHorizontalStrut(7));
        bPanel.add(refreshColor);

        bPanel.add(Box.createHorizontalStrut(7));
        bPanel.add(new JSeparator(SwingConstants.VERTICAL));
        bPanel.add(Box.createHorizontalStrut(2));

        bPanel.add(new JLabel("Time:"));
        bPanel.add(timeTextField);
        bPanel.add(worldTimeLabel);
        bPanel.add(nextTimeButton);

        bPanel.add(jCheckBoxPause);

        bPanel.add(Box.createHorizontalStrut(7));
        bPanel.add(new JSeparator(SwingConstants.VERTICAL));
        bPanel.add(Box.createHorizontalStrut(2));

        bPanel.add(new JLabel(" ID:"));
        bPanel.add(idTextField);

        bPanel.add(Box.createHorizontalStrut(7));
        bPanel.add(new JSeparator(SwingConstants.VERTICAL));
        bPanel.add(Box.createHorizontalStrut(2));
        bPanel.add(new JLabel(" Index:"));

        bPanel.add(new JLabel(" IndexType:"));
        bPanel.add(indexTypeCombo);
        bPanel.add(Box.createHorizontalStrut(5));
        bPanel.add(new JSeparator(SwingConstants.VERTICAL));
        bPanel.add(Box.createHorizontalStrut(3));

        bPanel.add(new JLabel(" check:"));
        bPanel.add(checkIdTextField);

        bPanel.add(Box.createHorizontalStrut(7));
        bPanel.add(new JSeparator(SwingConstants.VERTICAL));
        bPanel.add(Box.createHorizontalStrut(2));

        bPanel.add(new JLabel(" x,y:"));
        bPanel.add(xyTextField);

        bPanel.setPreferredSize(new Dimension(940, 20));

        bottomPanel.add(bPanel, BorderLayout.WEST);

        centerPanel.setDividerLocation(0.99);

        JFrame frame = new JFrame("Viewer " + getViewerID() + " (" + model.getAllEntities().size() + " entities)");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack();

        if (!showInLaunch) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        }

        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerPanel.setDividerLocation(0.8);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
//                System.exit(0);
            }
        });
        viewer.addViewListener(new ViewListener() {

            @Override
            public void objectsClicked(ViewComponent view, List<RenderedObject> objects) {

                if (objects.isEmpty()) {
                    StaticViewProperties.selectedObject = null;
                    selectedObject = null;
                    CHECK_ID = new EntityID(0);
                    CHECK_ID2 = new EntityID(0);
                    selectObject(null);
                    viewer.repaint();
                }
                if (objects.size() == 1) {
                    StandardEntity entity = (StandardEntity) objects.get(0).getObject();
                    selectObject(entity.getID().getValue());
                } else {
                    int c = 0;
                    int id = -1;
                    popup.removeAll();
                    for (RenderedObject next : objects) {
                        StandardEntity entity = (StandardEntity) next.getObject();
                        if (entity instanceof Human) {
                            c++;
                            if (c == 1) {
                                id = entity.getID().getValue();
                            }
                        }
                        JMenuItem jmi = new JMenuItem(entity.getID().getValue() + "-" + entity);
                        jmi.addActionListener(new ActionListener() {
                            //                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {

                                JMenuItem source = (JMenuItem) actionEvent.getSource();
                                StringTokenizer st = new StringTokenizer(source.getText(), "-");
                                String menuID = st.nextToken();
                                int id = Integer.parseInt(menuID);
                                selectObject(id);
                            }
                        });
                        popup.add(jmi);
                    }

                    if (c != 1) {
                        double x = MouseInfo.getPointerInfo().getLocation().getX();
                        double y = MouseInfo.getPointerInfo().getLocation().getY();

                        popup.show(viewer, (int) Math.round(x), (int) Math.round(y));
                    } else if (id != -1) {
                        selectObject(id);
                    }
                }
            }

            //            @Override
            public void objectsRollover(ViewComponent view, List<RenderedObject> objects) {
            }
        });


//        tree.addTreeSelectionListener(new TreeSelectionListener() {
//            public void valueChanged(TreeSelectionEvent e) {
//                try {
//                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//                    /* if nothing is selected */
//                    if (node == rootNode) {
//                        selectedTreeObject = null;
//                        prepareExtraDataPanel();
//                        return;
//                    }
//                    selectedTreeObject = null;
//                    if (node == null)
//                        return;
//
//                    selectedTreeObject = node.getUserObject();
//                    expandNode(node);
//                    treeNodes = node.getPath();
//                    tree.updateUI();
//                    repaintSelectedObject();
//                } catch (Exception ignore) {
//                }
//            }
//        });
//
//        tree.addMouseListener(new MouseListener() {
//
//            public void mouseClicked(MouseEvent e) {
//                try {
//                    if (e.getButton() == MouseEvent.BUTTON3) {
////                        updateDataObject();
//                        prepareExtraDataPanel();
//                        treeNodes = null;
//                        StaticViewProperties.objectToPaint.clear();
//                    } else {
//                        if (e.getClickCount() == 2) {
//                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//                            if (node != null)
//                                if (!tree.isCollapsed(new TreePath(node.getPath()))) {
//                                    tree.expandPath(new TreePath(node.getPath()));
//                                } else {
//                                    tree.collapsePath(new TreePath(node.getPath()));
//                                }
//                        } else {
//                            StaticViewProperties.objectToPaint.clear();
//                        }
//                    }
//                } catch (Exception ex) {
////                    ex.printStackTrace();
//                }
//            }
//
//            public void mousePressed(MouseEvent e) {
//            }
//
//            public void mouseReleased(MouseEvent e) {
//            }
//
//            public void mouseEntered(MouseEvent e) {
//            }
//
//            public void mouseExited(MouseEvent e) {
//            }
//        });

    }

    private void addAllInCombo(JComboBox<StandardEntity> combo, List<StandardEntity> items) {
        for (StandardEntity entity : items) {
            combo.addItem(entity);
        }
    }

    protected void handleTimeStepProcess(final KVTimestep t) {
        if (pause)
            return;
//        Collection<StandardEntity> buildings=model.getEntitiesOfType(StandardEntityURN.BUILDING);
//
//        for(StandardEntity entity: buildings){
//            Building building= (Building) entity;
//            building.setFieryness(0);
//        }
//        for(Integer i:dataMap.keySet() ){
//
//            if(i<t.getTime()){
//                super.handleTimestep(dataMap.get(i));
//            }else{
//                break;
//            }
//        }
        super.handleTimestep(t);


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                time = t.getTime();
                if (worldTime < time) {
                    worldTime = time;
                    worldTimeLabel.setText("/" + worldTime + "   ");
                }
                timeLabel.setText("Time: " + t.getTime());
                scoreValue = format.format(scoreFunction.score(model, new Timestep(t.getTime())));
                scoreLabel.setText("    Score: " + scoreValue);
                if (selectedObject != null && shouldUpdateAgentData) {
                    selectObject(selectedObject.getID().getValue());
                } else {
                    selectObject(null);
                }
                viewer.view(model, t.getCommands());
//                if (t.getTime() == 300) {
//                printOutCivilianInformations();
//                }

                viewer.repaint();
            }
        });
    }

//    LearnerIO learnerIO_Log = new LearnerIO("data/ambulance/" + "Test" + ".dta", true);
//
//    private void printOutCivilianInformations() {
//        long totalHp = 0;
//        int totalRescuedCivs = 0;
//        String data = "";
//        String filename = "data/ambulance/" + "Test" + ".dta";
//        String filename_Log = "data/ambulance/" + "Test" + "_Market_Log.dta";
//        Civilian civilian;
//        for (StandardEntity st : model.getEntitiesOfType(StandardEntityURN.CIVILIAN)) {
//            civilian = (Civilian) st;
//            if (civilian.getPosition(model) instanceof Refuge) {
//                totalHp += civilian.getHP();
//                totalRescuedCivs++;
//            }
//        }
//
//        data += " " + time + " " + totalRescuedCivs + " " + totalHp + "              " + scoreValue;
////        if (time == 300) {
////            LearnerIO learnerIO = new LearnerIO("data/ambulance/" + "Test" + ".dta", true);
////            learnerIO.simplePrintToFile(filename, " " + totalRescuedCivs + " " + totalHp + "              " + scoreValue);
////        }
//
//        if (time == 1) {
//
//            learnerIO_Log.simplePrintToFile(filename, "___________________________________________________\n");
//
//        }
//        learnerIO_Log.simplePrintToFile(filename, data);
//
//
//    }

    @Override
    protected void handleTimestep(final KVTimestep t) {
        dataMap.put(t.getTime(), t);
        if (!isSetToManual) {
            handleTimeStepProcess(t);
        } else {
            KVTimestep timestep = dataMap.get(++currentTime);
            if (timestep != null)
                handleTimeStepProcess(timestep);
        }
    }

    protected void manualTimeStep(int timeStep) {
        KVTimestep kvTimestep = dataMap.get(timeStep);
        if (kvTimestep != null) {
            currentTime = timeStep - 1;
            isSetToManual = true;
            handleTimestep(kvTimestep);
        } else {
            Logger.error("Null KVTimeStep");
        }
    }


    protected void manualNextTimeStep() {
        manualTimeStep(++time);
    }

    @Override
    public String toString() {
        return "MRLs viewer";
    }


    private void prepareExtraDataPanel() {
        if (selectedObject == null) {
            return;
        }
        rootNode.removeAllChildren();

        tree.updateUI();
//        paintField.updateUI();
    }


    private DefaultMutableTreeNode processHierarchy(List<ViewLayer> hierarchy) {
        DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode("PROPERTIES");
        DefaultMutableTreeNode parent;
        DefaultMutableTreeNode child;

        for (ViewLayer next : viewer.getLayers()) {
            Action action = viewer.getLayersActions().get(next);
            JMenuItem menuItem = new JMenuItem(action);
            parent = new DefaultMutableTreeNode(new LayerComponent(next, menuItem, true));
            mainNode.add(parent);

            List<JMenuItem> items = next.getPopupMenuItems();
            if (items != null && !items.isEmpty()) {
                for (JMenuItem item : items) {
                    child = new DefaultMutableTreeNode(new LayerComponent(next, item, false));
                    parent.add(child);
                }
            }
        }

        return mainNode;
    }

    private ScoreFunction makeScoreFunction() {
        String className = config.getValue(Constants.SCORE_FUNCTION_KEY);
        ScoreFunction result = instantiate(className, ScoreFunction.class);
        result.initialise(model, config);
        return result;
    }

    public JPanel getTopPanel() {
        return topPanel;
    }

    public JPanel getBottomPanel() {
        return bottomPanel;
    }

    public MrlAnimatedWorldModelViewer getViewerPanel() {
        return viewer;
    }

    public JTable getAgentPropPanel() {
        return propertiesTable;
    }

    public JComboBox<StandardEntity> getAgentSelPanel() {
        return agentCombo;
    }

    public JTree getLayerConPanel() {
        return layerTree;
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }
}
