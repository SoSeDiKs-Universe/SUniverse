package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Loading and saving ghosts
 */
public class LoadSaveGhosts implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(@NotNull PlayerDataLoadedEvent event) {
		Player player = event.getPlayer();
		if (!player.isInvisible()) return;
		if (!player.getGameMode().isInvulnerable()) return;
//		if (MobPossessing.isPossessing(player)) return; // TODO

		GhostyPlayer.markGhost(player);
//		player.setCooldown(ASItems.GHOST_RELOCATOR.getType(), 5 * 20); // TODO
	}

	@EventHandler
	public void onQuit(@NotNull PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.NIGHT_VISION);
		if (potionEffect == null) return;
		if (potionEffect.getDuration() != PotionEffect.INFINITE_DURATION) return;

		player.removePotionEffect(PotionEffectType.NIGHT_VISION);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onGameModeChange(@NotNull PlayerGameModeChangeEvent event) {
		if (!event.getNewGameMode().isInvulnerable()) return;

		Player player = event.getPlayer();
		if (player.getGameMode().isInvulnerable()) return;
		if (!GhostyPlayer.isGhost(player)) return;

		GhostyPlayer.markGhost(player);
	}

}
