package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.gui.container.TVerticalAndHorizontalScrollContainer;
import cn.ussshenzhou.t88.gui.util.ImageFit;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.teacon.signmeup.SignMeUp;

/**
 * @author USS_Shenzhou
 */
public class MapPanel extends TVerticalAndHorizontalScrollContainer {
    private final TImage map = new TImage(ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID, "textures/gui/map.png"));

    private float size = 1;

    public MapPanel() {
        map.setImageFit(ImageFit.STRETCH);
        this.add(map);
    }

    @Override
    public void layout() {
        int mapSize = (int) (getUsableHeight() * size);
        map.setBounds(
                Math.max((width - mapSize) / 2, 0),
                Math.max((height - mapSize) / 2, 0),
                mapSize, mapSize);
        super.layout();
    }

    @Override
    public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void zoom(float delta) {
        delta *= 0.2f;
        float newSize = Mth.clamp(size + delta, 0.75f, 10);
        float centerX = (float) (scrollAmountX + getUsableWidth() / 2f) / map.getWidth();
        float centerY = (float) (scrollAmountY + getUsableHeight() / 2f) / map.getHeight();
        size = newSize;
        this.layout();
        float newScrollX = centerX * map.getWidth() - getUsableWidth() / 2f;
        float newScrollY = centerY * map.getHeight() - getUsableHeight() / 2f;
        initPos();
        if (isScrollBarVisibleHorizontal()) {
            //this.scrollAmountX = Mth.clamp(newScrollX, 0, getMaxScrollX());
        } else {
            this.scrollAmountX = 0;
        }

        if (isScrollBarVisibleVertical()) {
            //this.scrollAmountY = Mth.clamp(newScrollY, 0, getMaxScrollY());
        } else {
            this.scrollAmountY = 0;
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
}
