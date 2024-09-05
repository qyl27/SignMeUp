package org.teacon.signmeup.config;

import cn.ussshenzhou.t88.config.TConfig;
import net.minecraft.util.Mth;

/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("FieldMayBeFinal")
public class MiniMap implements TConfig {
    @Override
    public String getChildDirName() {
        return "SignMeUp";
    }

    private int coverRange = 64;
    public boolean followPlayerRotation = true;

    public int getCoverRange() {
        return Mth.clamp(coverRange, 8, 128);
    }

    public void setCoverRange(int coverRange) {
        this.coverRange = coverRange;
    }
}
