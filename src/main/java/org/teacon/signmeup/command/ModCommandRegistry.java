package org.teacon.signmeup.command;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class ModCommandRegistry {
    @SubscribeEvent
    public static void regCommand(RegisterCommandsEvent event) {
        OpCommands.waypoints(event.getDispatcher());
    }
}
