package org.teacon.signmeup.network;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.network.annotation.Codec;
import cn.ussshenzhou.t88.network.annotation.NetPacket;
import cn.ussshenzhou.t88.network.annotation.ServerHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.teacon.signmeup.SignMeUp;
import org.teacon.signmeup.config.PlayerCommands;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author USS_Shenzhou
 */
@NetPacket(modid = SignMeUp.MODID)
public record PerformCommandPacket(String commandName) {
    private static final Map<String, PlayerCommands.Command> COMMANDS = ConfigHelper.getConfigRead(PlayerCommands.class).playerCommands.stream()
            .collect(Collectors.toMap(command -> command.title, command -> command));

    @Codec
    public static final StreamCodec<ByteBuf, PerformCommandPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PerformCommandPacket::commandName,
            PerformCommandPacket::new
    );

    @ServerHandler
    public void serverHandler(IPayloadContext context) {
        context.enqueueWork(() -> {
            var command = COMMANDS.get(commandName);
            if (command != null) {
                var player = context.player();
                final MinecraftServer server = context.player().getServer();
                if (server != null) {
                    final CommandSourceStack source = server.createCommandSourceStack()
                            .withEntity(player)
                            .withSuppressedOutput()
                            .withPermission(2);
                    for (String c : command.commands) {
                        server.getCommands().performPrefixedCommand(source, c);
                    }
                }
            }
        });
    }
}
