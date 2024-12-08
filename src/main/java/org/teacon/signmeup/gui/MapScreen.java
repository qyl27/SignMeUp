package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.gui.advanced.THoverSensitiveImageButton;
import cn.ussshenzhou.t88.gui.screen.TScreen;
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
    private final THoverSensitiveImageButton settingsButton = new THoverSensitiveImageButton(Component.empty(),
            button -> {
                ClientHooks.pushGuiLayer(Minecraft.getInstance(), new SettingsScreen());
            },
            SignMeUp.id("textures/gui/setting.png"),
            SignMeUp.id("textures/gui/setting_hovered.png"));
    private final CommandsButtonPanel commandsButtonPanel = new CommandsButtonPanel();
    private final WayPointsButtonPanel wayPointsButtonPanel = new WayPointsButtonPanel();


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
        this.add(commandsButtonPanel);
        this.add(wayPointsButtonPanel);
    }

    @Override
    public void layout() {
        mapPanel.setBounds(0, 0, width, height);
        settingsButton.setBounds(10, 10, 20, 20);
        var commandsPanelSize = commandsButtonPanel.getPreferredSize();
        commandsButtonPanel.setBounds(
                width - commandsPanelSize.x - 6,
                (height - commandsPanelSize.y) / 2,
                commandsPanelSize);
        var wayPointsPanelSize = wayPointsButtonPanel.getPreferredSize();
        wayPointsButtonPanel.setBounds(
                0,
                (height - wayPointsPanelSize.y) / 2,
                wayPointsPanelSize);
        super.layout();
    }
}
