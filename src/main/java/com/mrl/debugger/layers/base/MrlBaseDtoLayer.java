package com.mrl.debugger.layers.base;

import com.mrl.debugger.StaticViewProperties;
import com.mrl.debugger.ViewLayer;
import com.mrl.debugger.remote.dto.StandardDto;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.view.StandardViewLayer;
import rescuecore2.view.RenderedObject;
import rescuecore2.worldmodel.EntityID;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * @author Mahdi
 */
public abstract class MrlBaseDtoLayer<T extends StandardDto> extends StandardViewLayer implements MrlBaseLayer {

    private static final Stroke STROKE_DEFAULT = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    private boolean drawAllData = false;

    protected Map<EntityID, T> agentDtoMap = new HashMap<>();
    private Set<T> allDtoSet = new HashSet<>();

    public MrlBaseDtoLayer() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            drawAllData = annotation.drawAllData();
        }
    }

    @Override
    public void bind(EntityID id, int key, Object... params) {
        if (key == 0) {
            T data = (T) params[0];
            agentDtoMap.put(id, data);
            if (drawAllData) {
                allDtoSet.clear();
                allDtoSet.addAll(agentDtoMap.values());
            }
        }
    }

    @Override
    public Collection<RenderedObject> render(Graphics2D g, ScreenTransform t, int width, int height) {

        g.setStroke(STROKE_DEFAULT);
        if (drawAllData
                && (StaticViewProperties.selectedObject == null || !agentDtoMap.containsKey(StaticViewProperties.selectedObject.getID()))) {
            for (T p : allDtoSet) {
                paintDto(transform(p, t), g);
            }
        } else if (StaticViewProperties.selectedObject != null
                && agentDtoMap.containsKey(StaticViewProperties.selectedObject.getID())) {
            T dto = agentDtoMap.get(StaticViewProperties.selectedObject.getID());
            paintDto(transform(dto, t), g);
        }
        return new ArrayList<>();
    }


    protected abstract T transform(T p, ScreenTransform t);

    protected abstract void paintDto(T p, Graphics2D g);

    @Override
    public String getName() {
        ViewLayer annotation = this.getClass().getAnnotation(ViewLayer.class);
        if (annotation != null) {
            return annotation.caption();
        } else {
            return "Unknown";
        }
    }


}
