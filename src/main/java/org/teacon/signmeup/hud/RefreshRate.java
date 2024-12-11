package org.teacon.signmeup.hud;

import cn.ussshenzhou.t88.gui.util.ITranslatable;

/**
 * @author USS_Shenzhou
 */

public enum RefreshRate implements ITranslatable {
    EVERY_FRAME("gui.sign_up.minimap.refresh.every"),
    ANOTHER_FRAME("gui.sign_up.minimap.refresh.another"),
    TICK("gui.sign_up.minimap.refresh.tick"),
    TICK_20("gui.sign_up.minimap.refresh.tick20");


    RefreshRate(String translateKey) {
        this.translateKey = translateKey;
    }

    private final String translateKey;

    @Override
    public String translateKey() {
        return translateKey;
    }
}
