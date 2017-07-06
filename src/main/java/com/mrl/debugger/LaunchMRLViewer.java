package com.mrl.debugger;


import com.mrl.debugger.remote.ViewerGateway;
import rescuecore2.Constants;
import rescuecore2.components.ComponentConnectionException;
import rescuecore2.components.ComponentLauncher;
import rescuecore2.components.TCPComponentLauncher;
import rescuecore2.config.Config;
import rescuecore2.config.ConfigException;
import rescuecore2.connection.ConnectionException;
import rescuecore2.misc.CommandLineOptions;
import rescuecore2.registry.Registry;
import rescuecore2.standard.entities.StandardEntityFactory;
import rescuecore2.standard.entities.StandardPropertyFactory;
import rescuecore2.standard.messages.StandardMessageFactory;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Launcher for sample agents. This will launch as many instances of each of the sample agents as possible, all using one connction.
 */
public final class LaunchMRLViewer {
    private static org.apache.log4j.Logger Logger = org.apache.log4j.Logger.getLogger(LaunchMRLViewer.class);

    private LaunchMRLViewer() {
    }

    /**
     * Launch 'em!
     *
     * @param args The following arguments are understood: -p <port>, -h <hostname>, -fb <fire brigades>, -pf <police forces>, -at <ambulance teams>
     */
    public static void main(String[] args) {
        //Logger.setLogContext("mrlviewer");
        try {
            Registry.SYSTEM_REGISTRY.registerEntityFactory(StandardEntityFactory.INSTANCE);
            Registry.SYSTEM_REGISTRY.registerMessageFactory(StandardMessageFactory.INSTANCE);
            Registry.SYSTEM_REGISTRY.registerPropertyFactory(StandardPropertyFactory.INSTANCE);
            Config config = new Config();
            args = CommandLineOptions.processArgs(args, config);
            int port = config.getIntValue(Constants.KERNEL_PORT_NUMBER_KEY, Constants.DEFAULT_KERNEL_PORT_NUMBER);
            String host = config.getValue(Constants.KERNEL_HOST_NAME_KEY, Constants.DEFAULT_KERNEL_HOST_NAME);

            ComponentLauncher launcher = new TCPComponentLauncher(host, port, config);

            MrlViewer mrlViewer = new MrlViewer();
            connect(launcher, config, mrlViewer);

            listenToAgents(mrlViewer);
        } catch (IOException e) {
            Logger.error("Error connecting agents", e);
        } catch (ConfigException e) {
            Logger.error("Configuration error", e);
        } catch (ConnectionException e) {
            Logger.error("Error connecting agents", e);
        } catch (InterruptedException e) {
            Logger.error("Error connecting agents", e);
        } catch (AlreadyBoundException e) {
            Logger.error("Error connecting agents", e);
            e.printStackTrace();
        }
    }

    private static void connect(ComponentLauncher launcher, Config config, MrlViewer mrlViewer) throws InterruptedException, ConnectionException {
        try {
            Logger.info("Connecting viewer ...");
            launcher.connect(mrlViewer);

            mrlViewer.getViewerPanel().getLayers();


            Logger.info("success");
        } catch (ComponentConnectionException e) {
            Logger.info("failed: " + e.getMessage());
        }
    }


    public static void listenToAgents(MrlViewer mrlViewer) throws RemoteException, AlreadyBoundException {
        ViewerGateway gateway = new DefaultViewerGateway(mrlViewer);
        ViewerGateway stub = (ViewerGateway) UnicastRemoteObject.exportObject(gateway, 0);

        startRmiRegistry();
        // Bind the remote object's stub in the registry
        java.rmi.registry.Registry registry = LocateRegistry.getRegistry();
        registry.bind("com.mrl.debugger.remote.ViewerGateway", stub);

        System.err.println("Server ready");
    }


    private static void startRmiRegistry() {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("RMI registry ready.");
        } catch (Exception e) {
            System.out.println("Exception starting RMI registry:");
            e.printStackTrace();
        }
    }
}