package me.sosedik.miscme.listener.projectile;

import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.api.event.player.PlayerIgniteExplosiveMinecartEvent;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Candle;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Burning projectiles create fire upon landing
 */
@NullMarked
// MCCheck: 1.21.8, new replaceable by fire blocks
public class BurningProjectileCreatesFire implements Listener {

	private static final List<Material> replaceableByFire = List.of(
		Material.SHORT_GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN,
		Material.DEAD_BUSH, Material.BUSH, Material.FIREFLY_BUSH, Material.LEAF_LITTER,
		Material.SHORT_DRY_GRASS, Material.TALL_DRY_GRASS
	);

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFireProjectileLand(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile.getFireTicks() <= 0) return;

		Entity hitEntity = event.getHitEntity();
		if (hitEntity != null) {
			if (hitEntity instanceof ExplosiveMinecart explosiveMinecart && !explosiveMinecart.isIgnited()) {
				explosiveMinecart.ignite();
				if (projectile.getShooter() instanceof Player shooter)
					new PlayerIgniteExplosiveMinecartEvent(shooter, explosiveMinecart).callEvent();
				return;
			}
			hitEntity.setFireTicks(Math.max(hitEntity.getFireTicks(), 20 * 5));
			if (hitEntity instanceof Creeper creeper && !creeper.isIgnited()) {
				creeper.ignite(projectile);
				return;
			}
			return;
		}

		var hitBlock = event.getHitBlock();
		var hitBlockFace = event.getHitBlockFace();
		if (hitBlock == null || hitBlockFace == null) return;

		if (!createFireOrIgnite(hitBlock, hitBlockFace, projectile, BlockIgniteEvent.IgniteCause.ARROW)) return;

		if (projectile instanceof AbstractArrow arrow) {
			MiscMe.scheduler().sync(() -> {
				if (arrow.isValid())
					arrow.startFalling();
			}, 1L);
		}
	}

	/**
	 * Creates fire at location
	 *
	 * @param hitBlock hit block
	 * @param hitBlockFace git block face
	 */
	public static boolean createFireOrIgnite(Block hitBlock, BlockFace hitBlockFace, @Nullable Entity ignitingEntity, BlockIgniteEvent.IgniteCause cause) {
		if (!hitBlock.isBurnable() && !hitBlock.isReplaceable() && !hitBlock.getRelative(hitBlockFace).getRelative(BlockFace.DOWN).getType().isSolid())
			return false;

		if (hitBlock.getType() == Material.TNT) {
			hitBlock.setType(Material.AIR);
			hitBlock.getWorld().spawn(hitBlock.getLocation().center(), TNTPrimed.class, tnt -> {
				Entity source = ignitingEntity;
				if (source instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter)
					source = shooter;
				tnt.setSource(source);
			});
			if (ignitingEntity != null && Tag.ENTITY_TYPES_ARROWS.isTagged(ignitingEntity.getType()))
				ignitingEntity.remove();
			return true;
		}
		if (tryToLit(hitBlock)) return true;

		if (hitBlock.isReplaceable()) {
			var igniteEvent = new BlockIgniteEvent(hitBlock, cause, ignitingEntity);
			if (!igniteEvent.callEvent()) return false;

			Material fireType = Tag.SOUL_FIRE_BASE_BLOCKS.isTagged(hitBlock.getRelative(BlockFace.DOWN).getType()) ? Material.SOUL_FIRE : Material.FIRE;
			hitBlock.setType(fireType);
			return true;
		}

		hitBlock = hitBlock.getRelative(hitBlockFace);
		if (tryToLit(hitBlock)) return true;

		if (!(
			hitBlock.isEmpty()
			|| Tag.CORAL_PLANTS.isTagged(hitBlock.getType())
			|| replaceableByFire.contains(hitBlock.getType())
		)) return false;

		var igniteEvent = new BlockIgniteEvent(hitBlock, cause, ignitingEntity);
		if (!igniteEvent.callEvent()) return false;

		Material fireType = Tag.SOUL_FIRE_BASE_BLOCKS.isTagged(hitBlock.getRelative(BlockFace.DOWN).getType()) ? Material.SOUL_FIRE : Material.FIRE;
		hitBlock.setType(fireType);
		if (hitBlockFace == BlockFace.UP) return true;
		if (hitBlock.getRelative(BlockFace.DOWN).isSolid()) return true;
		if (!(hitBlock.getBlockData() instanceof Fire fire)) return true; // Note: soul fire is not Fire

		for (BlockFace face : fire.getAllowedFaces()) {
			if (hitBlock.getRelative(face).getType().isBurnable())
				fire.setFace(face, true);
		}
		hitBlock.setBlockData(fire);
		return true;
	}

	private static boolean tryToLit(Block block) {
		BlockData blockData = block.getBlockData();
		if (blockData instanceof Campfire campfire && !campfire.isLit()) {
			campfire.setLit(true);
			block.setBlockData(blockData);
			return true;
		}
		if (blockData instanceof Candle candle && !candle.isLit()) {
			candle.setLit(true);
			block.setBlockData(blockData);
			// Workaround candles getting unlit
			MiscMe.scheduler().sync(() -> {
				if (block.getType() == blockData.getMaterial())
					block.setBlockData(blockData);
			}, 1L);
			return true;
		}
		if (Tag.CANDLE_CAKES.isTagged(block.getType()) && blockData instanceof Lightable lightable && !lightable.isLit()) {
			lightable.setLit(true);
			block.setBlockData(blockData);
			// Workaround candles getting unlit
			MiscMe.scheduler().sync(() -> {
				if (block.getType() == blockData.getMaterial())
					block.setBlockData(blockData);
			}, 1L);
			return true;
		}
		return false;
	}

}
