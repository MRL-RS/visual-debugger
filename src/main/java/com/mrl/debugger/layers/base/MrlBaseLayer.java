package com.mrl.debugger.layers.base;

import rescuecore2.worldmodel.EntityID;

/**
 * @author Mahdi
 */
public interface MrlBaseLayer {

    void bind(EntityID id, int key, Object... params);
}
