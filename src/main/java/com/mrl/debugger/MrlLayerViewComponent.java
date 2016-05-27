package com.mrl.debugger;

import rescuecore2.config.Config;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.view.AreaNeighboursLayer;
import rescuecore2.view.Icons;
import rescuecore2.view.LayerViewComponent;
import rescuecore2.view.RenderedObject;
import rescuecore2.view.ViewLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import static rescuecore2.standard.view.StandardViewLayer.VISIBILITY_SUFFIX;
import static rescuecore2.standard.view.StandardWorldModelViewer.STANDARD_VIEWER_PREFIX;

/**
 * Created by Mostafa Shabani.
 * Date: Jun 23, 2011
 * Time: 11:16:18 PM
 */
public class MrlLayerViewComponent extends LayerViewComponent {

    public static final Color VIEWER_BACKGROUND_COLOR = new Color(195, 176, 145);

    private Config config;
    private List<ViewLayer> layers;
    private Map<ViewLayer, Action> layerActions;
    private Object[] data;
    private Rectangle2D bounds;

    /**
     * Construct a new LayerViewComponent.
     */
    public MrlLayerViewComponent() {
        super();
        setBackground(VIEWER_BACKGROUND_COLOR);
        layers = new ArrayList<ViewLayer>();
        layerActions = new HashMap<ViewLayer, Action>();
        addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    @Override
    public void initialise(Config c) {
        super.initialise(c);
        this.config = c;
        for (ViewLayer next : layers) {
            com.mrl.debugger.ViewLayer annotation = next.getClass().getAnnotation(com.mrl.debugger.ViewLayer.class);
            String visibleKey = STANDARD_VIEWER_PREFIX + "." + next.getClass().getSimpleName() + "." + VISIBILITY_SUFFIX;
            if (annotation != null) {
                config.setBooleanValue(visibleKey, annotation.visible());
            }
            next.initialise(config);
        }
    }

    /**
     * Add a view layer.
     *
     * @param layer The layer to add.
     */
    public void addLayer(ViewLayer layer) {
        super.addLayer(layer);
        layers.add(layer);
        layer.setLayerViewComponent(this);
        layerActions.put(layer, new LayerAction(layer));
        if (config != null) {
            layer.initialise(config);
        }
        computeBounds();
    }

    /**
     * Remove a view layer.
     *
     * @param layer The layer to remove.
     */
    public void removeLayer(ViewLayer layer) {
        super.removeLayer(layer);
        int index = layers.indexOf(layer);
        if (index != -1) {
            layers.remove(index);
            layerActions.remove(layer);
            layer.setLayerViewComponent(null);
            computeBounds();
        }
    }

    /**
     * Remove all view layers.
     */
    public void removeAllLayers() {
        super.removeAllLayers();
        for (ViewLayer next : layers) {
            next.setLayerViewComponent(null);
        }
        layers.clear();
        layerActions.clear();
        computeBounds();
    }

    @Override
    public void view(Object... objects) {
        data = objects;
        computeBounds();
        super.view(objects);
    }

    @Override
    protected Collection<RenderedObject> render(Graphics2D g, ScreenTransform transform, int width, int height) {
        Collection<RenderedObject> result = new HashSet<RenderedObject>();
        prepaint();
        for (ViewLayer next : layers) {
            if (next.isVisible()) {
                Graphics2D copy = (Graphics2D) g.create();
                result.addAll(next.render(copy, transform, width, height));
            }
        }
        postpaint();
        return result;
    }

    /**
     * Get all installed layers.
     *
     * @return All installed layers.
     */
    public List<ViewLayer> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    protected Map<ViewLayer, Action> getLayersActions() {
        return Collections.unmodifiableMap(layerActions);
    }

    /**
     * Do whatever needs doing before the layers are painted. The default implementation does nothing.
     */
    protected void prepaint() {
    }

    /**
     * Do whatever needs doing after the layers are painted. The default implementation does nothing.
     */
    protected void postpaint() {
    }

    private void computeBounds() {
        Rectangle2D oldBounds = bounds;
        bounds = null;
        for (ViewLayer next : layers) {
            expandBounds(next.view(data));
        }
        if (bounds == null) {
            updateBounds(0, 0, 1, 1);
        } else if (oldBounds == null
                || oldBounds.getMinX() != bounds.getMinX()
                || oldBounds.getMinY() != bounds.getMinY()
                || oldBounds.getMaxX() != bounds.getMaxX()
                || oldBounds.getMaxY() != bounds.getMaxY()) {
            updateBounds(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
        }
    }

    private void expandBounds(Rectangle2D next) {
        if (next == null) {
            return;
        }
        if (bounds == null) {
            bounds = next;
        } else {
            Rectangle2D.union(bounds, next, bounds);
        }
    }

    private void showPopupMenu(int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        for (ViewLayer next : layers) {
            Action action = layerActions.get(next);
            JMenu layerMenu = new JMenu(next.getName());
            layerMenu.add(new JMenuItem(action));
            if (next.isVisible()) {
                List<JMenuItem> items = next.getPopupMenuItems();
                if (items != null && !items.isEmpty()) {
                    layerMenu.addSeparator();
                    for (JMenuItem item : items) {
                        layerMenu.add(item);
                    }
                }
            }
            menu.add(layerMenu);
        }
        menu.show(this, x, y);
    }

    public class LayerAction extends AbstractAction {
        private ViewLayer layer;

        public LayerAction(ViewLayer layer) {
//            super("Visible");
            super(layer.getName());
            this.layer = layer;
            if (layer instanceof AreaNeighboursLayer) {
                layer.setVisible(false);
            }
            putValue(Action.SELECTED_KEY, Boolean.valueOf(layer.isVisible()));
            putValue(Action.SMALL_ICON, layer.isVisible() ? Icons.TICK : Icons.CROSS);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean selected = ((Boolean) getValue(Action.SELECTED_KEY)).booleanValue();
            putValue(Action.SELECTED_KEY, Boolean.valueOf(!selected));
            putValue(Action.SMALL_ICON, !selected ? Icons.TICK : Icons.CROSS);
            layer.setVisible(!selected);
            MrlLayerViewComponent.this.repaint();
        }
    }
}
