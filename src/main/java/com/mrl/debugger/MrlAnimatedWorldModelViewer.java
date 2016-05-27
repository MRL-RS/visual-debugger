package com.mrl.debugger;

import com.mrl.debugger.layers.base.MrlBaseAnimatedHumanLayer;
import org.reflections.Reflections;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * Created by Mostafa Shabani
 * Date: Dec 10, 2010
 */
public class MrlAnimatedWorldModelViewer extends MrlStandardWorldModelViewer {
    private static final int FRAME_COUNT = 10;
    private static final int ANIMATION_TIME = 750;
    private static final int FRAME_DELAY = ANIMATION_TIME / FRAME_COUNT;

    private MrlBaseAnimatedHumanLayer humans;
    private final Object lock = new Object();
    private boolean done;


    /**
     * Construct an animated world model viewer.
     */
    public MrlAnimatedWorldModelViewer() {
        super();
        Timer timer = new Timer(FRAME_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (lock) {
                    if (done) {
                        return;
                    }
                    done = true;
                    if (humans.nextFrame()) {
                        done = false;
                        repaint();
                    }
                }
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    protected void addTopLayers() {
        super.addTopLayers();
        addLayer(humans = new MrlBaseAnimatedHumanLayer());
    }

    @Override
    public String getViewerName() {
        return "Animated world model viewer";
    }


    @Override
    public void addCustomLayers() {

        Reflections reflections = new Reflections("com.mrl.debugger.layers.custom");
        Set<Class<?>> layers =
                reflections.getTypesAnnotatedWith(ViewLayer.class);
        for (Class<?> layer : layers) {
            try {
                rescuecore2.view.ViewLayer viewLayer = (rescuecore2.view.ViewLayer) layer.newInstance();
                ViewLayer annotation = layer.getAnnotation(ViewLayer.class);
                viewLayer.setVisible(annotation.visible());
//                if(viewLayer instanceof MrlStandardAnimatedHumanLayer){
//                    humans = (MrlStandardAnimatedHumanLayer) viewLayer;
//                }
                addLayer(viewLayer);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void view(Object... objects) {
        super.view(objects);
        synchronized (lock) {
            done = false;
            humans.computeAnimation(FRAME_COUNT);
        }
    }
}