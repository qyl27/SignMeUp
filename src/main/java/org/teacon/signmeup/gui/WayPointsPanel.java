package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import org.teacon.signmeup.config.Waypoints;

import java.util.LinkedHashSet;

/**
 * @author USS_Shenzhou
 */
public class WayPointsPanel extends TPanel {

    public WayPointsPanel() {
        super();
    }

    private LinkedHashSet<Waypoints.WayPoint> getWayPoints() {
        return ConfigHelper.getConfigRead(Waypoints.class).waypoints;
    }
}
