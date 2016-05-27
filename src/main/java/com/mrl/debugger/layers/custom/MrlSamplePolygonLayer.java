package com.mrl.debugger.layers.custom;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.base.MrlBaseShapeLayer;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = false, caption = "Sample Partitions", drawOverAllData = false)
public class MrlSamplePolygonLayer extends MrlBaseShapeLayer {

    @Override
    protected void paintPolygon(Polygon p, Graphics2D g) {
        g.setColor(Color.BLUE);
        g.draw(p);
    }
}
