package me.sosedik.moves.listener.movement;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.moves.Moves;
import me.sosedik.moves.api.event.PlayerStartSittingEvent;
import me.sosedik.moves.api.event.PlayerStopSittingEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Sitting on blocks! :>
 */
@NullMarked
public class SittingMechanics implements Listener {

	private static final Map<UUID, SittingData> SITTERS = new HashMap<>();
	private static final String SITTING_DATA = "sitting";
	private static final String SITTING_CASE = "sit_case";
	private static final String SITTING_LOC = "sit_loc";

	/**
	 * Different sitting cases
	 */
	public enum SitCase {

		/**
		 * Sitting and rotating
		 */
		ROTATING,
		/**
		 * Sitting and not rotating
		 */
		STATIC

	}

	private record SittingData(SitCase sitCase, Location loc) {
	}

	/**
	 * Makes player sit in the provided location
	 *
	 * @param player player
	 * @param loc location
	 * @param sitCase sit case
	 */
	public static @Nullable ArmorStand sit(Player player, Location loc, SitCase sitCase) {
		SittingData oldSittingData = SITTERS.get(player.getUniqueId());
		boolean sitting = oldSittingData != null;
		if (sitting && oldSittingData.sitCase() == sitCase && oldSittingData.loc().isBlockSame(loc)) return null;

		if (player.isSleeping())
			player.wakeup(false);
		CrawlingMechanics.standUp(player);

		for (Player nearbyPlayer : loc.getNearbyEntitiesByType(Player.class, 0.45)) {
			if (isSitting(nearbyPlayer) && !nearbyPlayer.getCollidableExemptions().contains(player.getUniqueId())) {
				if (sitting) return null;
				unSit(nearbyPlayer, true);
			}
		}

		if (sitting)
			unSit(player, false);

		SITTERS.put(player.getUniqueId(), new SittingData(sitCase, loc));

		ArmorStand sit = loc.getWorld().spawn(loc, ArmorStand.class, as -> {
			as.setPersistent(false);
			as.setSmall(true);
			as.setMarker(true);
			as.setGravity(false);
			as.setVisible(false);
			as.setCollidable(false);
			as.setInvulnerable(true);
			as.setRemoveWhenFarAway(true);
			as.setCanTick(false);
		});
		if (!sit.addPassenger(player)) {
			sit.remove();
			return null;
		}

		var event = new PlayerStartSittingEvent(player, loc.getBlock(), sitCase);
		event.callEvent();

		runSittingTask(player, sit, event.getSitCase() != SitCase.STATIC);

		return sit;
	}

	private static void runSittingTask(Player player, ArmorStand sit, boolean rotate) {
		Moves.scheduler().sync(task -> {
			if (!sit.isValid()) return true;

			List<Entity> passengers = sit.getPassengers();
			if (passengers.isEmpty()) return true;

			if (sit.getLocation().getBlock().isEmpty()) {
				unSit(player, true);
				return true;
			}

			if (rotate)
				sit.setRotation(passengers.getFirst().getLocation().getYaw(), 0);

			if (sit.getHealth() != player.getFoodLevel())
				sit.setHealth(Math.max(1, player.getFoodLevel())); // Setting health to 0 removes armor stand

			return false;
		}, 0L, 1L);
	}

	/**
	 * Unsits player if they are sitting
	 *
	 * @param player player
	 * @param velocity whether to apply velocity
	 */
	public static void unSit(Player player, boolean velocity) {
		unSit(player, player.getVehicle(), velocity);
	}

	private static void unSit(Player player, @Nullable Entity mount, boolean velocity) {
		if (!isSitting(player)) return;
		if (!player.isOnline()) return;

		Location loc = mount == null ? player.getLocation() : mount.getLocation().setDirection(player.getLocation().getDirection());

		if (mount != null)
			mount.remove();

		new PlayerStopSittingEvent(player).callEvent();

		LocationUtil.smartTeleport(player, loc, false)
			.thenRun(() -> {
				if (velocity) {
					player.setNoDamageTicks(3);
					boolean roomAbove = player.getEyeLocation().getBlock().getRelative(BlockFace.UP).isEmpty();
					if (roomAbove) {
						float pitch = player.getLocation().getPitch();
						boolean lookingForward = pitch < 45F && pitch > -50F;
						if (lookingForward) {
							player.setVelocity(new Vector(0, 0.2, 0));
							Moves.scheduler().sync(() -> {
								Vector vector = player.getEyeLocation().getDirection().setY(0).normalize().multiply(0.15).setY(0.2);
								player.setVelocity(vector);
							}, 2L);
						} else {
							Block block = player.getLocation().getBlock();
							boolean extraHeight = LocationUtil.getMaxYPoint(block) > 1;
							player.setVelocity(new Vector(0, extraHeight ? 0.4 : 0.2, 0));
							Moves.scheduler().sync(() -> player.setVelocity(new Vector(0, 0.2, 0)), 2L);
						}
					}
				}
			});

		SITTERS.remove(player.getUniqueId());
	}

	public static void removeChairs() {
		for (UUID uuid : new ArrayList<>(SITTERS.keySet())) {
			saveData(uuid, PlayerDataStorage.getData(uuid));
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) continue;

			unSit(player, false);
		}
		SITTERS.clear();
	}

	@EventHandler(ignoreCancelled = true)
	public void onSit(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		if (!player.isOnGround() && !isSitting(player)) return; // Let the player change the sit
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;

		ItemStack offHandItem = player.getInventory().getItemInOffHand();
		if (offHandItem.getType().isBlock() && !offHandItem.isEmpty()) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (player.getLocation().distance(block.getLocation().center()) > 2) return;

		Block upper = block.getRelative(BlockFace.UP);
		if (!upper.isEmpty() && LocationUtil.getMinYPoint(upper) < 0.5) return;

		if (player.isSneaking()) {
			if (block.getBlockData() instanceof Bed bed) {
				if (bed.isOccupied()) return;

				event.setCancelled(true);
				Location loc = block.getLocation().center(block.getBoundingBox().getHeight());
				Moves.scheduler().sync(() -> {
					if (!player.isSneaking())
						sit(player, loc, SitCase.ROTATING);
				}, 10L);
			}
			return;
		}

		if (block.getBlockData() instanceof Stairs stairs) {
			if (stairs.getHalf() == Bisected.Half.TOP) return;

			event.setCancelled(true);
			BlockFace face = stairs.getFacing().getOppositeFace();
			double adding = switch (stairs.getShape()) {
				case INNER_LEFT, INNER_RIGHT -> 0.3;
				case OUTER_LEFT, OUTER_RIGHT -> 0.1;
				default -> 0.15;
			};
			switch (stairs.getShape()) {
				case INNER_LEFT, OUTER_LEFT:
					if (face == BlockFace.EAST) face = BlockFace.NORTH_EAST;
					else if (face == BlockFace.WEST) face = BlockFace.SOUTH_WEST;
					break;
				case INNER_RIGHT, OUTER_RIGHT:
					if (face == BlockFace.EAST) face = BlockFace.SOUTH_EAST;
					else if (face == BlockFace.WEST) face = BlockFace.NORTH_WEST;
					break;
			}
			Vector add = block.getRelative(face).getLocation().subtract(block.getLocation().toVector()).toVector();

			Location loc = player.getLocation();
			float pitch = loc.getPitch();
			if (pitch > 35F) pitch = 35F;
			else if (pitch < -35F) pitch = -35F;
			loc.setDirection(add).setPitch(pitch);

			Location tpLoc = loc.clone();
			loc = block.getLocation().center().setDirection(add).add(add.multiply(adding));

			SittingData oldSittingData = SITTERS.get(player.getUniqueId());
			if (oldSittingData != null && oldSittingData.sitCase() == SitCase.STATIC && oldSittingData.loc().isBlockSame(loc)) return;

			player.teleport(tpLoc);
			sit(player, loc, SitCase.STATIC);
		} else if (block.getBlockData() instanceof Slab slab) {
			event.setCancelled(true);
			boolean lower = slab.getType() == Slab.Type.BOTTOM;
			Location loc = block.getLocation().center(lower ? 0.5 : 0.99);
			sit(player, loc, SitCase.ROTATING);
		} else if (Tag.WOOL_CARPETS.isTagged(block.getType())) {
			event.setCancelled(true);
			Location loc = block.getLocation().center(0.1);
			sit(player, loc, SitCase.ROTATING);
		} else if (Tag.PRESSURE_PLATES.isTagged(block.getType())) {
			event.setCancelled(true);
			Location loc = block.getLocation().center(0.1);
			sit(player, loc, SitCase.ROTATING);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBedSit(PlayerBedEnterEvent event) {
		Player player = event.getPlayer();
		if (player.isSneaking())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getDismounted() instanceof ArmorStand stand)) return;
		if (!(event.getEntity() instanceof Player player)) return;

		unSit(player, stand, true);
	}

	@EventHandler
	public void onQuit(PlayerDataSaveEvent event) {
		Player player = event.getPlayer();
		saveData(player.getUniqueId(), event.getData());
		if (event.isQuit())
			unSit(player, false);
	}

	@EventHandler
	public void onDataLoad(PlayerDataLoadedEvent event) {
		ReadWriteNBT data = event.getData();
		if (!data.hasTag(SITTING_DATA)) return;

		data = data.getCompound(SITTING_DATA);
		assert data != null;
		if (!data.hasTag(SITTING_CASE)) return;
		if (!data.hasTag(SITTING_LOC)) return;

		Player player = event.getPlayer();
		ReadWriteNBT locData = data.getCompound(SITTING_LOC);
		assert locData != null;
		Location loc = new Location(player.getWorld(), locData.getDouble("x"), locData.getDouble("y"), locData.getDouble("z"));
		if (loc.distanceSquared(player.getLocation()) > 4) {
			player.setVelocity(new Vector(0, 0.35, 0));
			return;
		}

		SitCase sitCase = data.getOrDefault(SITTING_DATA, SitCase.ROTATING);
		sit(player, loc, sitCase);
	}

	private static void saveData(UUID uuid, ReadWriteNBT nbt) {
		SittingData sitData = SITTERS.get(uuid);
		if (sitData == null) return;

		ReadWriteNBT sittingData = nbt.getOrCreateCompound(SITTING_DATA);
		sittingData.setString(SITTING_CASE, sitData.sitCase().name());
		Location loc = sitData.loc();
		ReadWriteNBT locData = sittingData.getOrCreateCompound(SITTING_LOC);
		locData.setDouble("x", loc.getX());
		locData.setDouble("y", loc.getY());
		locData.setDouble("z", loc.getZ());
	}

	/**
	 * Checks is this player is currently sitting
	 *
	 * @param player player
	 * @return whether player is sitting
	 */
	public static boolean isSitting(Player player) {
		return SITTERS.containsKey(player.getUniqueId());
	}

}
