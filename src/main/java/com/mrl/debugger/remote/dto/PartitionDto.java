package com.mrl.debugger.remote.dto;

import java.awt.*;

/**
 * Created on 5/27/2016.
 *
 * @author Pooya Deldar Gohardani
 */
public class PartitionDto implements StandardDto{
    private String id;
    private Polygon polygon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
