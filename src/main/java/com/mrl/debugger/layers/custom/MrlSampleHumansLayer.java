package com.mrl.debugger.layers.custom;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.standard.MrlStandardHumanLayer;
import math.geom2d.conic.Circle2D;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Human;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = false, caption = "Sample humans")
public class MrlSampleHumansLayer extends MrlStandardHumanLayer {

    @Override
    protected void paintData(Human h, Shape shape, Graphics2D g, ScreenTransform t) {
        g.setColor(Color.MAGENTA.darker());
        Circle2D circle2D = new Circle2D(t.xToScreen(h.getX()), t.yToScreen(h.getY()), 18d, true);
        circle2D.draw(g);
//        g.fill(shape);
    }
}
