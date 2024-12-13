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

    private int coverRange = 96;
    public boolean followPlayerRotation = true;
    public float ssaaRatio = 1.5f;
    public boolean visible = true;
    public RefreshRate refreshRate = RefreshRate.ANOTHER_FRAME;

    public int getCoverRange() {
        return Mth.clamp(coverRange, 8, 320);
    }

    public void setCoverRange(int coverRange) {
        this.coverRange = coverRange;
    }

}
