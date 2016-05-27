package com.mrl.debugger.layers.base;

import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.Util;
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
public abstract class MrlBasePointLayer extends StandardViewLayer implements MrlBaseLayer {

    private static final Stroke STROKE_DEFAULT = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    private boolean drawAllData = false;

    protected Map<EntityID, List<Point>> agentPointsMap = new HashMap<>();
    private Set<Point> allPoints = new HashSet<>();

    public MrlBasePointLayer() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            drawAllData = annotation.drawAllData();
        }
    }

    @Override
    public void bind(EntityID id, int key, Object... params) {
        if (key == 0) {
            java.util.List<Point> data = (List<Point>) params[0];
            agentPointsMap.put(id, data);
            if (drawAllData) {
                allPoints.clear();
                for (java.util.List<Point> agentData : agentPointsMap.values()) {
                    allPoints.addAll(agentData);
                }
            }
        }
    }

    @Override
    public Collection<RenderedObject> render(Graphics2D g, ScreenTransform t, int width, int height) {

        g.setStroke(STROKE_DEFAULT);
        if (drawAllData
                && (StaticViewProperties.selectedObject == null || !agentPointsMap.containsKey(StaticViewProperties.selectedObject.getID()))) {
            for (Point p : allPoints) {
                paintPoint(Util.transform(p, t), g);
            }
        } else if (StaticViewProperties.selectedObject != null
                && agentPointsMap.containsKey(StaticViewProperties.selectedObject.getID())) {
            List<Point> polygons = agentPointsMap.get(StaticViewProperties.selectedObject.getID());
            for (Point p : polygons) {
                paintPoint(Util.transform(p, t), g);
            }
        }

        return new ArrayList<>();
    }


    protected abstract void paintPoint(Point p, Graphics2D g);

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
