package com.mrl.debugger.remote.dto;

import java.awt.geom.Line2D;

/**
 * Created by pooya on 3/2/2017.
 */
public class EdgeDto implements StandardDto{

    private Line2D line;
    private boolean passable;

    public Line2D getLine() {
        return line;
    }

    public void setLine(Line2D line) {
        this.line = line;
    }

    public boolean isPassable() {
        return passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }
}
