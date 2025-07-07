package me.sosedik.moves.listener.movement;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.block.fluid.FluidData;
import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.moves.Moves;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MetadataUtil;
import org.bukkit.Fluid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Free-falling mechanics
 */
@NullMarked
public class FreeFall implements Listener {

	private static final Set<UUID> LEAPING = new HashSet<>();
	private static final String LEAPING_TAG = "leaping";
	private static final String WAS_FLYING_META = "WasFlyingBeforeLeap";

	@EventHandler(ignoreCancelled = true)
	public void onSneak(PlayerToggleSneakEvent event) {
		if (!event.isSneaking()) return;

		Player player = event.getPlayer();
		if (player.isOnGround()) return;
		if (player.isFlying()) return;
		if (player.isGliding()) return;
		if (player.isSwimming()) return;
		if (player.getFallDistance() < 3) return;
		if (isLeaping(player)) return;
		if (SneakCounter.getSneaksCount(player) < 3) return;

		startLeaping(player);
	}

	@EventHandler(ignoreCancelled = true)
	public void onGlide(EntityToggleGlideEvent event) {
		if (event.isGliding()) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (!isLeaping(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSafeFall(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getDamageSource().getDamageType() != DamageType.FALL) return;
		if (!isLeaping(player)) return;

		FreeFall.stopLeaping(player);

		Block block = player.getSupportingBlock();
		if (block == null) block = player.getLocation().getBlock();

		if (isFreeFallSafeLanding(player, block)) {
			handleLeapingFall(player);
			event.setCancelled(true);
			return;
		}

		if (event.isCancelled()) return;

		if (player.getFallDistance() > 20F) {
			event.setDamage(event.getFinalDamage() * 1.3);
		}
		hurtNearbyEntities(player);
	}

	private boolean isFreeFallSafeLanding(Player player, Block block) {
		if (block.getType() == Material.WATER_CAULDRON) {
			if (player.getFallDistance() >= 100F) {
				// TODO grant advancement
			}
			return true; // Impressive, honestly
		}

		FluidData fluidData = block.getWorld().getFluidData(block.getLocation());
		if (fluidData.getFluidType() == Fluid.WATER || fluidData.getFluidType() == Fluid.FLOWING_WATER)
			return fluidData.getLevel() == 8L;

		Material type = block.getType();
		return type == Material.HAY_BLOCK
			|| type == Material.SNOW
			|| type == Material.SNOW_BLOCK
			|| type == Material.POWDER_SNOW
			|| Tag.LEAVES.isTagged(type)
			|| Tag.BEDS.isTagged(type)
			|| Tag.WOOL.isTagged(type);
	}

	private void handleLeapingFall(Player player) {
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		boolean feltOnSide = !LocationUtil.isTrulySolid(player, block);
		float distance = player.getFallDistance();

		block.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 0F);
		block.emitSound(Sound.ITEM_LEAD_BREAK, 1F, 1F);

		if (!feltOnSide && Tag.LEAVES.isTagged(block.getType())) {
			int leavesBelow = 1;
			int maxLeaves = Math.min((int) Math.ceil(distance / 5), 7);
			Block leaves = block;
			for (; leavesBelow <= maxLeaves; leavesBelow++) {
				leaves = leaves.getRelative(BlockFace.DOWN);
				if (!Tag.LEAVES.isTagged(leaves.getType()))
					break;
			}
			for (int j = 1; j <= leavesBelow; j++) {
				Moves.scheduler().sync(() -> {
					player.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 1F);
					Location loc = player.getLocation().getBlock().getLocation().add(0.5, -1, 0.5);
					player.teleport(loc, TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_PASSENGERS);
				}, j * 3L);
			}
		}
	}

	private void hurtNearbyEntities(Player player) {
		Collection<LivingEntity> nearbyEntities = player.getLocation().getNearbyLivingEntities(0.7);
		if (nearbyEntities.isEmpty()) return;

		double damage = 1 + player.getFallDistance() / 10;
		for (LivingEntity ent : nearbyEntities) {
			if (ent == player) continue;

			ent.damage(damage, player);
			ent.setVelocity(new Vector(0, 0.2, 0));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFlightToggle(PlayerToggleFlightEvent event) {
		if (!event.isFlying()) return;

		Player player = event.getPlayer();
		if (!isLeaping(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSave(PlayerDataSaveEvent event) {
		if (!event.isQuit()) return;
		if (!isLeaping(event.getPlayer())) return;

		ReadWriteNBT data = event.getData();
		data.setBoolean(LEAPING_TAG, true);

		stopLeaping(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(PlayerDataLoadedEvent event) {
		ReadWriteNBT data = event.getData();
		if (!data.hasTag(LEAPING_TAG)) return;

		startLeaping(event.getPlayer());
	}

	/**
	 * Starts free-falling for the player
	 *
	 * @param player player
	 */
	public static void startLeaping(Player player) {
		if (!LEAPING.add(player.getUniqueId())) return;

		boolean flying = player.isFlying();
		if (flying) {
			player.setFlying(false);
			MetadataUtil.setMetadata(player, WAS_FLYING_META, true);
		}

		player.setGliding(true);
		Location loc = player.getLocation().center().pitch(90F);
		player.teleport(loc, TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_PASSENGERS);
		player.emitSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 1.5F);

		Moves.scheduler().sync(task -> {
			if (!isLeaping(player)) return true;

			// Gliding might've been reset by external factors
			player.setGliding(true);

			Location currentLoc = player.getLocation();
			double x = loc.getX() - currentLoc.getX();
			double z = loc.getZ() - currentLoc.getZ();
			player.setVelocity(new Vector(x, player.getVelocity().getY() - 0.3, z));

			if (player.isOnGround() || player.isInWater()) {
				Moves.scheduler().sync(() -> stopLeaping(player), 15L);
				return true;
			}

			if (isSaveFallLocation(player)) {
				player.setFallDistance(0F);
				stopLeaping(player);
				player.setGliding(false);
				return true;
			}

			return false;
		}, 0L, 1L);
	}

	private static boolean isSaveFallLocation(Player player) {
		Block block = player.getLocation().getBlock();
		return block.isLiquid()
				|| block.getType() == Material.COBWEB
				|| block.getType() == Material.SCAFFOLDING;
	}

	/**
	 * Stops free-falling for the player
	 *
	 * @param player player
	 */
	public static void stopLeaping(Player player) {
		if (!LEAPING.remove(player.getUniqueId())) return;

		var metadata = MetadataUtil.removeMetadata(player, WAS_FLYING_META);
		if (metadata != null)
			player.setFlying(true);
	}

	/**
	 * Checks if the player is currently leaping
	 *
	 * @param player player
	 * @return whether player is leaping
	 */
	public static boolean isLeaping(Player player) {
		return LEAPING.contains(player.getUniqueId());
	}

}
