package com.mrl.debugger.layers.standard;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.base.MrlBaseRoadLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Road;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = true, caption = "Roads")
public class MrlStandardRoadLayer extends MrlBaseRoadLayer {


//    @Override
//    protected void paintEdge(Edge e, Graphics2D g, ScreenTransform t) {
//
//    }

    @Override
    public Shape render(Road area, Graphics2D g, ScreenTransform t) {
        super.render(area, g, t);
        return null;
    }


    @Override
    protected void paintShape(Road r, Polygon p, Graphics2D g) {

    }

    @Override
    protected void paintData(Road area, Polygon p, Graphics2D g) {
        super.paintData(area, p, g);
    }

}
