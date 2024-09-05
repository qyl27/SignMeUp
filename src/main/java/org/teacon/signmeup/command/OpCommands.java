package org.teacon.signmeup.command;

import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.network.NetworkHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.network.chat.Component;
import org.teacon.signmeup.config.Waypoints;
import org.teacon.signmeup.network.RemoveWaypointPacket;
import org.teacon.signmeup.network.SetWaypointPacket;

import java.util.Optional;

/**
 * @author USS_Shenzhou
 */
public class OpCommands {

    public static void waypoints(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("smu")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.literal("set")
                                .then(Commands.argument("pos", Vec3Argument.vec3(false))
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .executes(OpCommands::setWaypoint)
                                                .then(Commands.argument("description", StringArgumentType.string())
                                                        .executes(OpCommands::setWaypoint)
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                ConfigHelper.getConfigRead(Waypoints.class).waypoints.stream().map(wayPoint -> wayPoint.name), builder))
                                        .executes(OpCommands::removeWaypoint)
                                )
                        )
        );
    }

    private static int setWaypoint(CommandContext<CommandSourceStack> context) {
        var pos = context.getArgument("pos", WorldCoordinates.class).getBlockPos(context.getSource());
        var name = context.getArgument("name", String.class);
        var description = context.getArgument("description", String.class);
        var waypoint = new Waypoints.WayPoint(name, description, pos.getX(), pos.getY(), pos.getZ());
        ConfigHelper.getConfigWrite(Waypoints.class, waypoints -> {
            waypoints.waypoints.stream().filter(w -> w.name.equals(waypoint.name)).findFirst().ifPresentOrElse(
                    point -> context.getSource().sendSuccess(() -> Component.literal("[" + point + "] already exists. Remove it first if you want to replace it."), true),
                    () -> {
                        context.getSource().sendSuccess(() -> Component.literal("Added " + waypoint), true);
                        waypoints.waypoints.add(waypoint);
                        NetworkHelper.sendToAllPlayers(new SetWaypointPacket(name, description, pos));
                    }
            );
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int removeWaypoint(CommandContext<CommandSourceStack> context) {
        var name = context.getArgument("name", String.class);
        var waypoint = Waypoints.WayPoint.dumbWayPoint(name);
        ConfigHelper.getConfigWrite(Waypoints.class, waypoints -> {
            if (waypoints.waypoints.remove(waypoint)) {
                context.getSource().sendSuccess(() -> Component.literal("[" + name + "] removed"), true);
                Optional.ofNullable(context.getSource().getPlayer())
                        .ifPresent(player -> NetworkHelper.sendToAllPlayers(new RemoveWaypointPacket(name)));
            } else {
                context.getSource().sendFailure(Component.literal("No waypoint called " + name));
            }
        });
        return Command.SINGLE_SUCCESS;
    }
}
