package me.sosedik.requiem.listener.player;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.util.NbtProxies;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
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

	public static final String POSSESSED_TAG = "possessed";
	private static final String POSSESSED_ENTITY_DATA = "entity_data";
	private static final String POSSESSED_ENTITY_LOC = "location";

	@EventHandler(priority = EventPriority.LOW)
	public void onLoad(@NotNull PlayerDataLoadedEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT data = event.getData();
		if (!loadPossessingData(player, data))
			loadGhostData(player);
	}

	private boolean loadPossessingData(@NotNull Player player, @NotNull ReadWriteNBT data) {
		if (!data.hasTag(POSSESSED_TAG)) return false;

		data = data.getOrCreateCompound(POSSESSED_TAG);
		if (!data.hasTag(POSSESSED_ENTITY_DATA)) return false;
		if (!data.hasTag(POSSESSED_ENTITY_LOC)) return false;

		byte[] entityData = data.getByteArray(POSSESSED_ENTITY_DATA);
		LivingEntity entity = (LivingEntity) Bukkit.getUnsafe().deserializeEntity(entityData, player.getWorld(), true);
		Location loc = data.get(POSSESSED_ENTITY_LOC, NbtProxies.LOCATION).world(player.getWorld());

		entity.spawnAt(loc);
		PossessingPlayer.startPossessing(player, entity);
//		addExtraControlItems(player); // ToDo: restore inventory? // TODO

		return true;
	}

	private void loadGhostData(@NotNull Player player) {
		if (!player.isInvisible()) return;
		if (!player.getGameMode().isInvulnerable()) return;

		GhostyPlayer.markGhost(player);
//		player.setCooldown(ASItems.GHOST_RELOCATOR.getType(), 5 * 20); // TODO
	}

	@EventHandler
	public void onSave(@NotNull PlayerDataSaveEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT data = event.getData();
		boolean quit = event.isQuit();
		savePossessedData(player, data, quit);
	}

	private void savePossessedData(@NotNull Player player, @NotNull ReadWriteNBT data, boolean quit) {
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		if (quit)
			player.leaveVehicle();

		byte[] entityData = Bukkit.getUnsafe().serializeEntity(entity);
		Location entityLoc = entity.getLocation();

		if (quit) {
			PossessingPlayer.stopPossessing(player, entity, true);
		}

		data = data.getOrCreateCompound(POSSESSED_TAG);
		data.setByteArray(POSSESSED_ENTITY_DATA, entityData);
		data.set(POSSESSED_ENTITY_LOC, entityLoc.world(null), NbtProxies.LOCATION);
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
		if (!event.getNewGameMode().isInvulnerable()) return;

		Player player = event.getPlayer();
		if (player.getGameMode().isInvulnerable()) return;
		if (!GhostyPlayer.isGhost(player)) return;

		GhostyPlayer.markGhost(player);
	}

}
