package com.mrl.debugger.layers.base;

import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.ViewLayer;
import math.geom2d.conic.Circle2D;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.view.StandardEntityViewLayer;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MrlBaseAreaLayer<E extends Area> extends StandardEntityViewLayer<E> implements MrlBaseLayer {

    protected static final Color OUTLINE_COLOUR = Color.GRAY.darker().darker();

    protected Map<EntityID, java.util.List<Integer>> agentEntitiesMap = new HashMap<>();

    private boolean drawOverAllData = false;
    private Set<Integer> overallEntities = new HashSet<>();

    public void bind(EntityID id, int key, Object... params) {
        if (key == 0) {
            java.util.List<Integer> data = (java.util.List<Integer>) params[0];
            agentEntitiesMap.put(id, data);
            if (drawOverAllData) {
                overallEntities.clear();
                for (java.util.List<Integer> agentData : agentEntitiesMap.values()) {
                    overallEntities.addAll(agentData);
                }
            }
        }
    }

    protected MrlBaseAreaLayer(Class<E> clazz) {
        super(clazz);
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            drawOverAllData = annotation.drawAllData();
        }
    }

    @Override
    public Shape render(E area, Graphics2D g, ScreenTransform t) {
        java.util.List<Edge> edges = area.getEdges();
        if (edges.isEmpty()) {
            return null;
        }

        int count = edges.size();
        int[] xs = new int[count];
        int[] ys = new int[count];
        int i = 0;
        for (Edge e : edges) {
            xs[i] = t.xToScreen(e.getStartX());
            ys[i] = t.yToScreen(e.getStartY());
            ++i;
        }
        Polygon shape = new Polygon(xs, ys, count);

        paintShape(area, shape, g);

        if (drawOverAllData
                && (StaticViewProperties.selectedObject == null || !agentEntitiesMap.containsKey(StaticViewProperties.selectedObject.getID()))) {
            if (overallEntities.contains(area.getID().getValue())) {
                paintData(area, shape, g);
            }
        } else if (StaticViewProperties.selectedObject != null
                && agentEntitiesMap.containsKey(StaticViewProperties.selectedObject.getID())
                && agentEntitiesMap.get(StaticViewProperties.selectedObject.getID()).contains(area.getID().getValue())) {
            paintData(area, shape, g);
        }

        if (area.equals(StaticViewProperties.selectedObject)) {
            Circle2D circle2D = new Circle2D(t.xToScreen(area.getX()), t.yToScreen(area.getY()), 18d);
            circle2D.draw(g);
        }

        for (Edge edge : edges) {
            paintEdge(edge, g, t);
        }
        return shape;
    }

    @Override
    public String getName() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            return annotation.caption();
        } else {
            return "null";
        }
    }

    /**
     * Paint an individual edge.
     *
     * @param e The edge to paint.
     * @param g The graphics to paint on.
     * @param t The screen transform.
     */
    protected void paintEdge(Edge e, Graphics2D g, ScreenTransform t) {
    }

    /**
     * Paint the overall shape.
     *
     * @param area The area.
     * @param p    The overall polygon.
     * @param g    The graphics to paint on.
     */
    protected void paintShape(E area, Polygon p, Graphics2D g) {
    }

    /**
     * Paint the overall shape which contains in data retrieved from agents.
     *
     * @param area The area.
     * @param p    The overall polygon.
     * @param g    The graphics to paint on.
     */
    protected void paintData(E area, Polygon p, Graphics2D g) {
    }

}
