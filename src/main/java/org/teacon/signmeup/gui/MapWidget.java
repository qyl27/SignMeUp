package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.gui.container.TVerticalAndHorizontalScrollContainer;
import cn.ussshenzhou.t88.gui.util.ImageFit;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.teacon.signmeup.SignMeUp;

/**
 * @author USS_Shenzhou
 */
public class MapWidget extends TVerticalAndHorizontalScrollContainer {
    private final TImage map = new TImage(ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID, "textures/gui/map.png"));

    private float size = 1;

    public MapWidget() {
        map.setImageFit(ImageFit.STRETCH);
        this.add(map);
    }

    @Override
    public void layout() {
        int mapSize = (int) (height * size);
        map.setBounds((width - mapSize) / 2, (height - mapSize) / 2, mapSize, mapSize);
        super.layout();
    }

    private void zoom(float delta) {
        delta *= 0.2f;
        float newSize = Mth.clamp(size + delta, 0.75f, 10);
        int mapSize = (int) (height * newSize);
        map.setBounds((width - mapSize) / 2, (height - mapSize) / 2, mapSize, mapSize);
        initPos();
        if (isScrollBarVisibleHorizontal()) {
            this.scrollAmountX *= newSize / size;
        } else {
            this.scrollAmountX = 0;
        }
        if (isScrollBarVisibleVertical()) {
            this.scrollAmountY *= newSize / size;
        } else {
            this.scrollAmountY = 0;
        }
        size = newSize;
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
