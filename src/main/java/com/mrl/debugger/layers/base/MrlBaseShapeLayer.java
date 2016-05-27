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
public abstract class MrlBaseShapeLayer extends StandardViewLayer implements MrlBaseLayer {

    private static final Stroke STROKE_DEFAULT = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    private boolean drawOverAllData = false;

    protected Map<EntityID, List<Polygon>> agentPolygonsMap = new HashMap<>();
    private Set<Polygon> overallPolygons = new HashSet<>();

    public MrlBaseShapeLayer() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            drawOverAllData = annotation.drawOverAllData();
        }
    }

    @Override
    public void bind(EntityID id, int key, Object... params) {
        if (key == 0) {
            java.util.List<Polygon> data = (List<Polygon>) params[0];
            agentPolygonsMap.put(id, data);
            if (drawOverAllData) {
                overallPolygons.clear();
                for (java.util.List<Polygon> agentData : agentPolygonsMap.values()) {
                    overallPolygons.addAll(agentData);
                }
            }
        }
    }


    @Override
    public Collection<RenderedObject> render(Graphics2D g, ScreenTransform t, int width, int height) {

        g.setStroke(STROKE_DEFAULT);
        if (drawOverAllData
                && (StaticViewProperties.selectedObject == null || !agentPolygonsMap.containsKey(StaticViewProperties.selectedObject.getID()))) {
            for (Polygon p : overallPolygons) {
                paintPolygon(transform(p, t), g);
            }
        } else if (StaticViewProperties.selectedObject != null
                && agentPolygonsMap.containsKey(StaticViewProperties.selectedObject.getID())) {
            List<Polygon> polygons = agentPolygonsMap.get(StaticViewProperties.selectedObject.getID());
            for (Polygon p : polygons) {
                paintPolygon(transform(p, t), g);
            }
        }


        return new ArrayList<>();
    }

    protected Polygon transform(Polygon p, ScreenTransform t) {
        int count = p.npoints;
        int[] xs = new int[count];
        int[] ys = new int[count];
        for (int i = 0; i < count; i++) {
            xs[i] = t.xToScreen(p.xpoints[i]);
            ys[i] = t.yToScreen(p.ypoints[i]);
        }
        return new Polygon(xs, ys, count);
    }

    protected void paintPolygon(Polygon p, Graphics2D g) {

    }

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
