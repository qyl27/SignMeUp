package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.advanced.THoverSensitiveImageButton;
import cn.ussshenzhou.t88.gui.container.TVerticalScrollContainer;
import cn.ussshenzhou.t88.network.NetworkHelper;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.teacon.signmeup.SignMeUp;
import org.teacon.signmeup.config.PlayerCommands;
import org.teacon.signmeup.network.PerformCommandPacket;

/**
 * @author USS_Shenzhou
 */
public class CommandsButtonPanel extends TVerticalScrollContainer {

    public CommandsButtonPanel() {
        super();
        ConfigHelper.getConfigRead(PlayerCommands.class).playerCommands.forEach(command -> {
            var button = new THoverSensitiveImageButton(Component.literal(command.title),
                    b -> {
                        NetworkHelper.sendToServer(new PerformCommandPacket(command.title));
                        getTopParentScreenOptional().ifPresent(tScreen -> tScreen.onClose(false));
                    },
                    ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID,"textures/gui/button_panel_button.png"),
                    ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID,"textures/gui/button_panel_button_hovered.png"));
            button.setPadding(0);
            button.setTooltip(Tooltip.create(Component.literal(command.tooltip)));
            this.add(button);
        });
        this.setBackground(0xaa564149);
    }

    @Override
    public void layout() {
        for (int i = 0; i < children.size(); i++) {
            var commandButton = children.get(i);
            commandButton.setBounds(0, 20 * i, 80, 20);
        }
        super.layout();
    }

    @Override
    public Vector2i getPreferredSize() {
        //noinspection DataFlowIssue
        return new Vector2i(80 + 6, (int) (getTopParentScreen().height * 0.618));
    }
}
