package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.CommandUtils;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Teleporting between worlds
 */
@NullMarked
@Permission("essence.command.world")
public class WorldCommand {

	@Command("world <world> [player]")
	public void onCommand(
		CommandSourceStack stack,
		@Argument(value = "world") World world,
		@Nullable @Argument(value = "player") Player player,
		@Flag(value = "keepPos") boolean keepPos,
		@Flag(value = "silent") boolean silent
	) {
		Player target = CommandUtils.getTargetPlayer(stack, player);
		if (target == null) return;

		if (target.getWorld() == world) {
			Messenger.messenger(stack.getSender()).sendMessage(CommandUtils.isTargetingOther(stack, target) ? "command.world.already_in.other" : "command.world.already_in", raw("world", world.getName()));
			return;
		}

		if (CommandUtils.isTargetingOther(stack, target))
			Messenger.messenger(stack.getSender()).sendMessage("command.world.other", raw("world", world.getName()), raw("player", target.displayName()));

		Essence.scheduler().sync(() -> {
			LocationUtil.smartTeleport(target, keepPos ? target.getLocation().world(world) : world.getSpawnLocation().center(1), false)
				.thenAccept(_ -> {
					if (!silent)
						Messenger.messenger(target).sendMessage("command.world", raw("world", world.getName()));
				});
		});
	}

}
