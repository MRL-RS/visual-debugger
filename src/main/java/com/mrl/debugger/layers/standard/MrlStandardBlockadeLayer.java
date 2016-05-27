package com.mrl.debugger.layers.standard;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.base.MrlBaseBlockadeLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Blockade;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(caption = "Blockades", visible = true)
public class MrlStandardBlockadeLayer extends MrlBaseBlockadeLayer {

    @Override
    public Shape render(Blockade b, Graphics2D g, ScreenTransform t) {
        super.render(b, g, t);
        return null;
    }
}
