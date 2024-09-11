package org.teacon.signmeup.network;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.network.annotation.Codec;
import cn.ussshenzhou.t88.network.annotation.NetPacket;
import cn.ussshenzhou.t88.network.annotation.ServerHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.teacon.signmeup.SignMeUp;
import org.teacon.signmeup.config.Waypoints;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author USS_Shenzhou
 */
@NetPacket(modid = SignMeUp.MODID)
public record TeleportToWayPointPacket(String name) {
    private static final Map<String, Waypoints.WayPoint> WAYPOINTS = ConfigHelper.getConfigRead(Waypoints.class).waypoints.stream()
            .collect(Collectors.toMap(wayPoint -> wayPoint.name, wayPoint -> wayPoint));

    @Codec
    public static final StreamCodec<ByteBuf, TeleportToWayPointPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TeleportToWayPointPacket::name,
            TeleportToWayPointPacket::new
    );

    @ServerHandler
    public void serverHandler(IPayloadContext context) {
        context.enqueueWork(() -> {
            var waypoints = WAYPOINTS.get(name);
            var player = context.player();
            player.teleportTo(waypoints.x, waypoints.y, waypoints.z);
        });
    }
}
