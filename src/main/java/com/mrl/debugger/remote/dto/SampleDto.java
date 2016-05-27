package com.mrl.debugger.remote.dto;

import java.util.List;

/**
 * Created on 5/27/2016.
 *
 * @author Pooya Deldar Gohardani
 */
public class SampleDto implements StandardDto{
    private PartitionDto myPartition;
    private List<PartitionDto> otherPartitions;

    public PartitionDto getMyPartition() {
        return myPartition;
    }

    public void setMyPartition(PartitionDto myPartition) {
        this.myPartition = myPartition;
    }

    public List<PartitionDto> getOtherPartitions() {
        return otherPartitions;
    }

    public void setOtherPartitions(List<PartitionDto> otherPartitions) {
        this.otherPartitions = otherPartitions;
    }
}
