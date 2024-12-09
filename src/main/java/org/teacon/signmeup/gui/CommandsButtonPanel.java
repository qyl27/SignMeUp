package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.advanced.THoverSensitiveImageButton;
import cn.ussshenzhou.t88.network.NetworkHelper;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.teacon.signmeup.SignMeUp;
import org.teacon.signmeup.config.PlayerCommands;
import org.teacon.signmeup.network.PerformCommandPacket;

/**
 * @author USS_Shenzhou
 */
public class CommandsButtonPanel extends ButtonPanel {

    public CommandsButtonPanel() {
        super(false);
        ConfigHelper.getConfigRead(PlayerCommands.class).playerCommands.forEach(command -> {
            var button = new THoverSensitiveImageButton(Component.literal(command.title),
                    b -> {
                        NetworkHelper.sendToServer(new PerformCommandPacket(command.title));
                        getTopParentScreenOptional().ifPresent(tScreen -> tScreen.onClose(false));
                    },
                    SignMeUp.id("textures/gui/button_panel_button.png"),
                    SignMeUp.id("textures/gui/button_panel_button_hovered.png"));
            button.setPadding(0);
            button.setTooltip(Tooltip.create(Component.literal(command.tooltip)));
            this.buttons.add(button);
        });
    }
}
