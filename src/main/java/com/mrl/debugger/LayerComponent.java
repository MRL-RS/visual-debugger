package com.mrl.debugger;

import rescuecore2.view.ViewLayer;

import javax.swing.*;

/**
 * Created with MostafaS.
 * User: MRL_RSL
 * Date: 10/31/13
 * Time: 1:49 PM
 */
public class LayerComponent {
    private ViewLayer viewLayer;
    private JMenuItem menuItem;
    private boolean isParent;

    public LayerComponent(ViewLayer viewLayer, JMenuItem menuItem, boolean isParent) {
        this.viewLayer = viewLayer;
        this.menuItem = menuItem;
        this.isParent = isParent;
    }

    public ViewLayer getViewLayer() {
        return viewLayer;
    }

    public JMenuItem getMenuItem() {
        return menuItem;
    }

    public boolean isParent() {
        return isParent;
    }

    @Override
    public String toString() {
        return menuItem.getText();
    }
}
