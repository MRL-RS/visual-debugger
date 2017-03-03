package com.mrl.debugger.remote.dto;

import java.util.List;

/**
 * Created on 5/27/2016.
 *
 * @author Pooya Deldar Gohardani
 */
public class GraphDto implements StandardDto {
    private List<EdgeDto> edgeDtoList;

    public List<EdgeDto> getEdgeDtoList() {
        return edgeDtoList;
    }

    public void setEdgeDtoList(List<EdgeDto> edgeDtoList) {
        this.edgeDtoList = edgeDtoList;
    }
}
