package com.mrl.debugger.layers.custom;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.standard.MrlStandardBuildingLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Building;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = false, caption = "Sample buildings")
public class MrlSampleBuildingsLayer extends MrlStandardBuildingLayer {


    @Override
    public void bind(EntityID id, int key, Object... params) {
        super.bind(id, key, params);
    }

    @Override
    public Shape render(Building area, Graphics2D g, ScreenTransform t) {
        super.render(area, g, t);
        return null;
    }

    @Override
    protected void paintData(Building area, Polygon p, Graphics2D g) {
        g.setColor(Color.CYAN);
        g.fill(p);
    }
}
