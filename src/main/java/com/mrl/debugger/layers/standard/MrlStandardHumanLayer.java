package com.mrl.debugger.layers.standard;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.base.MrlBaseHumanLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Human;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(caption = "Humans", visible = true)
public class MrlStandardHumanLayer extends MrlBaseHumanLayer {

    @Override
    public Shape render(Human h, Graphics2D g, ScreenTransform t) {
        super.render(h, g, t);
        return null;
    }

    @Override
    protected void paintShape(Human h, Shape shape, Graphics2D g) {

    }

    @Override
    protected void defaultCircle(Human h, Graphics2D g, ScreenTransform t) {

    }

    @Override
    protected void paintData(Human h, Shape shape, Graphics2D g, ScreenTransform t) {

    }
}
