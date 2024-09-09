package org.teacon.signmeup.config;

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

    public ArrayList<Command> waypoints = new ArrayList<>();

    public static class Command{
        public final String title = "";
        public final String tooltip = "";
        public final ArrayList<String> commands = new ArrayList<>();
    }
}
