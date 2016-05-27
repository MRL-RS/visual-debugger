package com.mrl.debugger.layers.base;

import com.mrl.debugger.MrlConstants;
import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.ViewLayer;
import rescuecore2.config.Config;
import rescuecore2.misc.Pair;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.*;
import rescuecore2.view.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.*;

/**
 * @author Mahdi
 */
@ViewLayer(caption = "Human info", visible = false)
public class MrlBaseHumanInfoLayer extends MrlBaseHumanLayer {

    private static final int DEFAULT_ICON_SIZE = 32;

    private static final int HP_MAX = 10000;
    private static final int HP_INJURED = 7500;
    private static final int HP_CRITICAL = 1000;

    private static final int SIZE = 10;

    private static final Stroke STROKE_DEFAULT = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

    private static final Color CIVILIAN_COLOUR = Color.GREEN;
    private static final Color FIRE_BRIGADE_COLOUR = Color.RED;
    private static final Color POLICE_FORCE_COLOUR = Color.BLUE;
    private static final Color AMBULANCE_TEAM_COLOUR = Color.WHITE;
    private static final Color DEAD_COLOUR = Color.YELLOW.brighter();

    private static final HumanSorter HUMAN_SORTER = new HumanSorter();

    private static int SAY_RANGE;
    private static int VIEW_RANGE;
    private static int CLEAR_RANGE;
    private static int AGENT_SIZE = 500;
    private static int EXTINGUISH_RANGE;
    private int iconSize;

    private boolean useIcons;
    private Map<String, Map<State, Icon>> icons;
    private Action useIconsAction;

    private boolean atInfo;
    private RenderATInfoAction atInfoAction;
    private boolean fbInfo;
    private RenderFBInfoAction fbInfoAction;
    private boolean pfInfo;
    private RenderPFInfoAction pfInfoAction;
    private boolean civInfo;
    private RenderCivInfoAction civInfoAction;

    private boolean sayRange;
    private SayRangeAction sayRangeAction;

    private boolean viewRange;
    private ViewRangeAction viewRangeAction;

    private boolean clearRange;
    private ClearRangeAction clearRangeAction;

    private boolean agentSize;
    private AgentSizeAction agentSizeAction;

    private boolean agentLocation;
    private AgentLocationAction agentLocationAction;

    private boolean extinguishRange;
    private ExtinguishRangeAction extinguishRangeAction;


    @Override
    public void initialise(Config config) {
        iconSize = config.getIntValue(MrlConstants.ICON_SIZE_KEY, DEFAULT_ICON_SIZE);
        useIcons = config.getBooleanValue(MrlConstants.USE_ICONS_KEY, false);
        SAY_RANGE = config.getIntValue(MrlConstants.VOICE_RANGE_KEY);
        VIEW_RANGE = config.getIntValue(MrlConstants.MAX_VIEW_DISTANCE_KEY);
        CLEAR_RANGE = config.getIntValue(MrlConstants.MAX_CLEAR_DISTANCE_KEY);
        EXTINGUISH_RANGE = config.getIntValue(MrlConstants.MAX_EXTINGUISH_DISTANCE_KEY);

        icons = new HashMap<String, Map<State, Icon>>();
        icons.put(StandardEntityURN.FIRE_BRIGADE.toString(), generateIconMap("FireBrigade"));
        icons.put(StandardEntityURN.AMBULANCE_TEAM.toString(), generateIconMap("AmbulanceTeam"));
        icons.put(StandardEntityURN.POLICE_FORCE.toString(), generateIconMap("PoliceForce"));
        icons.put(StandardEntityURN.CIVILIAN.toString() + "-Male", generateIconMap("Civilian-Male"));
        icons.put(StandardEntityURN.CIVILIAN.toString() + "-Female", generateIconMap("Civilian-Female"));
        useIconsAction = new UseIconsAction();


        atInfo = false;
        atInfoAction = new RenderATInfoAction();
        pfInfo = false;
        pfInfoAction = new RenderPFInfoAction();
        fbInfo = false;
        fbInfoAction = new RenderFBInfoAction();
        civInfo = false;
        civInfoAction = new RenderCivInfoAction();
        sayRange = false;
        sayRangeAction = new SayRangeAction();
        viewRange = false;
        viewRangeAction = new ViewRangeAction();
        clearRange = false;
        clearRangeAction = new ClearRangeAction();
        agentSize = false;
        agentSizeAction = new AgentSizeAction();
        extinguishRange = false;
        extinguishRangeAction = new ExtinguishRangeAction();
        agentLocation = true;
        agentLocationAction = new AgentLocationAction();
    }

    @Override
    public String getName() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            return annotation.caption();
        } else {
            return "Human";
        }
    }

    @Override
    public Shape render(Human h, Graphics2D g, ScreenTransform t) {
        Pair<Integer, Integer> location = getLocation(h);
        if (location == null) {
            return null;
        }

        if (atInfo) {
            renderATAction(h, g, t, location);
        }
        if (fbInfo) {
            renderFBAction(h, g, t, location);
        }
        if (pfInfo) {
            renderPFAction(h, g, t, location);
        }
        if (civInfo) {
            renderCivAction(h, g, t, location);
        }

        if (h.isPositionDefined() && (world.getEntity(h.getPosition()) instanceof AmbulanceTeam)) {
            return null;
        }


        int x = t.xToScreen(location.first());
        int y = t.yToScreen(location.second());
        Shape shape;
        Icon icon = useIcons ? getIcon(h) : null;

        if (icon == null) {
//            shape = new Ellipse2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);
            if (h == StaticViewProperties.selectedObject) {
                if (sayRange) {
                    g.setColor(Color.GREEN);
                    renderSayRange(g, t, h.getLocation(world));
                    g.setColor(Color.MAGENTA);
                }
                if (viewRange) {
                    g.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                    g.setColor(new Color(0, 128, 255));
                    renderViewRange(g, t, h.getLocation(world));
                    g.setStroke(STROKE_DEFAULT);
                    g.setColor(Color.MAGENTA);
                }
                if (agentSize) {
                    g.setColor(Color.YELLOW);
                    renderAgentSize(g, t, h.getLocation(world));
                    g.setColor(Color.MAGENTA);
                }
                if (clearRange && h instanceof PoliceForce) {
                    g.setColor(Color.blue);
                    renderClearRange(g, t, h.getLocation(world));
                    g.setColor(Color.MAGENTA);
                }
                if (extinguishRange && h instanceof FireBrigade) {
                    g.setColor(Color.blue);
                    renderExtinguishRange(g, t, h.getLocation(world));
                    g.setColor(Color.MAGENTA);
                }

                if (agentLocation) {
                    defaultCircle(h, g, t);
                }
            } else {
                g.setColor(adjustColour(getColour(h), h.getHP()));
            }
//            g.fill(shape);

        } else {
            x -= icon.getIconWidth() / 2;
            y -= icon.getIconHeight() / 2;
            shape = new Rectangle2D.Double(x, y, icon.getIconWidth(), icon.getIconHeight());
            icon.paintIcon(null, g, x, y);
        }


        return null;
    }


    private void renderATAction(Human human, Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location) {

        if (human instanceof AmbulanceTeam) {
            String strID = human.getID().toString();
            g.setColor(Color.WHITE.brighter());
            drawInfo(g, t, strID, location, -13, 25);
//            String s = String.valueOf(MrlPlatoonAgent.VALUE_FOR_DISPLAY_IN_VIEWER.get(human.getID()));
            String s = "null";
            if (human.isBuriednessDefined()) {
                s = String.valueOf(human.getBuriedness());
            }
            if (!s.equals("null")) {
                drawInfo(g, t, s, location, -16, -15);
            }
        }
    }

    private void renderFBAction(Human human, Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location) {

        if (human instanceof FireBrigade) {
            String strID = human.getID().toString();
            g.setColor(Color.RED.darker());
            drawInfo(g, t, strID, location, -13, 25);
//            String s = String.valueOf(MrlPlatoonAgent.VALUE_FOR_DISPLAY_IN_VIEWER.get(human.getID()));
            String s = "null";
            if (human.isBuriednessDefined()) {
                s = String.valueOf(human.getBuriedness());
            }
            if (!s.equals("null")) {
                drawInfo(g, t, s, location, -16, -15);
            }
        }
    }

    private void renderPFAction(Human human, Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location) {

        if (human instanceof PoliceForce) {
            String strID = human.getID().toString();
            g.setColor(Color.BLUE.brighter());
            drawInfo(g, t, strID, location, -13, 25);
            String s = "null";
            if (human.isBuriednessDefined()) {
                s = String.valueOf(human.getBuriedness());
            }
//            String s = String.valueOf(MrlPlatoonAgent.VALUE_FOR_DISPLAY_IN_VIEWER.get(human.getID()));
            if (!s.equals("null")) {
                drawInfo(g, t, s, location, -16, -15);
            }
        }
    }

    private void renderCivAction(Human human, Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location) {
        if (human instanceof Civilian) {
            String strID = human.getID().toString();
            String strHP = String.valueOf(human.getHP());
            String strDMG = String.valueOf(human.getDamage());
            String strBRD = String.valueOf(human.getBuriedness());
            g.setColor(Color.GREEN);
            drawInfo(g, t, strID, location, -13, 15);
            drawInfo(g, t, strHP, location, -16, -7);
            drawInfo(g, t, strDMG, location, 6, 5);
            drawInfo(g, t, strBRD, location, -25, 5);
        }
    }

    private void drawInfo(Graphics2D g, ScreenTransform t, String strInfo, Pair<Integer, Integer> location, int changeXPos, int changeYPos) {
        int x;
        int y;
        if (strInfo != null) {
            x = t.xToScreen(location.first());
            y = t.yToScreen(location.second());
            g.drawString(strInfo, x + changeXPos, y + changeYPos);
        }
    }

    private void renderSayRange(Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location) {
        drawCircle(g, t, location, SAY_RANGE);
    }

    private void renderViewRange(Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location) {
        drawCircle(g, t, location, VIEW_RANGE);
    }

    private void renderClearRange(Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location) {
        drawCircle(g, t, location, CLEAR_RANGE);
    }

    private void renderAgentSize(Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location) {
        drawCircle(g, t, location, AGENT_SIZE);
    }

    private void renderExtinguishRange(Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location) {
        drawCircle(g, t, location, EXTINGUISH_RANGE);
    }



    private Icon getIcon(Human h) {
        State state = getState(h);
        Map<State, Icon> iconMap;
        switch (h.getStandardURN()) {
            case CIVILIAN:
                boolean male = h.getID().getValue() % 2 == 0;
                if (male) {
                    iconMap = icons.get(StandardEntityURN.CIVILIAN.toString() + "-Male");
                } else {
                    iconMap = icons.get(StandardEntityURN.CIVILIAN.toString() + "-Female");
                }
                break;
            default:
                iconMap = icons.get(h.getStandardURN().toString());
        }
        if (iconMap == null) {
            return null;
        }
        return iconMap.get(state);
    }


    private Map<State, Icon> generateIconMap(String type) {
        Map<State, Icon> result = new EnumMap<State, Icon>(State.class);
        for (State state : State.values()) {
            String resourceName = "rescuecore2/standard/view/" + type + "-" + state.toString() + "-" + iconSize + "x" + iconSize + ".png";
            URL resource = MrlBaseHumanInfoLayer.class.getClassLoader().getResource(resourceName);
            if (resource == null) {
//                Log.warn("Couldn't find resource: " + resourceName);
            } else {
                result.put(state, new ImageIcon(resource));
            }
        }
        return result;
    }

    private State getState(Human h) {
        int hp = h.getHP();
        if (hp <= 0) {
            return State.DEAD;
        }
        if (hp <= HP_CRITICAL) {
            return State.CRITICAL;
        }
        if (hp <= HP_INJURED) {
            return State.INJURED;
        }
        return State.HEALTHY;
    }

    private enum State {
        HEALTHY {
            @Override
            public String toString() {
                return "Healthy";
            }
        },
        INJURED {
            @Override
            public String toString() {
                return "Injured";
            }
        },
        CRITICAL {
            @Override
            public String toString() {
                return "Critical";
            }
        },
        DEAD {
            @Override
            public String toString() {
                return "Dead";
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////ACTIONS
    private final class UseIconsAction extends AbstractAction {
        public UseIconsAction() {
            super("Use icons");
            putValue(Action.SELECTED_KEY, useIcons);
            putValue(Action.SMALL_ICON, useIcons ? Icons.TICK : Icons.CROSS);
        }

        public void actionPerformed(ActionEvent e) {
            useIcons = !useIcons;
            putValue(Action.SELECTED_KEY, useIcons);
            putValue(Action.SMALL_ICON, useIcons ? Icons.TICK : Icons.CROSS);
            component.repaint();
        }
    }

    private final class RenderATInfoAction extends AbstractAction {
        public RenderATInfoAction() {
            super("Ambulance Team Info");
            update();
        }

        public void setATRenderInfo(boolean render) {
            atInfo = render;
            atInfoAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setATRenderInfo(!atInfo);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, atInfo);
            putValue(Action.SMALL_ICON, atInfo ? Icons.TICK : Icons.CROSS);
        }
    }

    private final class RenderFBInfoAction extends AbstractAction {
        public RenderFBInfoAction() {
            super("Fire Brigade Info");
            update();
        }

        public void setFBRenderInfo(boolean render) {
            fbInfo = render;
            fbInfoAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setFBRenderInfo(!fbInfo);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, fbInfo);
            putValue(Action.SMALL_ICON, fbInfo ? Icons.TICK : Icons.CROSS);
        }
    }

    private final class RenderPFInfoAction extends AbstractAction {
        public RenderPFInfoAction() {
            super("Police Force Info");
            update();
        }

        public void setPFRenderInfo(boolean render) {
            pfInfo = render;
            pfInfoAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setPFRenderInfo(!pfInfo);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, Boolean.valueOf(pfInfo));
            putValue(Action.SMALL_ICON, pfInfo ? Icons.TICK : Icons.CROSS);
        }
    }

    private final class RenderCivInfoAction extends AbstractAction {
        public RenderCivInfoAction() {
            super("Civilian Info");
            update();
        }

        public void setCivRenderInfo(boolean render) {
            civInfo = render;
            civInfoAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setCivRenderInfo(!civInfo);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, Boolean.valueOf(civInfo));
            putValue(Action.SMALL_ICON, civInfo ? Icons.TICK : Icons.CROSS);
        }
    }

    private final class SayRangeAction extends AbstractAction {
        public SayRangeAction() {
            super("Say Range");
            update();
        }

        public void setSayRange(boolean render) {
            sayRange = render;
            sayRangeAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setSayRange(!sayRange);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, Boolean.valueOf(sayRange));
            putValue(Action.SMALL_ICON, sayRange ? Icons.TICK : Icons.CROSS);
        }
    }

    private final class ViewRangeAction extends AbstractAction {
        public ViewRangeAction() {
            super("view Range");
            update();
        }

        public void setViewRange(boolean render) {
            viewRange = render;
            viewRangeAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setViewRange(!viewRange);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, Boolean.valueOf(viewRange));
            putValue(Action.SMALL_ICON, viewRange ? Icons.TICK : Icons.CROSS);
        }
    }

    private final class ClearRangeAction extends AbstractAction {
        public ClearRangeAction() {
            super("Clear Range");
            update();
        }

        public void setClearRange(boolean render) {
            clearRange = render;
            clearRangeAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setClearRange(!clearRange);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, Boolean.valueOf(clearRange));
            putValue(Action.SMALL_ICON, clearRange ? Icons.TICK : Icons.CROSS);
        }
    }

    private final class ExtinguishRangeAction extends AbstractAction {

        public ExtinguishRangeAction() {
            super("Extinguish Range");
            update();
        }

        public void setExtinguishRange(boolean render) {
            extinguishRange = render;
            extinguishRangeAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setExtinguishRange(!extinguishRange);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, Boolean.valueOf(extinguishRange));
            putValue(Action.SMALL_ICON, extinguishRange ? Icons.TICK : Icons.CROSS);
        }
    }

    private final class AgentSizeAction extends AbstractAction {
        public AgentSizeAction() {
            super("Agent Size");
            update();
        }

        public void setAgentSize(boolean render) {
            agentSize = render;
            agentSizeAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setAgentSize(!agentSize);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, Boolean.valueOf(agentSize));
            putValue(Action.SMALL_ICON, agentSize ? Icons.TICK : Icons.CROSS);
        }
    }

    private final class AgentLocationAction extends AbstractAction {
        public AgentLocationAction() {
            super("Agent Location");
            update();
        }

        public void setAgentLocation(boolean render) {
            agentLocation = render;
            agentLocationAction.update();
        }

        public void actionPerformed(ActionEvent e) {
            setAgentLocation(!agentLocation);
            component.repaint();
        }

        void update() {
            putValue(Action.SELECTED_KEY, Boolean.valueOf(agentLocation));
            putValue(Action.SMALL_ICON, agentLocation ? Icons.TICK : Icons.CROSS);
        }
    }

    @Override
    public java.util.List<JMenuItem> getPopupMenuItems() {
        java.util.List<JMenuItem> result = new ArrayList<JMenuItem>();
        result.add(new JMenuItem(useIconsAction));
        result.add(new JMenuItem("   ----------INFO----------"));
        result.add(new JMenuItem(atInfoAction));
        result.add(new JMenuItem(fbInfoAction));
        result.add(new JMenuItem(pfInfoAction));
        result.add(new JMenuItem(civInfoAction));
        result.add(new JMenuItem(sayRangeAction));
        result.add(new JMenuItem(viewRangeAction));
        result.add(new JMenuItem(clearRangeAction));
        result.add(new JMenuItem(agentSizeAction));
        result.add(new JMenuItem(extinguishRangeAction));
        result.add(new JMenuItem(agentLocationAction));

        return result;
    }

    private static final class HumanSorter implements Comparator<Human>, java.io.Serializable {
        public int compare(Human h1, Human h2) {
            if (h1 instanceof Civilian && !(h2 instanceof Civilian)) {
                return -1;
            }
            if (h2 instanceof Civilian && !(h1 instanceof Civilian)) {
                return 1;
            }
            return h1.getID().getValue() - h2.getID().getValue();
        }
    }

    @Override
    protected void postView() {
        Collections.sort(entities, HUMAN_SORTER);
    }


    /**
     * Get the location of a human.
     *
     * @param h The human to look up.
     * @return The location of the human.
     */
    protected Pair<Integer, Integer> getLocation(Human h) {
        return h.getLocation(world);
    }


}
