package com.mrl.debugger.layers.standard;

import com.mrl.debugger.layers.base.MrlBaseBuildingDtoLayer;
import com.mrl.debugger.layers.base.MrlBaseBuildingLayer;
import com.mrl.debugger.remote.dto.BuildingDto;
import com.mrl.debugger.remote.dto.StandardDto;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Building;

import java.awt.*;

/**
 * Created by pooya on 6/27/2017.
 */
public class MrlStandardBuildingDtoLayer extends MrlBaseBuildingDtoLayer {

    @Override
    protected void paintShape(Building b, Polygon p, Graphics2D g) {

    }

    @Override
    public Shape render(Building area, Graphics2D g, ScreenTransform t) {
        super.render(area, g, t);
        return null;
    }

    @Override
    protected void paintData(Building area, BuildingDto dto, Polygon p, Graphics2D g) {
        super.paintData(area, dto,p, g);
    }
}
