package org.teacon.signin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.teacon.signin.SignMeUp;
import org.teacon.signin.data.Waypoint;

import java.util.function.Supplier;

public final class TriggerFromWaypointPacket {

    private final ResourceLocation waypoint;
    private final ResourceLocation trigger;

    // Used for deserialization
    public TriggerFromWaypointPacket(PacketBuffer buf) {
        this.waypoint = buf.readResourceLocation();
        this.trigger = buf.readResourceLocation();
    }

    public TriggerFromWaypointPacket(ResourceLocation waypoint, ResourceLocation trigger) {
        this.waypoint = waypoint;
        this.trigger = trigger;
    }

    public void write(PacketBuffer buf) {
        buf.writeResourceLocation(this.waypoint);
        buf.writeResourceLocation(this.trigger);
    }

    public void handle(Supplier<NetworkEvent.Context> contextGetter) {
        final NetworkEvent.Context context = contextGetter.get();
        context.enqueueWork(() -> {
            final Waypoint wp = SignMeUp.MANAGER.findWaypoint(this.waypoint);
            if (wp != null && !wp.isDisabled()) {
                SignMeUp.trigger(context.getSender(), wp.getActualLocation(), this.trigger);
            }
        });
        context.setPacketHandled(true);
    }
}