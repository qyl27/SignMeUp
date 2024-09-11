package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.gui.advanced.TImageButton;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.ClientHooks;
import org.teacon.signmeup.SignMeUp;

/**
 * @author USS_Shenzhou
 */
public class MapScreen extends TScreen {
    private static MapScreen instance = new MapScreen();

    private final MapPanel mapPanel = new MapPanel();
    private final TImageButton settingsButton = new TImageButton(TImage.PLACEHOLDER_IMAGE, button -> {
        ClientHooks.pushGuiLayer(Minecraft.getInstance(), new SettingsScreen());
    }, 0x80ffffff, 0xff000000 + SignMeUp.MAIN_COLOR);
    private final CommandsPanel commandsPanel = new CommandsPanel();
    private final WayPointsPanel wayPointsPanel = new WayPointsPanel();


    public static MapScreen getNewInstance() {
        instance = new MapScreen();
        return instance;
    }

    public static MapScreen getInstance() {
        return instance;
    }

    public MapScreen() {
        super(Component.literal("Map Screen"));
        this.add(mapPanel);
        this.add(settingsButton);
        this.add(commandsPanel);
        this.add(wayPointsPanel);
    }

    @Override
    public void layout() {
        mapPanel.setBounds(0, 0, width, height);
        settingsButton.setBounds(10, 10, 20, 20);
        var commandsPanelSize = commandsPanel.getPreferredSize();
        commandsPanel.setBounds(
                width - commandsPanelSize.x,
                (height - commandsPanelSize.y) / 2,
                commandsPanelSize);
        var wayPointsPanelSize = wayPointsPanel.getPreferredSize();
        wayPointsPanel.setBounds(
                0,
                (height - wayPointsPanelSize.y) / 2,
                wayPointsPanelSize);
        super.layout();
    }
}
