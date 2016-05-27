package com.mrl.debugger.layers.base;

import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * @author Mahdi
 */
public interface MrlBaseLayer {

    void bind(EntityID id, int key, Object... params);
}
