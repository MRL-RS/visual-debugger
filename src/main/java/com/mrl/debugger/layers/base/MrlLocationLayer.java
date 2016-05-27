package com.mrl.debugger.layers.base;

import rescuecore2.config.Config;
import rescuecore2.misc.Pair;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.view.StandardViewLayer;
import rescuecore2.view.RenderedObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class MrlLocationLayer extends StandardViewLayer {
    private boolean visible = true;
    public static Pair<Integer, Integer> xy;
    private static final Color valueColor = Color.WHITE;

    /**
     * Construct an area view layer.
     */
    public MrlLocationLayer() {
    }

    @Override
    public void initialise(Config config) {
    }

    @Override
    public java.util.List<JMenuItem> getPopupMenuItems() {
        return new ArrayList<JMenuItem>();
    }

    @Override
    public Collection<RenderedObject> render(Graphics2D g, ScreenTransform t, int width, int height) {
        if (xy != null) {
            int radius = 20;
            int x = t.xToScreen(xy.first());
            int y = t.yToScreen(xy.second());
            g.setColor(valueColor);
            g.drawOval(x - radius, y - radius, radius << 1, radius << 1);
        }
        return new ArrayList<RenderedObject>();
    }

    @Override
    public void setVisible(boolean b) {
        visible = b;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public String getName() {
        return "Location";
    }
}

