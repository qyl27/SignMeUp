package org.teacon.signmeup.mixin;

import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.teacon.signmeup.hud.MiniMapPanel;

/**
 * @author USS_Shenzhou
 */
@Mixin(value = RenderSectionManager.class,remap = false)
public abstract class IrisRenderSectionManagerMixin {

    @Shadow
    protected abstract float getSearchDistance();

    @Redirect(method = "createTerrainRenderList", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;getSearchDistance()F"), require = 0)
    private float t88DisableDistanceLimitWhenRenderingMiniMap(RenderSectionManager instance) {
        if (MiniMapPanel.rendering) {
            return 1000;
        }
        return this.getSearchDistance();
    }
}
