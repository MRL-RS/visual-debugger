package com.mrl.debugger.layers.base;

import com.mrl.debugger.ViewLayer;
import math.geom2d.conic.Circle2D;
import rescuecore2.config.Config;
import rescuecore2.misc.Pair;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.misc.AgentPath;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.util.*;

/**
 * @author Mahdi
 */
@ViewLayer(caption = "Humans", visible = true)
public class MrlBaseAnimatedHumanLayer extends MrlBaseHumanLayer {
    private Set<EntityID> humanIDs;

    private Map<EntityID, Queue<Pair<Integer, Integer>>> frames;
    private boolean animationDone;

    /**
     * Construct an animated human view layer.
     */
    public MrlBaseAnimatedHumanLayer() {
        humanIDs = new HashSet<EntityID>();
        frames = new HashMap<>();
        animationDone = true;
    }

    @Override
    public void initialise(Config config) {
        super.initialise(config);
        humanIDs.clear();
        synchronized (this) {
            frames.clear();
            animationDone = true;
        }
    }

    protected void defaultCircle(Human h, Graphics2D g, ScreenTransform t) {
        Circle2D circle2D = new Circle2D(t.xToScreen(h.getX()), t.yToScreen(h.getY()), 18d, true);
        circle2D.draw(g);
    }

//    @Override
//    public String getName() {
//        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
//        if (annotation != null) {
//            return annotation.caption();
//        } else {
//            return "Human";
//        }
//    }

    /**
     * Increase the frame number.
     *
     * @return True if a new frame is actually required.
     */
    public boolean nextFrame() {
        synchronized (this) {
            if (animationDone) {
                return false;
            }
            animationDone = true;
            for (Queue<Pair<Integer, Integer>> next : frames.values()) {
                if (next.size() > 1) {
                    next.remove();
                    animationDone = false;
                }
            }
            return !animationDone;
        }
    }

    @Override
    protected Pair<Integer, Integer> getLocation(Human h) {
        synchronized (this) {
            Queue<Pair<Integer, Integer>> agentFrames = frames.get(h.getID());
            if (agentFrames != null && !agentFrames.isEmpty()) {
                return agentFrames.peek();
            }
        }
        return h.getLocation(world);
    }

    @Override
    protected void preView() {
        super.preView();
        humanIDs.clear();
    }

    @Override
    protected void viewObject(Object o) {
        super.viewObject(o);
        if (o instanceof Human) {
            humanIDs.add(((Human) o).getID());
        }
    }

    /**
     * Compute the animation frames.
     *
     * @param frameCount The number of animation frames to compute.
     */
    public void computeAnimation(int frameCount) {
        synchronized (this) {
            frames.clear();
            // Compute animation
            double step = 1.0 / (frameCount - 1.0);
            for (EntityID next : humanIDs) {
                Queue<Pair<Integer, Integer>> result = new LinkedList<Pair<Integer, Integer>>();
                Human human = (Human) world.getEntity(next);
                if (human == null) {
                    continue;
                }
                AgentPath path = AgentPath.computePath(human, world);
                if (path == null) {
                    continue;
                }
                for (int i = 0; i < frameCount; ++i) {
                    Pair<Integer, Integer> nextPoint = path.getPointOnPath(i * step);
                    result.add(nextPoint);
                }
                result.add(human.getLocation(world));
                frames.put(next, result);
            }
            animationDone = false;
        }
    }
}

