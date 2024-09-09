package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.HudManager;
import cn.ussshenzhou.t88.gui.advanced.TOptionsPanel;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import net.minecraft.network.chat.Component;
import org.teacon.signmeup.config.MiniMap;
import org.teacon.signmeup.hud.MiniMapPanel;

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
        settingsPanel.addOptionSliderDoubleInit(Component.translatable("gui.sign_up.minimap.ssaa"), 0.5, 4,
                (component, value) -> Component.literal(String.format("%.1f", Math.round(value * 10) / 10.0)),
                null,
                (slider, value) -> {
                    ConfigHelper.getConfigWrite(MiniMap.class, miniMap -> miniMap.ssaaRatio = (float) (Math.round(slider.getAbsValue() * 10) / 10.0));
                    HudManager.getChildren().stream()
                            .filter(tComponent -> tComponent instanceof MiniMapPanel)
                            .findFirst()
                            .ifPresent(minimap -> minimap.resizeAsHud(this.getRectangle().width(), this.getRectangle().height()));
                },
                ConfigHelper.getConfigRead(MiniMap.class).ssaaRatio,
                true
        );
    }

    @Override
    public void layout() {
        settingsPanel.setBounds(0, 0, width, height);
        super.layout();
    }
}
