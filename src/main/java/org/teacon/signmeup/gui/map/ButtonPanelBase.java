package org.teacon.signmeup.gui.map;

import cn.ussshenzhou.t88.gui.advanced.THoverSensitiveImageButton;
import cn.ussshenzhou.t88.gui.container.TVerticalScrollContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector2i;
import org.teacon.signmeup.SignMeUp;

/**
 * @author USS_Shenzhou
 */
public abstract class ButtonPanelBase extends TPanel {
    private final TImage topDeco;
    private final TImage bottomDeco;

    private static final ResourceLocation ARROW_LEFT = SignMeUp.id("textures/gui/button_panel_arrow_left.png");
    private static final ResourceLocation ARROW_RIGHT = SignMeUp.id("textures/gui/button_panel_arrow_right.png");
    private static final ResourceLocation ARROW_LEFT_HD = SignMeUp.id("textures/gui/button_panel_arrow_left_hovered.png");
    private static final ResourceLocation ARROW_RIGHT_HD = SignMeUp.id("textures/gui/button_panel_arrow_right_hovered.png");
    private static final ResourceLocation BUTTON_PANEL_TL = SignMeUp.id("textures/gui/button_panel_top_left.png");;
    private static final ResourceLocation BUTTON_PANEL_BL = SignMeUp.id("textures/gui/button_panel_bottom_left.png");;
    private static final ResourceLocation BUTTON_PANEL_TR = SignMeUp.id("textures/gui/button_panel_top_right.png");;
    private static final ResourceLocation BUTTON_PANEL_BR = SignMeUp.id("textures/gui/button_panel_bottom_right.png");;

    private static final class THoverSensitiveImageButtonImpl extends THoverSensitiveImageButton {
        private TImage image, hovered;

        public THoverSensitiveImageButtonImpl(Component text1, Button.OnPress onPress, boolean left) {
            super(text1, onPress, left ? ARROW_LEFT : ARROW_RIGHT, left ? ARROW_LEFT_HD : ARROW_RIGHT_HD);
            image = new TImage(left ? ARROW_RIGHT : ARROW_LEFT);
            hovered = new TImage(left ? ARROW_RIGHT_HD : ARROW_LEFT_HD);
        }

        private void switchSide() {
            remove(backgroundImage);
            remove(backgroundImageHovered);

            image.setVisibleT(backgroundImage.isVisibleT());
            LayoutHelper.BSameAsA(image, backgroundImage);
            hovered.setVisibleT(backgroundImageHovered.isVisibleT());
            LayoutHelper.BSameAsA(hovered, backgroundImageHovered);

            TImage c;
            c = backgroundImage;
            backgroundImage = image;
            image = c;
            c = backgroundImageHovered;
            backgroundImageHovered = hovered;
            hovered = c;

            add(backgroundImageHovered);
            add(backgroundImage);
        }
    }

    protected final Buttons buttons = new Buttons();

    private final boolean left;

    private boolean collaped = false;

    private final THoverSensitiveImageButtonImpl collapse;

    public ButtonPanelBase(boolean left) {
        super();
        this.left = left;
        this.setBackground(0xaa564149);
        this.add(buttons);
        this.add(topDeco = new TImage(left ? BUTTON_PANEL_TL : BUTTON_PANEL_TR) {
            @Override
            public void renderTop(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
                super.renderTop(graphics, pMouseX, pMouseY, pPartialTick);
                super.render(graphics, pMouseX, pMouseY, pPartialTick);
            }

            @Override
            public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
            }
        });
        this.add(bottomDeco = new TImage(left ? BUTTON_PANEL_BL : BUTTON_PANEL_BR) {
            @Override
            public void renderTop(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
                super.renderTop(graphics, pMouseX, pMouseY, pPartialTick);
                super.render(graphics, pMouseX, pMouseY, pPartialTick);
            }

            @Override
            public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
            }
        });
        this.add(collapse = new THoverSensitiveImageButtonImpl(Component.empty(), b -> switchCollapse(), left));
    }

    @Override
    public void layout() {
        if (!collaped) {
            LayoutHelper.BSameAsA(buttons, this);
            LayoutHelper.BTopOfA(topDeco, 0, this, 99, 10);
            LayoutHelper.moveDown(topDeco, 4);
            LayoutHelper.BBottomOfA(bottomDeco, 0, this, 99, 10);
            LayoutHelper.moveUp(bottomDeco, 4);
        }

        collapse.setAbsBounds(left != collaped ? getXT() + width - 10 : getXT(), getYT() + height / 2 - 30, 10, 60);

        super.layout();
    }

    public void switchCollapse() {
        collaped = !collaped;
        if (!collaped) {
            remove(collapse);
            add(buttons);
            add(topDeco);
            add(bottomDeco);
            add(collapse);
            setBackground(0xaa564149);
        } else {
            remove(buttons);
            remove(topDeco);
            remove(bottomDeco);
            setBackground(0x00000000);
        }
        collapse.switchSide();
        TScreen ps = this.getParentScreen();
        if (ps != null) {
            ps.layout();
        }
    }

    @Override
    public Vector2i getPreferredSize() {
        //noinspection DataFlowIssue
        return new Vector2i(collaped ? 10 : 80 + 6 + 10, (int) (getTopParentScreen().height * 0.618));
    }

    protected class Buttons extends TVerticalScrollContainer {
        private static final int BUTTON_W = 80;

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
                    child.setBounds(left ? 0 : 10, 20 * i, BUTTON_W, 20);
                    i++;
                }
            }

            super.layout();
        }

        @Override
        protected int getScrollBarX() {
            return super.getXT() + BUTTON_W + (left ? 0 : 10);
        }

        @Override
        public void render(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.render(guigraphics, pMouseX, pMouseY, pPartialTick);
        }

        private static final ResourceLocation SCROLLER_VERTICAL = SignMeUp.id("scrollbar_vert");

        @Override
        protected void renderScrollBar(GuiGraphics guiGraphics) {
            int k1 = this.getMaxScroll();
            if (k1 > 0) {
                int l1 = getScrollBarX();
                int k = (int) ((float) (this.height * this.height) / bottomY);
                k = Mth.clamp(k, 32, this.height - 8);
                int l = (int) this.getScrollAmount() * (this.height - k) / k1 + this.getYT();
                if (l < this.getYT()) {
                    l = this.getYT();
                }

                guiGraphics.fill(l1, this.getYT(), l1 + 6, this.getYT() + height, -16777216);
                guiGraphics.blitSprite(SCROLLER_VERTICAL, l1, l, 6, k);
            }
        }

        @Override
        public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
            if (isInRange(pMouseX, pMouseY, scrollbarGap, scrollbarGap)) {
                for (TWidget tWidget : children) {
                    if (tWidget.mouseDragged(pMouseX, pMouseY + scrollAmount, pButton, pDragX, pDragY)) {
                        return true;
                    }
                }
                if (pMouseX > getScrollBarX() - scrollbarGap - 6) {
                    double d0 = Math.max(1, this.getMaxScroll());
                    int j = Mth.clamp((int) ((float) (height * height) / (float) bottomY), 32, height);
                    double d1 = Math.max(1.0D, d0 / (double) (height - j));
                    this.addScrollAmount(-pDragY * d1 / speedFactor);
                } else {
                    this.addScrollAmount(pDragY / speedFactor);
                }
                return true;
            }
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }
}
