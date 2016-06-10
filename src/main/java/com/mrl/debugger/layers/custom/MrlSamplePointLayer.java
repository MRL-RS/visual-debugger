package com.mrl.debugger.layers.custom;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.base.MrlBasePointLayer;

import java.awt.*;

/**
 * Created on 5/27/2016.
 *
 * @author Pooya Deldar Gohardani
 */
@ViewLayer(caption = "Sample points", tag = "SamplePoint",drawAllData = true)
public class MrlSamplePointLayer extends MrlBasePointLayer {

    @Override
    protected void paintPoint(Point p, Graphics2D g) {
        g.setColor(Color.magenta);
        g.fillOval((int) p.getX(), (int) p.getY(), 20, 20);
        g.setColor(Color.YELLOW);
        g.drawString("Info", (int) p.getX(), (int) p.getY());

    }
}
