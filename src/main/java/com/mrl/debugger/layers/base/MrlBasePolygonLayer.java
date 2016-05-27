package com.mrl.debugger.layers.base;

import rescuecore2.misc.gui.ScreenTransform;

import java.awt.*;

/**
 * @author Mahdi
 */
public abstract class MrlBasePolygonLayer extends MrlBaseShapeLayer<Polygon> {

    protected Polygon transform(Polygon p, ScreenTransform t) {
        int count = p.npoints;
        int[] xs = new int[count];
        int[] ys = new int[count];
        for (int i = 0; i < count; i++) {
            xs[i] = t.xToScreen(p.xpoints[i]);
            ys[i] = t.yToScreen(p.ypoints[i]);
        }
        return new Polygon(xs, ys, count);
    }
}
