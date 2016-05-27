package com.mrl.debugger;

import com.mrl.debugger.layers.base.MrlBaseBlockadeLayer;
import com.mrl.debugger.layers.base.MrlBaseBuildingLayer;
import com.mrl.debugger.layers.base.MrlBaseHumanInfoLayer;
import com.mrl.debugger.layers.base.MrlBaseRoadLayer;

/**
 * Created by Mostafa Shabani.
 * Date: Jun 23, 2011
 * Time: 11:19:57 PM
 */
public class MrlStandardWorldModelViewer extends MrlLayerViewComponent {
    /**
     * Construct a standard world model viewer.
     */
    public MrlStandardWorldModelViewer() {
        addBottomLayers();
        addCustomLayers();
        addTopLayers();
    }

    protected void addBottomLayers() {
        addLayer(new MrlBaseBuildingLayer());
        addLayer(new MrlBaseRoadLayer());
        addLayer(new MrlBaseBlockadeLayer());
    }

    protected void addTopLayers() {
        addLayer(new MrlBaseHumanInfoLayer());

    }

    @Override
    public String getViewerName() {
        return "MRL Standard world model viewer";
    }

    /**
     * Add the default layer set, i.e. nodes, roads, buildings, humans and commands.
     */
    public void addCustomLayers() {

    }
}