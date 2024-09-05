package org.teacon.signmeup.hud;

import cn.ussshenzhou.t88.gui.HudManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class HudListener {
    @SubscribeEvent
    public static void onJoinWorld(ClientPlayerNetworkEvent.LoggingIn event) {
        HudManager.add(new MiniMapPanel());
    }

}
