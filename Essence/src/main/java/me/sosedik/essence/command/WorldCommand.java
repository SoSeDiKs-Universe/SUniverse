package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Teleporting between worlds
 */
@Permission("essence.command.world")
public class WorldCommand {

	public WorldCommand(@NotNull CommandManager manager) {
		manager.registerCommand(this);
	}

	@Command("world <world> [player]")
	public void onCommand(
		@NotNull CommandSourceStack stack,
		@NotNull @Argument(value = "world") World world,
		@Nullable @Argument(value = "player") Player player,
		@Flag(value = "silent") boolean silent
	) {
		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}
		if (target.getWorld() == world) return;

		if (!silent) Messenger.messenger(target).sendMessage("command.world", raw("world", world.getName()));
		if (stack.getSender() != target)
			Messenger.messenger(stack.getSender()).sendMessage("command.world.other", raw("world", world.getName()), raw("player", target.displayName()));
		Essence.scheduler().sync(() -> target.teleportAsync(target.getLocation().world(world)));
	}

}
