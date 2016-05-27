package com.mrl.debugger.remote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Mahdi
 */
public interface ViewerGateway extends Remote {

//    void draw(Integer agentId, String layerTag, Serializable data) throws RemoteException;

    void draw(Integer agentId, String layerTag, int dataType, Serializable data) throws RemoteException;

//    void drawPolygons(Integer agentId, String layerTag, List<Polygon> polygon) throws RemoteException;
//
//    void drawLines(Integer agentId, String layerTag, List<Line2D> data) throws RemoteException;
//
//    void drawPoints(Integer agentId, String layerTag, List<Point2D> data) throws RemoteException;
//
//    void drawInfo(Integer agentId, String layerTag, String[] info, Point[] locations) throws RemoteException;
//
//    void drawInfo(Integer agentId, String layerTag, HashMap<Point2D, String> infoLocationMap) throws RemoteException;
//
//    void drawEntities(Integer agentId, String layerTag , List<Integer> ids);
}
