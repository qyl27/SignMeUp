package org.teacon.signmeup.config;

import cn.ussshenzhou.t88.config.TConfig;
import net.minecraft.core.BlockPos;

import java.util.LinkedHashSet;

/**
 * @author USS_Shenzhou
 */
public class Waypoints implements TConfig {
    @Override
    public String getChildDirName() {
        return "SignMeUp";
    }

    public LinkedHashSet<WayPoint> waypoints = new LinkedHashSet<>();

    public static class WayPoint {
        public String name, description;
        public int x, y, z;

        public WayPoint(String name, String description, int x, int y, int z) {
            this.name = name;
            this.description = description;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public WayPoint(String name, String description, BlockPos pos) {
            this.name = name;
            this.description = description;
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
        }

        public WayPoint() {
            this("", "", 0, 0, 0);
        }

        public static WayPoint dumbWayPoint(String name) {
            return new WayPoint(name, "", 0, 0, 0);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof WayPoint that) {
                return this.name.equals(that.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return "[WayPoint name=" + name + ", description=" + description + ", <" + x + ", " + y + ", " + z + ">]";
        }
    }
}
