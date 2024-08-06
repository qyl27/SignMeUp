package org.teacon.signmeup.data;

import cn.ussshenzhou.t88.config.TConfig;

import java.util.ArrayList;

/**
 * @author USS_Shenzhou
 */
public class PlayerCommands implements TConfig {
    @Override
    public String getChildDirName() {
        return "SignMeUp";
    }
    public final ArrayList<Command> waypoints = new ArrayList<>();

    public static class Command{
        public final String name = "";
        public final String command = "";
    }
}
