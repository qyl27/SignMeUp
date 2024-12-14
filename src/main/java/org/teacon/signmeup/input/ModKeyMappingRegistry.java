package org.teacon.signmeup.input;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeyMappingRegistry {
    @SubscribeEvent
    public static void onRegisterKey(RegisterKeyMappingsEvent event) {
        event.register(ModKeyInput.OPEN_NEW_MAP);
        event.register(ModKeyInput.OPEN_MAP);
    }
}
