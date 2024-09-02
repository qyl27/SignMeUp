package org.teacon.signmeup.data;

import cn.ussshenzhou.t88.config.TConfig;

/**
 * @author USS_Shenzhou
 */
public class Map implements TConfig {
    @Override
    public String getChildDirName() {
        return "SignMeUp";
    }

    public final String title = "TeaCon";
    public final int worldSize = 512;
    public final int centerWorldX = 0, centerWorldZ = 0;
}
