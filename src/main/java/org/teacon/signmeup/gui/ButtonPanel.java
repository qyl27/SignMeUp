package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.gui.advanced.THoverSensitiveImageButton;
import cn.ussshenzhou.t88.gui.container.TVerticalScrollContainer;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.teacon.signmeup.SignMeUp;

/**
 * @author USS_Shenzhou
 */
public abstract class ButtonPanel extends TPanel {
    private final TImage topDeco = new TImage(ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID, "textures/gui/button_panel_top.png")) {
        @Override
        public void renderTop(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderTop(graphics, pMouseX, pMouseY, pPartialTick);
            super.render(graphics, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        }
    };
    private final TImage bottomDeco = new TImage(ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID, "textures/gui/button_panel_bottom.png")) {
        @Override
        public void renderTop(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderTop(graphics, pMouseX, pMouseY, pPartialTick);
            super.render(graphics, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        }
    };
    protected final Buttons buttons = new Buttons();

    public ButtonPanel() {
        super();
        this.setBackground(0xaa564149);
        this.add(buttons);
        this.add(topDeco);
        this.add(bottomDeco);
    }

    @Override
    public void layout() {
        LayoutHelper.BSameAsA(buttons, this);
        LayoutHelper.BTopOfA(topDeco, 0, this, 99, 10);
        LayoutHelper.moveDown(topDeco, 4);
        LayoutHelper.BBottomOfA(bottomDeco, 0, this, 99, 10);
        LayoutHelper.moveUp(bottomDeco, 4);
        super.layout();
    }

    @Override
    public Vector2i getPreferredSize() {
        //noinspection DataFlowIssue
        return new Vector2i(80 + 7 + 6, (int) (getTopParentScreen().height * 0.618));
    }

    protected static class Buttons extends TVerticalScrollContainer {

        public Buttons() {
            super();
            this.setBackground(0xaa564149);
            this.setScrollbarGap(3);
        }

        @Override
        public void layout() {
            int i = 0;
            for (var child : children) {
                if (child instanceof THoverSensitiveImageButton) {
                    child.setBounds(0, 20 * i, 80, 20);
                    i++;
                }
            }
            super.layout();
        }
    }
}
