package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class MapScreen extends TScreen {
    private static MapScreen instance = new MapScreen();

    private final MapWidget mapWidget = new MapWidget();

    public static MapScreen getNewInstance() {
        instance = new MapScreen();
        return instance;
    }

    public static MapScreen getInstance() {
        return instance;
    }

    public MapScreen() {
        super(Component.literal("Map Screen"));
        this.add(mapWidget);
    }

    @Override
    public void layout() {
        mapWidget.setBounds(0, 0, width, height);
        super.layout();
    }
}
