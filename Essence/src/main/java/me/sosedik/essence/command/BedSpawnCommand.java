package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.LocationUtil;
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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Teleporting to bed spawn location
 */
@NullMarked
@Permission("essence.command.bedspawn")
public class BedSpawnCommand {

	@Command("bedspawn [player]")
	public void onCommand(
		CommandSourceStack stack,
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
			LocationUtil.smartTeleport(target, loc, false).thenRun(() -> target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 25 * 20, 10)));
		});

		if (!silent) Messenger.messenger(target).sendMessage("command.bedspawn");
		if (stack.getSender() != target)
			Messenger.messenger(stack.getSender()).sendMessage("command.bedspawn.other", raw("player", target.displayName()));
	}

}
