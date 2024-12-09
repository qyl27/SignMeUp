package org.teacon.signmeup.config;

import cn.ussshenzhou.t88.config.TConfig;

/**
 * @author USS_Shenzhou
 */
public class Map implements TConfig {
    @Override
    public String getChildDirName() {
        return "SignMeUp";
    }

    public int worldSize = 512;
    public int centerWorldX = 0, centerWorldZ = 0;
}
