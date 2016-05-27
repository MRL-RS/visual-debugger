package com.mrl.debugger.layers.base;

import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.ViewLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.view.StandardViewLayer;
import rescuecore2.view.RenderedObject;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Mahdi
 */
public abstract class MrlBaseShapeLayer<T extends Shape> extends StandardViewLayer implements MrlBaseLayer {

    private static final Stroke STROKE_DEFAULT = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    private boolean drawOverAllData = false;

    protected Map<EntityID, List<T>> agentShapesMap = new HashMap<>();
    private Set<T> overallShapes = new HashSet<>();

    public MrlBaseShapeLayer() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            drawOverAllData = annotation.drawAllData();
        }
    }

    @Override
    public void bind(EntityID id, int key, Object... params) {
        if (key == 0) {
            java.util.List<T> data = (List<T>) params[0];
            agentShapesMap.put(id, data);
            if (drawOverAllData) {
                overallShapes.clear();
                for (java.util.List<T> agentData : agentShapesMap.values()) {
                    overallShapes.addAll(agentData);
                }
            }
        }
    }


    @Override
    public Collection<RenderedObject> render(Graphics2D g, ScreenTransform t, int width, int height) {

        g.setStroke(STROKE_DEFAULT);
        if (drawOverAllData
                && (StaticViewProperties.selectedObject == null || !agentShapesMap.containsKey(StaticViewProperties.selectedObject.getID()))) {
            for (T p : overallShapes) {
                paintShape(transform(p, t), g);
            }
        } else if (StaticViewProperties.selectedObject != null
                && agentShapesMap.containsKey(StaticViewProperties.selectedObject.getID())) {
            List<T> polygons = agentShapesMap.get(StaticViewProperties.selectedObject.getID());
            for (T p : polygons) {
                paintShape(transform(p, t), g);
            }
        }

        return new ArrayList<>();
    }


    protected abstract T transform(T p, ScreenTransform t);

    protected abstract void paintShape(T p, Graphics2D g);

    @Override
    public String getName() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            return annotation.caption();
        } else {
            return "Unknown";
        }
    }


}
