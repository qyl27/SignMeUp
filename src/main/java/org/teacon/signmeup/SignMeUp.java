package org.teacon.signmeup;

import cn.ussshenzhou.t88.config.ConfigHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import org.teacon.signmeup.config.MiniMap;
import org.teacon.signmeup.config.PlayerCommands;
import org.teacon.signmeup.config.Map;
import org.teacon.signmeup.config.Waypoints;

/**
 * @author USS_Shenzhou
 */
@Mod(SignMeUp.MODID)
public class SignMeUp {
    public static final String MODID = "sign_up";
    public static final boolean IS_SODIUM_INSTALLED = ModList.get().isLoaded("sodium");
    public static final int MAIN_COLOR = 0x14b8a6;

    public SignMeUp(IEventBus modEventBus, ModContainer modContainer) {
        ConfigHelper.loadConfig(new PlayerCommands());
        ConfigHelper.loadConfig(new Map());
        ConfigHelper.loadConfig(new Waypoints());
        ConfigHelper.loadConfig(new MiniMap());
    }
}
