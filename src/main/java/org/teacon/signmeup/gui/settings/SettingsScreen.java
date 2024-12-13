package org.teacon.signmeup.gui.settings;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.HudManager;
import cn.ussshenzhou.t88.gui.advanced.TOptionsPanel;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import net.minecraft.network.chat.Component;
import org.teacon.signmeup.config.MiniMap;
import org.teacon.signmeup.hud.InnerMiniMapPanel;
import org.teacon.signmeup.hud.MiniMapPanel;
import org.teacon.signmeup.config.RefreshRate;

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
        settingsPanel.addOptionCycleButtonInit(Component.translatable("gui.sign_up.minimap.minimap"), List.of(
                        "gui.sign_up.minimap.minimap.on",
                        "gui.sign_up.minimap.minimap.off"
                        //TODO render-preset-off
                ),
                List.of(
                        button -> {
                            HudManager.addIfSameClassNotExist(new MiniMapPanel());
                            ConfigHelper.getConfigWrite(MiniMap.class, miniMap -> miniMap.visible = true);
                        },
                        button -> {
                            HudManager.removeInstanceOf(MiniMapPanel.class);
                            ConfigHelper.getConfigWrite(MiniMap.class, miniMap -> miniMap.visible = false);
                        }
                ),
                entry -> entry.getContent().equals(
                        HudManager.getChildren().stream().anyMatch(tComponent -> tComponent instanceof MiniMapPanel)
                                ? "gui.sign_up.minimap.minimap.on"
                                : "gui.sign_up.minimap.minimap.off"
                ));
        settingsPanel.addOptionCycleButtonInit(Component.translatable("gui.sign_up.minimap.rotate"), List.of(Boolean.TRUE, Boolean.FALSE),
                bool -> button -> ConfigHelper.getConfigWrite(MiniMap.class, miniMap -> miniMap.followPlayerRotation = button.getSelected().getContent()),
                entry -> entry.getContent() == ConfigHelper.getConfigRead(MiniMap.class).followPlayerRotation);
        settingsPanel.addOptionSliderDoubleInit(Component.translatable("gui.sign_up.minimap.range"), 8, 320,
                (component, value) -> Component.literal(String.format("%d", value.intValue())),
                null,
                (slider, value) -> ConfigHelper.getConfigWrite(MiniMap.class, miniMap -> miniMap.setCoverRange((int) slider.getAbsValue())),
                ConfigHelper.getConfigRead(MiniMap.class).getCoverRange(),
                true);

        settingsPanel.addOptionSliderDoubleInit(Component.translatable("gui.sign_up.minimap.ssaa"), 0.5, 4,
                (component, value) -> Component.literal(String.format("%.1f", Math.round(value * 10) / 10.0)),
                null,
                (slider, value) -> {
                    ConfigHelper.getConfigWrite(MiniMap.class, miniMap -> miniMap.ssaaRatio = (float) (Math.round(slider.getAbsValue() * 10) / 10.0));
                    HudManager.getChildren().stream()
                            .filter(tComponent -> tComponent instanceof InnerMiniMapPanel)
                            .findFirst()
                            .ifPresent(minimap -> minimap.resizeAsHud(this.getRectangle().width(), this.getRectangle().height()));
                },
                ConfigHelper.getConfigRead(MiniMap.class).ssaaRatio,
                true
        );
        settingsPanel.addOptionCycleButtonInit(Component.translatable("gui.sign_up.minimap.refresh"), List.of(RefreshRate.values()),
                refreshRate -> button -> ConfigHelper.getConfigWrite(MiniMap.class, miniMap -> miniMap.refreshRate = refreshRate),
                entry -> entry.getContent() == ConfigHelper.getConfigRead(MiniMap.class).refreshRate);
    }

    @Override
    public void layout() {
        settingsPanel.setBounds(0, 0, width, height);
        super.layout();
    }
}
