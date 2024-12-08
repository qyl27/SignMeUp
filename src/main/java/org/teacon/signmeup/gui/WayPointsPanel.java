package org.teacon.signmeup.gui;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TImage;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import org.teacon.signmeup.SignMeUp;
import org.teacon.signmeup.config.Waypoints;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author USS_Shenzhou
 */
public class WayPointsPanel extends TPanel {
    private final BiMap<Waypoints.WayPoint, WayPointDot> logicWaypoints = HashBiMap.create(ConfigHelper.getConfigRead(Waypoints.class).waypoints.size());
    private final ArrayList<WayPointDot> visualWayPoints = new ArrayList<>();
    private static final int DOT_SIZE = 18;

    public WayPointsPanel() {
        super();
    }

    private int getMergeRange() {
        return (int) (DOT_SIZE * 0.75);
    }

    public List<Waypoints.WayPoint> getHighlightWaypoints(double mouseX, double mouseY) {
        for (WayPointDot dot : visualWayPoints) {
            if (dot.isVisibleT() && dot.isInRange(mouseX, mouseY)) {
                return dot.getLogicWaypoints();
            }
        }

        return List.of();
    }

    public Vector2i lookupWaypoint(String waypoint) {
        for (WayPointDot dot : visualWayPoints) {
            List<Waypoints.WayPoint> ps = dot.getLogicWaypoints();
            for (Waypoints.WayPoint p : ps) {
                if (p.name.equals(waypoint)) {
                    return new Vector2i(dot.getXT() + dot.getWidth() / 2, dot.getYT() + dot.getHeight() / 2);
                }
            }
        }
        return null;
    }

    protected void update() {
        if (logicWaypoints.isEmpty()) {
            ConfigHelper.getConfigRead(Waypoints.class).waypoints.forEach(
                    wayPoint -> logicWaypoints.put(wayPoint, new WayPointDot(ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID, "textures/gui/waypoint.png")))
            );
        }
        logicWaypoints.forEach((wayPoint, wayPointDot) -> {
            var pos = WayPointsPanel.this.getParentInstanceOf(MapPanel.class).map.worldToGui(wayPoint.x, wayPoint.z);
            wayPointDot.setAbsBounds(pos.x - DOT_SIZE / 2, pos.y - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
        });
        visualWayPoints.forEach(this::remove);
        visualWayPoints.clear();
        var checked = new HashSet<>();
        for (var wayPointDot : logicWaypoints.values()) {
            if (checked.contains(wayPointDot)) {
                continue;
            }
            var near = logicWaypoints.values().stream()
                    .filter(dot -> dot != wayPointDot && distance2(dot, wayPointDot) <= getMergeRange() * getMergeRange())
                    .collect(Collectors.toList());
            if (near.isEmpty()) {
                visualWayPoints.add(wayPointDot);
            } else {
                checked.addAll(near);
                var dot = new WayPointMultiDot(ResourceLocation.fromNamespaceAndPath(SignMeUp.MODID, "textures/gui/waypoints.png"));
                near.add(wayPointDot);
                dot.joinAll(near.stream().map(d -> logicWaypoints.inverse().get(d)).toList());
                visualWayPoints.add(dot);
            }
        }
        visualWayPoints.forEach(this::add);
    }

    private int distance2(WayPointDot dot1, WayPointDot dot2) {
        return ((dot1.getXT() - 16) - (dot2.getXT() - 16)) * ((dot1.getXT() - 16) - (dot2.getXT() - 16)) + ((dot1.getYT() - 16) - (dot2.getYT() - 16)) * ((dot1.getYT() - 16) - (dot2.getYT() - 16));
    }

    public class WayPointDot extends TImage {

        public WayPointDot(ResourceLocation imageLocation) {
            super(imageLocation);
        }

        public List<Waypoints.WayPoint> getLogicWaypoints() {
            return List.of(logicWaypoints.inverse().get(this));
        }
    }

    public class WayPointMultiDot extends WayPointDot {
        private final ArrayList<Waypoints.WayPoint> containedWaypoints = new ArrayList<>();
        private final TLabel number = new TLabel();

        public WayPointMultiDot(ResourceLocation imageLocation) {
            super(imageLocation);
            this.add(number);
            number.setHorizontalAlignment(HorizontalAlignment.CENTER);
            //TODO
            number.setFontSize(TLabel.STD_FONT_SIZE * 0.75f);
        }

        @Override
        public List<Waypoints.WayPoint> getLogicWaypoints() {
            return Collections.unmodifiableList(containedWaypoints);
        }

        @Override
        public void layout() {
            LayoutHelper.BSameAsA(number, this);
            super.layout();
        }

        private void joinAll(Collection<Waypoints.WayPoint> wayPoints) {
            containedWaypoints.addAll(wayPoints);
            var x = wayPoints.stream()
                    .mapToInt(waypoint -> waypoint.x)
                    .sum() / wayPoints.size();
            var z = wayPoints.stream()
                    .mapToInt(waypoint -> waypoint.z)
                    .sum() / wayPoints.size();
            var pos = WayPointsPanel.this.getParentInstanceOf(MapPanel.class).map.worldToGui(x, z);
            this.setAbsBounds(pos.x - DOT_SIZE / 2, pos.y - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
            number.setText(Component.literal(String.valueOf(wayPoints.size())));
        }
    }
}
