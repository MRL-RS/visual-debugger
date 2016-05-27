package com.mrl.debugger.remote;

import com.mrl.debugger.MrlViewer;
import com.mrl.debugger.layers.base.MrlBaseLayer;
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
        this.layerMap = new HashMap();
        for (ViewLayer viewLayer : mrlViewer.getViewerPanel().getLayers()) {
            this.layerMap.put(viewLayer.getClass().getSimpleName(), viewLayer);
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
