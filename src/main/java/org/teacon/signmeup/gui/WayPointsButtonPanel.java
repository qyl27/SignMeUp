package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.advanced.THoverSensitiveImageButton;
import cn.ussshenzhou.t88.network.NetworkHelper;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.teacon.signmeup.SignMeUp;
import org.teacon.signmeup.config.Waypoints;
import org.teacon.signmeup.network.TeleportToWayPointPacket;

/**
 * @author USS_Shenzhou
 */
public class WayPointsButtonPanel extends ButtonPanel {

    public WayPointsButtonPanel() {
        super();
        ConfigHelper.getConfigRead(Waypoints.class).waypoints.forEach(wayPoint -> {
            var button = new THoverSensitiveImageButton(Component.literal(wayPoint.name),
                    b -> {
                        NetworkHelper.sendToServer(new TeleportToWayPointPacket(wayPoint.name));
                        getTopParentScreenOptional().ifPresent(tScreen -> tScreen.onClose(false));
                    },
                    SignMeUp.id("textures/gui/button_panel_button.png"),
                    SignMeUp.id("textures/gui/button_panel_button_hovered.png"));
            button.setPadding(0);
            button.setTooltip(Tooltip.create(Component.literal(wayPoint.description)));
            this.buttons.add(button);
        });
    }
}
