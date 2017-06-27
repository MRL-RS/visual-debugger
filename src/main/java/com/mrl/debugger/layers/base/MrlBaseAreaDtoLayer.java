package com.mrl.debugger.layers.base;

import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.remote.dto.BuildingDto;
import com.mrl.debugger.remote.dto.StandardDto;
import math.geom2d.conic.Circle2D;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.view.StandardEntityViewLayer;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class MrlBaseAreaDtoLayer<E extends Area, K extends StandardDto> extends StandardEntityViewLayer<E> implements MrlBaseLayer {

    protected static final Color OUTLINE_COLOUR = Color.GRAY.darker().darker();

    protected Map<EntityID, Map<Integer, BuildingDto>> agentEntitiesMap = new HashMap<>();

    private boolean drawOverAllData = false;
    private Set<StandardDto> overallEntities = new HashSet<>();

    public void bind(EntityID id, int key, Object... params) {
        if (key == 0) {
            java.util.List<BuildingDto> data = (java.util.List<BuildingDto>) params[0];
            Map<Integer, BuildingDto> areaMap = new HashMap<>();

            data.forEach(buildingDto -> {
                areaMap.put(buildingDto.getId(), buildingDto);
            });
            agentEntitiesMap.put(id, areaMap);

//            agentEntitiesMap.put(id, data);
            if (drawOverAllData) {
                overallEntities.clear();
                for (Map<Integer, BuildingDto> agentData : agentEntitiesMap.values()) {
                    overallEntities.addAll(agentData.values());
                }
            }
        }
    }

    protected MrlBaseAreaDtoLayer(Class<E> clazz) {
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
            if (overallEntities.contains(new BuildingDto(area.getID().getValue()))) {
//                paintData(area, shape, g);
            }
        } else {
            if (StaticViewProperties.selectedObject != null) {
                Map<Integer, BuildingDto> maps = agentEntitiesMap.get(StaticViewProperties.selectedObject.getID());
                if (agentEntitiesMap.containsKey(StaticViewProperties.selectedObject.getID())
                        && maps.keySet().contains(area.getID().getValue())) {
                    BuildingDto buildingDto = maps.get(area.getID().getValue());
                    paintData(area, (K) buildingDto, shape, g);
                }
            }
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
    protected void paintData(E area, K dto, Polygon p, Graphics2D g) {
    }

}
