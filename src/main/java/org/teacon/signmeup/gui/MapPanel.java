package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.container.TVerticalAndHorizontalScrollContainer;
import cn.ussshenzhou.t88.gui.util.ImageFit;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.teacon.signmeup.SignMeUp;
import org.teacon.signmeup.config.Map;

import static net.minecraft.util.Mth.PI;

/**
 * @author USS_Shenzhou
 */
public class MapPanel extends TVerticalAndHorizontalScrollContainer {
    protected final InnerMapPanel map = new InnerMapPanel();
    private static final Quaternionf QUATERNION = new Quaternionf();
    private final TImage me = new TImage(ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID, "textures/gui/me_map.png")) {
        @Override
        public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
            guigraphics.pose().pushPose();
            var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            QUATERNION.identity().rotateZ(PI + camera.getYRot() * PI / 180);
            guigraphics.pose().last().pose().rotateAround(QUATERNION, x + 16, y + 16, 0);
            super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
            guigraphics.pose().popPose();
        }
    };
    private final WayPointsPanel wayPointsPanel = new WayPointsPanel();

    private float size = 1.5f;

    public MapPanel() {
        map.setImageFit(ImageFit.STRETCH);
        this.add(map);
        this.add(me);
        this.add(wayPointsPanel);
    }

    @Override
    public void tickT() {
        locateMe();
        super.tickT();
    }

    private void locateMe() {
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        var mePos = map.worldToGui(camera.getBlockPosition().getX(), camera.getBlockPosition().getZ());
        me.setBounds(mePos.x - 16, mePos.y - 16, 32, 32);
    }


    @Override
    public void layout() {
        int mapSize = (int) (getUsableHeight() * size);
        map.setBounds(
                Math.max((getUsableWidth() - mapSize) / 2, 0),
                Math.max((getUsableHeight() - mapSize) / 2, 0),
                mapSize, mapSize);
        LayoutHelper.BSameAsA(wayPointsPanel, map);
        locateMe();
        wayPointsPanel.update();
        super.layout();
    }

    @Override
    public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void zoom(float delta) {
        delta *= 0.275f;
        float newSize = Mth.clamp(size + delta, 0.75f, 10);
        float centerX = (float) (scrollAmountX + getUsableWidth() / 2f) / map.getWidth();
        float centerY = (float) (scrollAmountY + getUsableHeight() / 2f) / map.getHeight();
        float prevCenterX = (float) (prevScrollAmountX + getUsableWidth() / 2f) / map.getWidth();
        float prevCenterY = (float) (prevScrollAmountY + getUsableHeight() / 2f) / map.getHeight();
        size = newSize;
        this.layout();
        float newScrollX = centerX * map.getWidth() - getUsableWidth() / 2f;
        float newScrollY = centerY * map.getHeight() - getUsableHeight() / 2f;
        float prevNewScrollX = prevCenterX * map.getWidth() - getUsableWidth() / 2f;
        float prevNewScrollY = prevCenterY * map.getHeight() - getUsableHeight() / 2f;
        initPos();

        if (isScrollBarVisibleHorizontal()) {
            this.prevScrollAmountX = Mth.clamp(prevNewScrollX, 0, getMaxScrollX());
            this.scrollAmountX = Mth.clamp(newScrollX, 0, getMaxScrollX());
        } else {
            this.prevScrollAmountX = this.scrollAmountX = 0;
        }

        if (isScrollBarVisibleVertical()) {
            this.prevScrollAmountY = Mth.clamp(prevNewScrollY, 0, getMaxScrollY());
            this.scrollAmountY = Mth.clamp(newScrollY, 0, getMaxScrollY());
        } else {
            this.prevScrollAmountY = this.scrollAmountY = 0;
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double deltaX, double deltaY) {
        if (this.isInRange(pMouseX, pMouseY)) {
            zoom((float) deltaY);
            return true;
        } else {
            return false;
        }
    }

    public static class InnerMapPanel extends TImage {
        public InnerMapPanel() {
            super(ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID, "textures/gui/map.png"));
        }

        public Vector2i worldToGui(double x, double z) {
            var mapCfg = ConfigHelper.getConfigRead(Map.class);
            //world center
            var pos = new Vector2f(mapCfg.centerWorldX, mapCfg.centerWorldZ);
            //world top left
            pos.add(-mapCfg.worldSize / 2f, -mapCfg.worldSize / 2f);
            //world top left delta
            pos.mul(-1).add((float) x, (float) z);
            //world top left delta relative
            pos.mul(1f / mapCfg.worldSize);
            //gui top left delta
            pos.mul(this.getWidth());
            pos.add(this.getXT(), this.getYT());
            return new Vector2i((int) pos.x, (int) pos.y);
        }
    }
}
