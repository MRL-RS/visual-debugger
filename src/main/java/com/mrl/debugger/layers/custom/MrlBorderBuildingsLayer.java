package com.mrl.debugger.layers.custom;

import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.layers.standard.MrlStandardBuildingLayer;
import rescuecore2.standard.entities.Building;

import java.awt.*;

/**
 * @author Pooya
 */
@ViewLayer(visible = false, caption = "Border buildings")
public class MrlBorderBuildingsLayer extends MrlStandardBuildingLayer {

    private static final Color HEATING_COLOUR = Color.YELLOW; //1
    private static final Color BURNING_COLOUR = Color.ORANGE; //2
    private static final Color INFERNO_COLOUR = Color.RED; //3
    private static final Color WATER_DAMAGE_COLOUR = Color.BLUE; //4
    private static final Color MINOR_DAMAGE_COLOUR = new Color(10, 140, 210, 255); //Color.PINK; //5
    private static final Color MODERATE_DAMAGE_COLOUR = new Color(10, 70, 190, 255); //Color.MAGENTA; //6
    private static final Color SEVERE_DAMAGE_COLOUR = new Color(1, 50, 140, 255); //Color.CYAN;  //7
    private static final Color BURNT_OUT_COLOUR = Color.DARK_GRAY.darker(); //8

    @Override
    protected void paintData(Building area, Polygon p, Graphics2D g) {

        if (area.getFierynessProperty().isDefined()) {
            switch (area.getFieryness()) {
                case 1:
                    g.setColor(HEATING_COLOUR);
                    break;
                case 2:
                    g.setColor(BURNING_COLOUR);
                    break;
                case 3:
                    g.setColor(INFERNO_COLOUR);
                    break;
                case 4:
                    g.setColor(WATER_DAMAGE_COLOUR);
                    break;
                case 5:
                    g.setColor(MINOR_DAMAGE_COLOUR);
                    break;
                case 6:
                    g.setColor(MODERATE_DAMAGE_COLOUR);
                    break;
                case 7:
                    g.setColor(SEVERE_DAMAGE_COLOUR);
                    break;
                case 8:
                    g.setColor(BURNT_OUT_COLOUR);
                    break;
                default:
                    g.setColor(Color.CYAN);
            }
        }
        g.fill(p);
    }
}
