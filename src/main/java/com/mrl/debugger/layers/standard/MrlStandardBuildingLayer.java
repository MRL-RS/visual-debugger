package com.mrl.debugger.layers.standard;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.base.MrlBaseBuildingLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Building;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = true, caption = "Buildings")
public class MrlStandardBuildingLayer extends MrlBaseBuildingLayer {

    @Override
    protected void paintShape(Building b, Polygon p, Graphics2D g) {

    }

    @Override
    public Shape render(Building area, Graphics2D g, ScreenTransform t) {
        super.render(area, g, t);
        return null;
    }

    @Override
    protected void paintData(Building area, Polygon p, Graphics2D g) {
        super.paintData(area, p, g);
    }
}
