package com.mrl.debugger.layers.base;

import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.ViewLayer;
import math.geom2d.conic.Circle2D;
import rescuecore2.misc.Pair;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.view.StandardEntityViewLayer;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.*;

/**
 * @author Mahdi
 */
@ViewLayer(caption = "Humans", visible = true)
public class MrlBaseHumanLayer extends StandardEntityViewLayer<Human> implements MrlBaseLayer {

    private static final int HP_MAX = 10000;
    private static final int HP_INJURED = 7500;
    private static final int HP_CRITICAL = 1000;

    protected static final int SIZE = 10;

    private static final Color CIVILIAN_COLOUR = Color.GREEN;
    private static final Color FIRE_BRIGADE_COLOUR = Color.RED;
    private static final Color POLICE_FORCE_COLOUR = Color.BLUE;
    private static final Color AMBULANCE_TEAM_COLOUR = Color.WHITE;
    private static final Color DEAD_COLOUR = Color.YELLOW.brighter();

    private static final HumanSorter HUMAN_SORTER = new HumanSorter();


    protected Map<EntityID, java.util.List<Integer>> agentEntitiesMap = new HashMap<>();
    private boolean drawOverAllData = false;
    private Set<Integer> overallEntities = new HashSet<>();

    public MrlBaseHumanLayer() {
        super(Human.class);
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            drawOverAllData = annotation.drawAllData();
        }
    }

    @Override
    public void bind(EntityID id, int key, Object... params) {
        if (key == 0) {
            java.util.List<Integer> data = (java.util.List<Integer>) params[0];
            agentEntitiesMap.put(id, data);
            if (drawOverAllData) {
                overallEntities.clear();
                for (java.util.List<Integer> agentData : agentEntitiesMap.values()) {
                    overallEntities.addAll(agentData);
                }
            }
        }
    }

    @Override
    public String getName() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            return annotation.caption();
        } else {
            return "Human";
        }
    }


    @Override
    public Shape render(Human h, Graphics2D g, ScreenTransform t) {
        Pair<Integer, Integer> location = getLocation(h);
        if (location == null) {
            return null;
        }
        if (h.isPositionDefined() && (world.getEntity(h.getPosition()) instanceof AmbulanceTeam)) {
            return null;
        }

        int x = t.xToScreen(location.first());
        int y = t.yToScreen(location.second());
        Shape shape;
        shape = new Ellipse2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);
        if (h == StaticViewProperties.selectedObject) {
            g.setColor(Color.MAGENTA);
            defaultCircle(h, g, t);
        } else {
            g.setColor(adjustColour(getColour(h), h.getHP()));
        }
        paintShape(h, shape, g);
        if (drawOverAllData
                && (StaticViewProperties.selectedObject == null || !agentEntitiesMap.containsKey(StaticViewProperties.selectedObject.getID()))) {
            if (overallEntities.contains(h.getID().getValue())) {
                paintData(h, shape, g,t);
            }
        } else if (StaticViewProperties.selectedObject != null
                && agentEntitiesMap.containsKey(StaticViewProperties.selectedObject.getID())
                && agentEntitiesMap.get(StaticViewProperties.selectedObject.getID()).contains(h.getID().getValue())) {
            paintData(h, shape, g,t);
        }
        return shape;
    }

    protected void drawCircle(Graphics2D g, ScreenTransform t, Pair<Integer, Integer> location, int range) {
        int x = location.first();
        int y = location.second();
        int d = t.xToScreen(x + range) - t.xToScreen(x);
        Circle2D circle2D = new Circle2D(t.xToScreen(x), t.yToScreen(y), d, true);
        circle2D.draw(g);
    }

    /**
     * Paint the overall shape.
     *
     * @param h     The area.
     * @param shape The overall polygon.
     * @param g     The graphics to paint on.
     */
    protected void paintShape(Human h, Shape shape, Graphics2D g) {
        g.fill(shape);
    }

    /**
     * Paint the overall shape which contains in data retrieved from agents.
     *  @param h     The Human.
     * @param shape The overall polygon.
     * @param g     The graphics to paint on.
     * @param t
     */
    protected void paintData(Human h, Shape shape, Graphics2D g, ScreenTransform t) {
    }

    protected void defaultCircle(Human h, Graphics2D g, ScreenTransform t) {

    }

    protected Color getColour(Human h) {
        switch (h.getStandardURN()) {
            case CIVILIAN:
                return CIVILIAN_COLOUR;
            case FIRE_BRIGADE:
                return FIRE_BRIGADE_COLOUR;
            case AMBULANCE_TEAM:
                return AMBULANCE_TEAM_COLOUR;
            case POLICE_FORCE:
                return POLICE_FORCE_COLOUR;
            default:
                throw new IllegalArgumentException("Don't know how to draw humans of type " + h.getStandardURN());
        }
    }

    protected Color adjustColour(Color c, int hp) {
        if (hp == 0) {
            return DEAD_COLOUR;
        }
        if (hp < HP_CRITICAL) {
            c = c.darker();
        }
        if (hp < HP_INJURED) {
            c = c.darker();
        }
        if (hp < HP_MAX) {
            c = c.darker();
        }
        return c;
    }

    private static final class HumanSorter implements Comparator<Human>, java.io.Serializable {
        public int compare(Human h1, Human h2) {
            if (h1 instanceof Civilian && !(h2 instanceof Civilian)) {
                return -1;
            }
            if (h2 instanceof Civilian && !(h1 instanceof Civilian)) {
                return 1;
            }
            return h1.getID().getValue() - h2.getID().getValue();
        }
    }

    @Override
    protected void postView() {
        Collections.sort(entities, HUMAN_SORTER);
    }


    /**
     * Get the location of a human.
     *
     * @param h The human to look up.
     * @return The location of the human.
     */
    protected Pair<Integer, Integer> getLocation(Human h) {
        return h.getLocation(world);
    }


}
