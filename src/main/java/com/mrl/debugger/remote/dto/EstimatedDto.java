package com.mrl.debugger.remote.dto;

/**
 * Created on 5/27/2016.
 *
 * @author Pooya Deldar Gohardani
 */
public class EstimatedDto implements StandardDto {
    private int id;
    private int fieryness;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFieryness() {
        return fieryness;
    }

    public void setFieryness(int fieryness) {
        this.fieryness = fieryness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EstimatedDto that = (EstimatedDto) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
