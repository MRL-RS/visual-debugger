package com.mrl.debugger.layers.base;

import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.remote.dto.BuildingDto;
import com.mrl.debugger.remote.dto.StandardDto;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.*;

import java.awt.*;

/**
 * @author Mahdi
 */
@ViewLayer(visible = true, caption = "Buildings")
public class MrlBaseBuildingDtoLayer extends MrlBaseAreaDtoLayer<Building, BuildingDto> {

    private static final Stroke WALL_STROKE = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    private static final Stroke ENTRANCE_STROKE = new BasicStroke(0.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

    private static final Color HEATING = new Color(176, 176, 56, 128);
    private static final Color BURNING = new Color(204, 122, 50, 128);
    private static final Color INFERNO = new Color(160, 52, 52, 128);
    private static final Color WATER_DAMAGE = new Color(50, 120, 130, 128);
    private static final Color MINOR_DAMAGE = new Color(100, 140, 210, 128);
    private static final Color MODERATE_DAMAGE = new Color(100, 70, 190, 128);
    private static final Color SEVERE_DAMAGE = new Color(80, 60, 140, 128);
    private static final Color BURNT_OUT = new Color(0, 0, 0, 255);

    private static final Color REFUGE_BUILDING_COLOR = Color.GREEN.darker();
    private static final Color CENTER_BUILDING_COLOR = Color.WHITE.brighter().brighter();
    private static final Color BURNING_REFUGE_COLOR = Color.RED.darker().darker();

    private static final Color GAS_STATION_COLOR = new Color(230, 180, 200);
    private static final Color BURNING_GAS_STATION_COLOR = new Color(250, 80, 200);


    public MrlBaseBuildingDtoLayer() {
        super(Building.class);
    }

    @Override
    protected void paintEdge(Edge e, Graphics2D g, ScreenTransform t) {
        g.setColor(OUTLINE_COLOUR);
        g.setStroke(e.isPassable() ? ENTRANCE_STROKE : WALL_STROKE);
        g.drawLine(t.xToScreen(e.getStartX()),
                t.yToScreen(e.getStartY()),
                t.xToScreen(e.getEndX()),
                t.yToScreen(e.getEndY()));
    }

//    @Override
//    public String getName() {
//        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
//        if (annotation != null) {
//            return annotation.caption();
//        } else {
//            return "Buildings";
//        }
//    }

    @Override
    protected void paintShape(Building b, Polygon p, Graphics2D g) {
        drawBrokenness(b, p, g);
        drawFieriness(b, p, g);
    }

    @Override
    protected void paintData(Building area, BuildingDto dto, Polygon p, Graphics2D g) {
        super.paintData(area,dto, p, g);
    }

    protected void fill(Shape shape, Graphics2D g, Color c) {
        g.setColor(c);
        g.fill(shape);
    }


    private void drawBrokenness(Building b, Shape shape, Graphics2D g) {
        int brokenness = b.getBrokenness();
        int colour = Math.max(0, 135 - brokenness / 2);
        fill(shape, g, new Color(colour, colour, colour));
//        g.setColor(new Color(colour, colour, colour));
//
//        g.fill(shape);
    }

    private void drawFieriness(Building b, Polygon shape, Graphics2D g) {
        if (b == StaticViewProperties.selectedObject) {
            g.setColor(Color.MAGENTA);
            g.fill(shape);
            return;
        }
        if (b instanceof Refuge) {
            g.setColor(REFUGE_BUILDING_COLOR);
            if (b.isFierynessDefined() && b.getFieryness() > 0) {
                g.setColor(BURNING_REFUGE_COLOR);
            }
            g.fill(shape);
        } else if (b instanceof GasStation) {
            g.setColor(GAS_STATION_COLOR);
            if (b.isFierynessDefined() && b.getFieryness() > 0) {
                g.setColor(BURNING_GAS_STATION_COLOR);
            }
            g.fill(shape);
        } else if ((b instanceof AmbulanceCentre) || (b instanceof FireStation) || (b instanceof PoliceOffice)) {
            g.setColor(CENTER_BUILDING_COLOR);
            if (b.isFierynessDefined() && b.getFieryness() > 0) {
                g.setColor(BURNING_REFUGE_COLOR);
            }
            g.fill(shape);
        }
        if (!b.isFierynessDefined()) {
            return;
        }
        switch (b.getFierynessEnum()) {
            case UNBURNT:
                return;
            case HEATING:
                g.setColor(HEATING);
                break;
            case BURNING:
                g.setColor(BURNING);
                break;
            case INFERNO:
                g.setColor(INFERNO);
                break;
            case WATER_DAMAGE:
                g.setColor(WATER_DAMAGE);
                break;
            case MINOR_DAMAGE:
                g.setColor(MINOR_DAMAGE);
                break;
            case MODERATE_DAMAGE:
                g.setColor(MODERATE_DAMAGE);
                break;
            case SEVERE_DAMAGE:
                g.setColor(SEVERE_DAMAGE);
                break;
            case BURNT_OUT:
                g.setColor(BURNT_OUT);
                break;
            default:
                throw new IllegalArgumentException("Don't know how to render fieriness " + b.getFierynessEnum());
        }
        g.fill(shape);
    }


    int blend(int a, int b, float ratio) {
        if (ratio > 1f) {
            ratio = 1f;
        } else if (ratio < 0f) {
            ratio = 0f;
        }
        float iRatio = 1.0f - ratio;

        int aA = (a >> 24 & 0xff);
        int aR = ((a & 0xff0000) >> 16);
        int aG = ((a & 0xff00) >> 8);
        int aB = (a & 0xff);

        int bA = (b >> 24 & 0xff);
        int bR = ((b & 0xff0000) >> 16);
        int bG = ((b & 0xff00) >> 8);
        int bB = (b & 0xff);

        int A = (int) (aA * iRatio + bA * ratio);
        int R = (int) (aR * iRatio + bR * ratio);
        int G = (int) (aG * iRatio + bG * ratio);
        int B = (int) (aB * iRatio + bB * ratio);

        return A << 24 | R << 16 | G << 8 | B;
    }


}
