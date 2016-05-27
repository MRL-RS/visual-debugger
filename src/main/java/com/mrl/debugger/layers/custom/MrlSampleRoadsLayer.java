package com.mrl.debugger.layers.custom;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.standard.MrlStandardRoadLayer;
import rescuecore2.standard.entities.Road;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = false, caption = "Sample roads", drawAllData = true)
public class MrlSampleRoadsLayer extends MrlStandardRoadLayer {

    @Override
    protected void paintData(Road area, Polygon p, Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fill(p);

//        if (area.getID().getValue() % 2 == 1) {
//            g.setColor(Color.GREEN);
//            g.draw(p);
//        }
    }
}
