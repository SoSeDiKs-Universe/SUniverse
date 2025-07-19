package me.sosedik.miscme.listener.projectile;

import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

/**
 * Snowballs create snow upon landing
 */
@NullMarked
public class SnowballCreatesSnow implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball snowball)) return;
		if (event.getHitEntity() != null) return;
		if (snowball.getItem().getType() != Material.SNOWBALL) return;

		Block block = event.getHitBlock();
		BlockFace blockFace = event.getHitBlockFace();
		if (block == null) return;
		if (blockFace == null) return;

		if (block.getType() == Material.SNOW) {
			growSnow(block);
			performStuckCheck(snowball, block);
			return;
		}

		Block upper = block.getRelative(BlockFace.UP);
		if (upper.getType() == Material.SNOW) {
			growSnow(upper);
			performStuckCheck(snowball, upper);
		} else if (block.getType().isSolid() && LocationUtil.isCube(block)) {
			setOrDropSnow(upper, blockFace, snowball.getVelocity());
		} else if (block.getType() == Material.SCAFFOLDING) {
			setOrDropSnow(upper, blockFace, snowball.getVelocity());
		} else if (!block.isLiquid() && block.getType() != Material.CACTUS) {
			setOrDropSnow(block, blockFace, snowball.getVelocity());
		}
	}

	private void growSnow(Block block) {
		if (!(block.getBlockData() instanceof Snow snow)) return;
		block.emitSound(Sound.BLOCK_SNOW_PLACE, 1F, 1F);
		if (snow.getLayers() == snow.getMaximumLayers() - 1) {
			block.setType(Material.SNOW_BLOCK);
		} else {
			snow.setLayers(snow.getLayers() + 1);
			block.setBlockData(snow);
		}
	}

	private void setOrDropSnow(Block block, BlockFace blockFace, Vector preVelocity) {
		if (block.isReplaceable()) {
			block.setType(Material.SNOW);
			block.emitSound(Sound.BLOCK_SNOW_PLACE, 1F, 1F);
			return;
		}
		Vector direction = blockFace.getDirection();
		block.getWorld().spawn(block.getLocation().center().shiftTowards(blockFace, 0.55), Item.class, item -> {
			item.setItemStack(ItemStack.of(Material.SNOWBALL));
			Vector velocity = preVelocity;
			velocity = velocity.subtract(direction.multiply(2 * velocity.dot(direction))).multiply(0.25);
			item.setVelocity(velocity);
		});
	}

	private void performStuckCheck(Snowball snowball, Block block) {
		if (!(snowball.getShooter() instanceof Player player)) return;

		Location playerLoc = player.getLocation();
		Location blockLoc = block.getLocation();
		if (playerLoc.getBlockX() == blockLoc.getBlockX()
				&& playerLoc.getBlockZ() == blockLoc.getBlockZ()
				&& playerLoc.getY() - blockLoc.getBlockY() < 1
		) {
			player.setVelocity(new Vector(0, 0.3, 0));
		}
	}

}
