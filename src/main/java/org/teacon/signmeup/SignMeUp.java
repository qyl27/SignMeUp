package org.teacon.signmeup;

import cn.ussshenzhou.t88.config.ConfigHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.teacon.signmeup.data.PlayerCommands;
import org.teacon.signmeup.data.Map;
import org.teacon.signmeup.data.Waypoints;

/**
 * @author USS_Shenzhou
 */
@Mod(SignMeUp.MODID)
public class SignMeUp {
    public static final String MODID = "sign_up";

    public SignMeUp(IEventBus modEventBus, ModContainer modContainer) {
        ConfigHelper.loadConfig(new PlayerCommands());
        ConfigHelper.loadConfig(new Map());
        ConfigHelper.loadConfig(new Waypoints());
    }
}
