package com.mrl.debugger.layers.base;

import com.mrl.debugger.Util;
import rescuecore2.misc.gui.ScreenTransform;

import java.awt.*;

/**
 * @author Mahdi
 */
public abstract class MrlBasePolygonLayer extends MrlBaseShapeLayer<Polygon> {

    protected Polygon transform(Polygon p, ScreenTransform t) {
        return Util.transform(p,t);
    }
}
