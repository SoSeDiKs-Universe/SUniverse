package me.sosedik.miscme.listener.projectile;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Candle;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Burning projectiles create fire upon landing
 */
@NullMarked
// MCCheck 1.21.4, new replaceable by fire blocks
public class BurningProjectileCreatesFire implements Listener {

	private static final List<Material> replaceableByFire = List.of(
		Material.SHORT_GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN,
		Material.DEAD_BUSH
	);

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFireProjectileLand(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile.getFireTicks() <= 0) return;

		Entity hitEntity = event.getHitEntity();
		if (hitEntity != null) {
			if (hitEntity instanceof ExplosiveMinecart explosiveMinecart && !explosiveMinecart.isIgnited()) {
				explosiveMinecart.ignite();
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

		createFire(hitBlock, hitBlockFace);
	}

	/**
	 * Creates fire at location
	 *
	 * @param hitBlock hit block
	 * @param hitBlockFace git block face
	 */
	public static void createFire(Block hitBlock, BlockFace hitBlockFace) {
		if (!hitBlock.getType().isBurnable() && !hitBlock.getRelative(hitBlockFace).getRelative(BlockFace.DOWN).getType().isSolid())
			return;

		if (hitBlock.getType() == Material.TNT) {
			hitBlock.setType(Material.AIR);
			hitBlock.getWorld().spawn(hitBlock.getLocation().center(), TNTPrimed.class);
			return;
		}
		if (tryToLit(hitBlock)) return;

		hitBlock = hitBlock.getRelative(hitBlockFace);
		if (tryToLit(hitBlock)) return;

		if (!(
			hitBlock.getType().isEmpty()
			|| Tag.CORAL_PLANTS.isTagged(hitBlock.getType())
			|| replaceableByFire.contains(hitBlock.getType())
		)) return;

		hitBlock.setType(Material.FIRE);
		if (hitBlockFace == BlockFace.UP) return;
		if (!(hitBlock.getBlockData() instanceof Fire fire)) return;

		for (BlockFace face : fire.getAllowedFaces()) {
			if (hitBlock.getRelative(face).getType().isBurnable())
				fire.setFace(face, true);
		}
		hitBlock.setBlockData(fire);
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
			return true;
		}
		if (Tag.CANDLE_CAKES.isTagged(block.getType()) && blockData instanceof Lightable lightable && !lightable.isLit()) {
			lightable.setLit(true);
			block.setBlockData(blockData);
			return true;
		}
		return false;
	}

}
