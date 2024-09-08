package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.advanced.TOptionsPanel;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import net.minecraft.network.chat.Component;
import org.teacon.signmeup.config.MiniMap;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
public class SettingsScreen extends TScreen {
    TOptionsPanel settingsPanel = new TOptionsPanel();

    public SettingsScreen() {
        super(Component.literal("Settings"));
        this.add(settingsPanel);
        settingsPanel.addOptionSplitter(Component.translatable("gui.sign_up.minimap"));
        settingsPanel.addOptionSliderDoubleInit(Component.translatable("gui.sign_up.minimap.range"), 8, 256,
                (component, value) -> Component.literal(String.format("%d", value.intValue())),
                null,
                (slider, value) -> ConfigHelper.getConfigWrite(MiniMap.class, miniMap -> miniMap.setCoverRange((int) slider.getAbsValue())),
                ConfigHelper.getConfigRead(MiniMap.class).getCoverRange(),
                true);
        settingsPanel.addOptionCycleButtonInit(Component.translatable("gui.sign_up.minimap.rotate"), List.of(Boolean.TRUE, Boolean.FALSE),
                bool -> button -> ConfigHelper.getConfigWrite(MiniMap.class, miniMap -> miniMap.followPlayerRotation = button.getSelected().getContent()),
                entry -> entry.getContent() == ConfigHelper.getConfigRead(MiniMap.class).followPlayerRotation);
    }

    @Override
    public void layout() {
        settingsPanel.setBounds(0, 0, width, height);
        super.layout();
    }
}
