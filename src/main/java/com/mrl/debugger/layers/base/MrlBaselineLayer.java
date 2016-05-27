package com.mrl.debugger.layers.base;

import com.mrl.debugger.Util;
import rescuecore2.misc.gui.ScreenTransform;

import java.awt.geom.Line2D;

/**
 * @author Mahdi
 */
public abstract class MrlBaselineLayer extends MrlBaseShapeLayer<Line2D> {

    protected Line2D transform(Line2D line2D, ScreenTransform t) {
        return Util.transform(line2D, t);
    }

}
