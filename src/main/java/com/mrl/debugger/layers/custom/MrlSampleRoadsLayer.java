package com.mrl.debugger.layers.custom;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.standard.MrlStandardRoadLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Road;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = false, caption = "Sample roads", drawOverAllData = true)
public class MrlSampleRoadsLayer extends MrlStandardRoadLayer {

    @Override
    public void bind(EntityID id, int key, Object... params) {
        super.bind(id, key, params);
    }

    @Override
    public Shape render(Road area, Graphics2D g, ScreenTransform t) {
        super.render(area, g, t);
        return null;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    @Override
    protected void paintData(Road area, Polygon p, Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fill(p);

//        if (area.getID().getValue() % 2 == 1) {
//            g.setColor(Color.GREEN);
//            g.draw(p);
//        }
    }
}
