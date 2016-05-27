package com.mrl.debugger.layers.custom;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.standard.MrlStandardRoadLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.Road;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = false, caption = "Sample complex roads")
public class MrlComplexRoadLayer extends MrlStandardRoadLayer {
    @Override
    public void bind(EntityID id, int key, Object... params) {
        super.bind(id, key, params);
    }

    @Override
    protected void paintEdge(Edge e, Graphics2D g, ScreenTransform t) {

    }

    @Override
    protected void paintData(Road area, Polygon p, Graphics2D g) {
        g.setColor(Color.RED);
        g.fill(p);
    }
}
