package com.mrl.debugger.layers.base;

import rescuecore2.misc.gui.ScreenTransform;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * @author Mahdi
 */
public abstract class MrlBaselineLayer extends MrlBaseShapeLayer<Line2D> {

    protected Line2D transform(Line2D line2D, ScreenTransform t) {
        Point2D p1 = line2D.getP1();
        Point2D p2 = line2D.getP2();
        Point2D newP1 = new Point2D.Double(t.xToScreen(p1.getX()), t.yToScreen(p1.getY()));
        Point2D newP2 = new Point2D.Double(t.xToScreen(p2.getX()), t.yToScreen(p2.getY()));

        return new Line2D.Double(newP1, newP2);
    }

}
