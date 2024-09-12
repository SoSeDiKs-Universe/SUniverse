package me.sosedik.requiem.listener.player;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
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
public class LoadSavePlayers implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onLoad(@NotNull PlayerDataLoadedEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT data = event.getData();
		if (!PossessingPlayer.loadPossessingData(player, data))
			GhostyPlayer.loadGhostData(player);
	}

	@EventHandler
	public void onSave(@NotNull PlayerDataSaveEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT data = event.getData();
		boolean quit = event.isQuit();
		PossessingPlayer.savePossessedData(player, data, quit);
	}

	@EventHandler
	public void onQuit(@NotNull PlayerQuitEvent event) {
		Player player = event.getPlayer();
		cleanupGhostState(player);
	}

	private void cleanupGhostState(@NotNull Player player) {
		PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.NIGHT_VISION);
		if (potionEffect == null) return;
		if (potionEffect.getDuration() != PotionEffect.INFINITE_DURATION) return;

		player.removePotionEffect(PotionEffectType.NIGHT_VISION);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onGameModeChange(@NotNull PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode().isInvulnerable()) return;

		Player player = event.getPlayer();
		if (!player.getGameMode().isInvulnerable()) return;
		if (!GhostyPlayer.isGhost(player)) return;

		GhostyPlayer.markGhost(player);
	}

}
