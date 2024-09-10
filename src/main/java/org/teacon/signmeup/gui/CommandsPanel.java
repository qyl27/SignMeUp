package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.advanced.THoverSensitiveImageButton;
import cn.ussshenzhou.t88.gui.container.TVerticalScrollContainer;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;
import org.teacon.signmeup.config.PlayerCommands;

/**
 * @author USS_Shenzhou
 */
public class CommandsPanel extends TVerticalScrollContainer {

    public CommandsPanel() {
        super();

        ConfigHelper.getConfigRead(PlayerCommands.class).playerCommands.forEach(command -> {
            var button = new THoverSensitiveImageButton(Component.literal(command.title),
                    b -> {
                //TODO
                    },
                    null,
                    null);
            button.setPadding(1);
            button.setTooltip(Tooltip.create(Component.literal(command.tooltip)));
            this.add(button);
        });
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
