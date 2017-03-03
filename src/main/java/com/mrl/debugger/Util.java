package com.mrl.debugger;

import rescuecore2.misc.Pair;
import rescuecore2.misc.geometry.Vector2D;
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

    public static Polygon clearAreaRectangle(double agentX, double agentY, double destinationX, double destinationY, double clearRad) {
        int clearLength = (int) Math.hypot(agentX - destinationX, agentY - destinationY);
        Vector2D agentToTarget = new Vector2D(destinationX - agentX, destinationY
                - agentY);

        if (agentToTarget.getLength() > clearLength)
            agentToTarget = agentToTarget.normalised().scale(clearLength);
        agentToTarget = agentToTarget.normalised().scale(agentToTarget.getLength() + 510);

        Vector2D backAgent = (new Vector2D(agentX, agentY))
                .add(agentToTarget.normalised().scale(-510));
        rescuecore2.misc.geometry.Line2D line = new rescuecore2.misc.geometry.Line2D(backAgent.getX(), backAgent.getY(),
                agentToTarget.getX(), agentToTarget.getY());

        Vector2D dir = agentToTarget.normalised().scale(clearRad);
        Vector2D perpend1 = new Vector2D(-dir.getY(), dir.getX());
        Vector2D perpend2 = new Vector2D(dir.getY(), -dir.getX());

        rescuecore2.misc.geometry.Point2D points[] = new rescuecore2.misc.geometry.Point2D[]{
                line.getOrigin().plus(perpend1),
                line.getEndPoint().plus(perpend1),
                line.getEndPoint().plus(perpend2),
                line.getOrigin().plus(perpend2)};
        int[] xPoints = new int[points.length];
        int[] yPoints = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            xPoints[i] = (int) points[i].getX();
            yPoints[i] = (int) points[i].getY();
        }
        return new Polygon(xPoints, yPoints, points.length);
    }

    public static rescuecore2.misc.geometry.Point2D getPoint(Pair<Integer, Integer> position) {
        return new rescuecore2.misc.geometry.Point2D(position.first(), position.second());
    }
}
