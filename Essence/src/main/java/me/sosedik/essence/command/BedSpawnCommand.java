package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Teleporting to bed spawn location
 */
@Permission("essence.command.bedspawn")
public class BedSpawnCommand {

	@Command("bedspawn [player]")
	public void onCommand(
		@NotNull CommandSourceStack stack,
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

		Essence.scheduler().sync(() -> {
			Location loc = target.getRespawnLocation();
			if (loc == null) {
				World world = Bukkit.getWorlds().getFirst();
				loc = new Location(world, 0, world.getMaxHeight() + 200, 0);
			}
			target.teleportAsync(loc).thenRun(() -> target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 25 * 20, 10)));
		});

		if (!silent) Messenger.messenger(target).sendMessage("command.bedspawn");
		if (stack.getSender() != target)
			Messenger.messenger(stack.getSender()).sendMessage("command.bedspawn.other", raw("player", target.displayName()));
	}

}
