package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.advanced.THoverSensitiveImageButton;
import cn.ussshenzhou.t88.gui.container.TVerticalScrollContainer;
import cn.ussshenzhou.t88.network.NetworkHelper;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;
import org.teacon.signmeup.config.Waypoints;
import org.teacon.signmeup.network.TeleportToWayPointPacket;

/**
 * @author USS_Shenzhou
 */
public class WayPointsPanel extends TVerticalScrollContainer {

    public WayPointsPanel() {
        super();
        ConfigHelper.getConfigRead(Waypoints.class).waypoints.forEach(wayPoint -> {
            var button = new THoverSensitiveImageButton(Component.literal(wayPoint.name),
                    b -> {
                        NetworkHelper.sendToServer(new TeleportToWayPointPacket(wayPoint.name));
                        getTopParentScreenOptional().ifPresent(tScreen -> tScreen.onClose(false));
                    },
                    null,
                    null);
            button.setPadding(1);
            button.setTooltip(Tooltip.create(Component.literal(wayPoint.description)));
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
