package me.sosedik.trappednewbie.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.incendo.cloud.annotations.Command;
import org.jspecify.annotations.NullMarked;

/**
 * Spit on that thing
 */
@NullMarked
public class SpitCommand {

	@Command("spit")
	public void onCommand(
		CommandSourceStack stack
	) {
		if (!(stack.getExecutor() instanceof Player player)) return;

		LivingEntity spitter = PossessingPlayer.getPossessed(player);
		if (spitter == null) spitter = player;

		Location loc = spitter.getEyeLocation()
				.add(spitter.getLocation().getDirection().multiply(0.8));
		Vector velocity = spitter.getEyeLocation().getDirection();
		TrappedNewbie.scheduler().sync(() -> {
			if (GhostyPlayer.isGhost(player)) {
				player.emitSound(Sound.ENTITY_GHAST_AMBIENT, 0.8F, 2F);
				return;
			}

			player.emitSound(Sound.ENTITY_LLAMA_SPIT, 0.8F, 1F);
			loc.getWorld().spawn(loc, LlamaSpit.class, entity -> entity.setVelocity(velocity));
		});
	}

}
