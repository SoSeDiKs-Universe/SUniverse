package me.sosedik.trappednewbie.listener.player;

import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The very first server join behaviour
 */
public class FirstWorldJoin implements Listener {

	//@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();
//		if (player.hasPlayedBefore()) return;

		World playerWorld = PerPlayerWorlds.getWorld(player.getUniqueId());
		player.addPotionEffects(List.of(effect(PotionEffectType.BLINDNESS), effect(PotionEffectType.NIGHT_VISION)));
		player.teleportAsync(new Location(playerWorld, 0, 120, 0))
			.thenAccept(result -> {
				player.setAllowFlight(true);
				player.setFlying(true);
			});
	}

	@EventHandler(ignoreCancelled = true)
	public void onMove(@NotNull PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedBlock()) return;

		Player player = event.getPlayer();
		Location to = event.getTo();
		World playerWorld = PerPlayerWorlds.getWorld(player.getUniqueId());
		if (to.getWorld() != playerWorld) return;

		Chunk chunk = to.getChunk();
		if (chunk.getX() == 0 && chunk.getZ() == 0) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onMoveC(@NotNull PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedBlock()) return;

		Player player = event.getPlayer();
		Location to = event.getTo();
		World playerWorld = PerPlayerWorlds.getWorld(player.getUniqueId());
		if (to.getWorld() != playerWorld) return;

		Chunk chunk = to.getChunk();
		if (chunk.getX() != 0 || chunk.getZ() != 0) return;
		if (to.getBlockX() != 0) return;
		if (to.getBlockZ() != 0) return;
		if (to.getBlockY() != 100) return;

		player.teleportAsync(new Location(Bukkit.getWorlds().getFirst(), 0, 120, 0));
	}

	private @NotNull PotionEffect effect(@NotNull PotionEffectType effectType) {
		return new PotionEffect(
				effectType,
				PotionEffect.INFINITE_DURATION,
				Byte.MAX_VALUE,
				false,
				false,
				true
		);
	}

}
