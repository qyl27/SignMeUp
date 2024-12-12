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

    public ArrayList<Command> playerCommands = new ArrayList<>();

    public static class Command {
        public String title = "";
        public String tooltip = "";
        public ArrayList<String> commands = new ArrayList<>();
    }
}
