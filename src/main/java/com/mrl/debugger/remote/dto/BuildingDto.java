package com.mrl.debugger.remote.dto;

/**
 * Created by pooya on 6/27/2017.
 */
public class BuildingDto implements StandardDto {
    private int id;
    private int fieryness;

    public BuildingDto(int id) {
        this.id = id;
    }

    public BuildingDto(int id, int fieryness) {
        this.id = id;
        this.fieryness = fieryness;
    }

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
        BuildingDto that = (BuildingDto) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "BuildingDto{" +
                "id=" + id +
                ", fieryness=" + fieryness +
                '}';
    }
}
