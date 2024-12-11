package org.teacon.signmeup.api;

import net.neoforged.fml.loading.FMLLoader;
import org.teacon.signmeup.hud.MiniMapAPI;

public interface MiniMap {
    static MiniMap getInstance() {
        if (!FMLLoader.getDist().isClient()) {
            throw new IllegalStateException("This API is only available on Client.");
        }
        return MiniMapAPI.INSTANCE;
    }

    /**
     * <p>Set the visibility of MiniMap.</p>
     *
     * <p>Mods should make sure the minimap is hided in a minimize time
     * After the expected time range, mods must invoke setMiniMapVisibility(modID, true)
     * to show the minimap normally.</p>
     *
     * <p>For the first time a mod hide the minimap, a message will be shown
     * to make players clear that the minimap is hided. And it will also be shown
     * on the map screen.</p>
     *
     * <p>There's no need for different mods to check whether the minimap is visibility.
     * Minimap won't be shown if any mod set it invisible.</p>
     *
     * <p>This method can be invoked from any threads.</p>
     *
     * @param modID      the caller's mod id.
     * @param visibility whether the MiniMap should be visible.
     * @throws IllegalArgumentException if target mod (specific by modID) is not loaded
     * @throws IllegalStateException    if the same mod wants to hide / show the minimap, but
     *                                  it's already hided / shown
     * @throws NullPointerException     if modID is null
     */
    void setMiniMapVisibility(String modID, boolean visibility);
}
