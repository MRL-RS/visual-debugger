package com.mrl.debugger.layers.base;

import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.ViewLayer;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.view.StandardEntityViewLayer;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Mahdi
 */
@ViewLayer(caption = "Blockades", visible = true)
public class MrlBaseBlockadeLayer extends StandardEntityViewLayer<Blockade> implements MrlBaseLayer {
    private static final Color BLOCKADE_COLOUR = Color.black;
    boolean drawOverAllData = false;


    protected Map<EntityID, java.util.List<Integer>> agentEntitiesMap = new HashMap<>();
    protected Set<Integer> overallEntities = new HashSet<>();

    public void bind(EntityID id, int key, Object... params) {
        if (key == 0) {
            java.util.List<Integer> data = (List<Integer>) params[0];
            agentEntitiesMap.put(id, data);
            if (drawOverAllData) {
                overallEntities.clear();
                for (java.util.List<Integer> agentData : agentEntitiesMap.values()) {
                    overallEntities.addAll(agentData);
                }
            }
        }
    }

    public MrlBaseBlockadeLayer() {
        super(Blockade.class);
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            drawOverAllData = annotation.drawAllData();
        }
    }

    @Override
    public Shape render(Blockade b, Graphics2D g, ScreenTransform t) {
        int[] apexes = b.getApexes();
        int count = apexes.length / 2;
        int[] xs = new int[count];
        int[] ys = new int[count];
        for (int i = 0; i < count; ++i) {
            xs[i] = t.xToScreen(apexes[i * 2]);
            ys[i] = t.yToScreen(apexes[(i * 2) + 1]);
        }
        Polygon shape = new Polygon(xs, ys, count);


        paintShape(b, shape, g);


        if (drawOverAllData
                && (StaticViewProperties.selectedObject == null || !agentEntitiesMap.containsKey(StaticViewProperties.selectedObject.getID()))) {
            if (overallEntities.contains(b.getID().getValue())) {
                paintData(b, shape, g);
            }
        } else if (StaticViewProperties.selectedObject != null
                && agentEntitiesMap.containsKey(StaticViewProperties.selectedObject.getID())
                && agentEntitiesMap.get(StaticViewProperties.selectedObject.getID()).contains(b.getID().getValue())) {
            paintData(b, shape, g);
        }
        return shape;
    }


    /**
     * Paint the overall shape which contains in data retrieved from agents.
     *
     * @param b The blockade.
     * @param p The overall polygon.
     * @param g The graphics to paint on.
     */
    protected void paintData(Blockade b, Polygon p, Graphics2D g) {

    }

    /**
     * Paint the overall shape.
     *
     * @param b The blockade.
     * @param p The overall polygon.
     * @param g The graphics to paint on.
     */
    protected void paintShape(Blockade b, Polygon p, Graphics2D g) {
        if (b == StaticViewProperties.selectedObject) {
            g.setColor(Color.MAGENTA);
        } else {
            g.setColor(BLOCKADE_COLOUR);
        }
        g.fill(p);
    }

    @Override
    public String getName() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            return annotation.caption();
        } else {
            return "Blockades";
        }
    }
}
