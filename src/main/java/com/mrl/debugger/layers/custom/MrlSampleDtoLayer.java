package com.mrl.debugger.layers.custom;

import com.mrl.debugger.Util;
import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.base.MrlBaseDtoLayer;
import com.mrl.debugger.remote.dto.PartitionDto;
import com.mrl.debugger.remote.dto.SampleDto;
import rescuecore2.misc.gui.ScreenTransform;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created on 5/27/2016.
 *
 * @author Pooya Deldar Gohardani
 */
@ViewLayer(caption = "Sample Dto", tag = "SampleDtoLayer", drawAllData = true)
public class MrlSampleDtoLayer extends MrlBaseDtoLayer<SampleDto> {

    @Override
    protected SampleDto transform(SampleDto p, ScreenTransform t) {
        SampleDto transformed = new SampleDto();
        transformed.setMyPartition(transformPartition(p.getMyPartition(), t));
        java.util.List<PartitionDto> otherPartitions = new ArrayList<>();
        for (PartitionDto partitionDto : p.getOtherPartitions()) {
            otherPartitions.add(transformPartition(partitionDto, t));
        }
        transformed.setOtherPartitions(otherPartitions);
        return transformed;
    }


    protected PartitionDto transformPartition(PartitionDto partitionDto, ScreenTransform t) {
        PartitionDto transformed = new PartitionDto();
        transformed.setId(partitionDto.getId());
        transformed.setPolygon(Util.transform(partitionDto.getPolygon(), t));
        return transformed;
    }

    @Override
    protected void paintDto(SampleDto p, Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.drawPolygon(p.getMyPartition().getPolygon());

        g.setColor(Color.BLUE);
        for (PartitionDto other : p.getOtherPartitions()) {
            g.drawPolygon(other.getPolygon());
        }
    }
}
