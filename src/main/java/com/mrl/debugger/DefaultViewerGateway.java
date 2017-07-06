package com.mrl.debugger;

import com.mrl.debugger.layers.base.MrlBaseLayer;
import com.mrl.debugger.remote.ViewerGateway;
import rescuecore2.view.ViewLayer;
import rescuecore2.worldmodel.EntityID;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mahdi
 */

public class DefaultViewerGateway implements ViewerGateway {

    private Map<String, ViewLayer> layerMap;

    public DefaultViewerGateway(MrlViewer mrlViewer) {
        this.layerMap = new HashMap<>();
        for (ViewLayer viewLayer : mrlViewer.getViewerPanel().getLayers()) {
            com.mrl.debugger.ViewLayer annotation = viewLayer.getClass().getAnnotation(com.mrl.debugger.ViewLayer.class);
            String name;
            if (annotation == null) {
                continue;
            }
            if (annotation.tag().isEmpty()) {
                name = viewLayer.getClass().getSimpleName();
            } else {
                name = annotation.tag();
            }
            this.layerMap.put(name, viewLayer);
        }

    }

    @Override
    public void draw(Integer agentId, String layerTag, int dataType, Serializable data) throws RemoteException {
        ViewLayer viewLayer = this.layerMap.get(layerTag);
        if (viewLayer instanceof MrlBaseLayer) {
            MrlBaseLayer standardLayer = (MrlBaseLayer) viewLayer;
            standardLayer.bind(new EntityID(agentId), dataType, data);
        }
    }
}
