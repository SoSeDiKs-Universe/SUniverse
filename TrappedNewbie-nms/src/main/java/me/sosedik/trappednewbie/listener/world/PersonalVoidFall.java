package me.sosedik.trappednewbie.listener.world;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTPersistentDataContainer;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.util.NbtProxies;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Falling in personal void worlds
 */
@NullMarked
public class PersonalVoidFall implements Listener {

	public static String LAST_LOCS_TAG = "last_locs";

	private static final Set<UUID> PENDING = new HashSet<>();

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFall(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getDamageSource().getDamageType() != DamageType.OUT_OF_WORLD) return;

		World world = player.getWorld();
		if (!TrappedNewbie.NAMESPACE.equals(world.key().namespace())) return;
		if (!world.key().value().startsWith("worlds-personal/")) return;

		event.setCancelled(true);
		if (!PENDING.add(player.getUniqueId())) return;

		World newWorld = PerPlayerWorlds.resolveWorld(player, World.Environment.NORMAL);

		Location loc = null;
		var nbt = new NBTPersistentDataContainer(newWorld.getPersistentDataContainer());
		NBTCompound lastLocsNbt = nbt.getCompound(LAST_LOCS_TAG);
		if (lastLocsNbt != null) {
			String key = player.getUniqueId().toString();
			if (lastLocsNbt.hasTag(key))
				loc = nbt.get(key, NbtProxies.LOCATION);
		}

		LimboWorldFall.runTeleport(player, newWorld, loc, GhostyPlayer.isGhost(player))
			.thenRun(() -> PENDING.remove(player.getUniqueId()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		if (!TrappedNewbie.NAMESPACE.equals(world.key().namespace())) return;
		if (!world.key().value().startsWith("worlds-personal/")) return;

		TrappedNewbieAdvancements.GET_INTO_A_PERSONAL_VOID.awardAllCriteria(player);

		Location fromLoc = event.getFromLoc();
		if (fromLoc == null) return;

		World from = event.getFrom();
		var nbt = new NBTPersistentDataContainer(from.getPersistentDataContainer());
		NBTCompound lastLocsNbt = nbt.getOrCreateCompound(LAST_LOCS_TAG);
		lastLocsNbt.set(player.getUniqueId().toString(), player.getLocation(), NbtProxies.LOCATION);
	}

}
