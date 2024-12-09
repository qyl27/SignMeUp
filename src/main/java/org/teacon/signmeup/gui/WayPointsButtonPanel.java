package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.advanced.THoverSensitiveImageButton;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import cn.ussshenzhou.t88.network.NetworkHelper;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.teacon.signmeup.SignMeUp;
import org.teacon.signmeup.config.Waypoints;
import org.teacon.signmeup.network.TeleportToWayPointPacket;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author USS_Shenzhou
 */
public class WayPointsButtonPanel extends ButtonPanel {

    public WayPointsButtonPanel() {
        super(true);
        ConfigHelper.getConfigRead(Waypoints.class).waypoints.forEach(wayPoint -> {
            var button = new THoverSensitiveImageButton(
                    Component.literal(wayPoint.name),
                    b -> {
                        NetworkHelper.sendToServer(new TeleportToWayPointPacket(wayPoint.name));
                        getTopParentScreenOptional().ifPresent(tScreen -> tScreen.onClose(false));
                    },
                    SignMeUp.id( "textures/gui/button_panel_button.png"),
                    SignMeUp.id( "textures/gui/button_panel_button_hovered.png")
            );
            button.setPadding(0);
            button.setTooltip(Tooltip.create(Component.literal(wayPoint.description)));
            this.buttons.add(button);
        });
    }

    public void highlight(List<Waypoints.WayPoint> highlightWaypoints) {
        if (highlightWaypoints.isEmpty()) {
            for (TWidget child : this.buttons.getChildren()) {
                if (child instanceof THoverSensitiveImageButton btn) {
                    btn.getButton().setFocused(false);
                }
            }
            return;
        }

        Set<String> lookup = highlightWaypoints.stream().map(w -> w.name).collect(Collectors.toSet());
        for (TWidget child : this.buttons.getChildren()) {
            if (!(child instanceof THoverSensitiveImageButton btn)) {
                continue;
            }
            btn.getButton().setFocused(lookup.contains(btn.getText().getText().getString()));
        }
    }

    public String getHighlightWaypoints() {
        for (TWidget child : this.buttons.getChildren()) {
            if (child instanceof THoverSensitiveImageButton btn) {
                if (btn.getButton().isHovered()) {
                    return btn.getText().getText().getString();
                }
            }
        }
        return null;
    }
}
