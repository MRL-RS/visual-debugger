package com.mrl.debugger;

import rescuecore2.misc.gui.ScreenTransform;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Created on 5/27/2016.
 *
 * @author Pooya Deldar Gohardani
 */
public class Util {
    public static Polygon transform(Polygon p, ScreenTransform t) {
        int count = p.npoints;
        int[] xs = new int[count];
        int[] ys = new int[count];
        for (int i = 0; i < count; i++) {
            xs[i] = t.xToScreen(p.xpoints[i]);
            ys[i] = t.yToScreen(p.ypoints[i]);
        }
        return new Polygon(xs, ys, count);
    }

    public static Line2D transform(Line2D line2D, ScreenTransform t) {
        Point2D p1 = line2D.getP1();
        Point2D p2 = line2D.getP2();
        Point2D newP1 = new Point2D.Double(t.xToScreen(p1.getX()), t.yToScreen(p1.getY()));
        Point2D newP2 = new Point2D.Double(t.xToScreen(p2.getX()), t.yToScreen(p2.getY()));

        return new Line2D.Double(newP1, newP2);
    }

    public static Point transform(Point p, ScreenTransform t) {
        return new Point(t.xToScreen(p.getX()), t.yToScreen(p.getY()));
    }
}
