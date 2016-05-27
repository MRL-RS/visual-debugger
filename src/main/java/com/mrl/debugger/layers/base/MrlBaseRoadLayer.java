package com.mrl.debugger.layers.base;

import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.ViewLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.Hydrant;
import rescuecore2.standard.entities.Road;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = true, caption = "Roads")
public class MrlBaseRoadLayer extends MrlBaseAreaLayer<Road> {

    private static final Color ROAD_EDGE_COLOUR = Color.GRAY.darker();
    private static final Color ROAD_SHAPE_COLOUR = new Color(185, 185, 185);
    private static final Color HYDRANT_SHAPE_COLOUR = new Color(255, 128, 100);

    private static final Stroke WALL_STROKE = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    private static final Stroke ENTRANCE_STROKE = new BasicStroke(0.3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);


    public MrlBaseRoadLayer() {
        super(Road.class);
    }

    @Override
    protected void paintEdge(Edge e, Graphics2D g, ScreenTransform t) {
        g.setColor(ROAD_EDGE_COLOUR);
        g.setStroke(e.isPassable() ? ENTRANCE_STROKE : WALL_STROKE);
        g.drawLine(t.xToScreen(e.getStartX()),
                t.yToScreen(e.getStartY()),
                t.xToScreen(e.getEndX()),
                t.yToScreen(e.getEndY()));
    }

//    @Override
//    public String getName() {
//        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
//        if (annotation != null) {
//            return annotation.caption();
//        } else {
//            return "Roads";
//        }
//    }

    @Override
    protected void paintShape(Road r, Polygon p, Graphics2D g) {
//        StandardEntityToPaint entityToPaint = StaticViewProperties.getPaintObject(r);
//        if (entityToPaint != null) {
//            g.setColor(entityToPaint.getColor());
//            g.fill(p);
//        } else {
        if (r == StaticViewProperties.selectedObject) {
            g.setColor(Color.MAGENTA);
        } else {
            if (r instanceof Hydrant) {
                g.setColor(HYDRANT_SHAPE_COLOUR);
            } else {
                g.setColor(ROAD_SHAPE_COLOUR);
            }
        }
//        }
        g.fill(p);
    }

    @Override
    protected void paintData(Road area, Polygon p, Graphics2D g) {
        super.paintData(area, p, g);
    }

}
