package me.sosedik.trappednewbie.listener.block;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * Trees can (and will) fall
 */
@NullMarked
public class TreePhysics implements Listener {

	//@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (!Tag.LOGS.isTagged(block.getType())) return;

		Player player = event.getPlayer();
		if (!Tag.ITEMS_AXES.isTagged(player.getInventory().getItemInMainHand().getType())) return;

		BlockFace fallDirection = player.getTargetBlockFace(EntityUtil.PLAYER_REACH, FluidCollisionMode.NEVER);
		if (fallDirection == null)
			fallDirection = player.getFacing().getOppositeFace();

		List<Block> blocks = getBlocks(block, new ArrayList<>());
		if (blocks.size() < 2) return;

		event.setCancelled(true);
		BlockFace finalFallDirection = fallDirection;
		Location initialLoc = block.getLocation();
		List<FallingBlockData> fallingDisplays = new ArrayList<>();
		for (Block fallingBlock : blocks) {
			BlockData fallData = fallingBlock.getBlockData();
			fallingBlock.setType(Material.AIR);
			BlockDisplay blockDisplay = block.getWorld().spawn(fallingBlock.getLocation(), BlockDisplay.class, display -> {
				display.setPersistent(false);
				display.setBlock(fallData);
				display.setBrightness(new Display.Brightness(0, 12));
				display.setInterpolationDuration(0);
			});
			fallingDisplays.add(new FallingBlockData(blockDisplay));
		}

		double[] totalAngle = {0};
		double[] angle = {3};
		TrappedNewbie.scheduler().sync(task -> {
			if (totalAngle[0] > 90) {
				for (FallingBlockData fallingBlockData : fallingDisplays) {
					BlockDisplay blockDisplay = fallingBlockData.blockDisplay;
					blockDisplay.setInterpolationDelay(0);
					blockDisplay.teleport(blockDisplay.getLocation().addY(-0.1));
				}
			} else {
				totalAngle[0] += angle[0];
				for (FallingBlockData fallingBlockData : fallingDisplays) {
					BlockDisplay blockDisplay = fallingBlockData.blockDisplay;
					tickFall(blockDisplay, initialLoc, fallingBlockData.initialLoc, finalFallDirection, angle[0], fallingBlockData.lastAngle);
				}
			}
			for (FallingBlockData fallingBlockData : fallingDisplays) {
				if (!fallingBlockData.shouldCollide) continue;

				BlockDisplay blockDisplay = fallingBlockData.blockDisplay;
				if (blockDisplay.getLocation().getBlock().isSolid()) {
					tryToPlace(fallingDisplays);
					return true;
				}
			}
			if (totalAngle[0] > 90) {
//				tryToPlace(fallingDisplays);
				return false;
			}

			angle[0] += 0.01;
			return false;
		}, 1L, 1L);

		block.emitSound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.65F, 0.85F);
	}

	private void tryToPlace(List<FallingBlockData> fallingDisplays) {
		fallingDisplays.sort((a, b) -> {
			Material aType = a.blockDisplay.getBlock().getMaterial();
			Material bType = b.blockDisplay.getBlock().getMaterial();

			boolean aIsLog = Tag.LOGS.isTagged(aType);
			boolean bIsLog = Tag.LOGS.isTagged(bType);
			if (aIsLog && !bIsLog) return -1;
			if (!aIsLog && bIsLog) return 1;

			boolean aIsLeaves = Tag.LEAVES.isTagged(aType);
			boolean bIsLeaves = Tag.LEAVES.isTagged(bType);
			if (aIsLeaves && !bIsLeaves) return 1;
			if (!aIsLeaves && bIsLeaves) return -1;

			return 0;
		});

		for (FallingBlockData data : fallingDisplays) {
			BlockDisplay blockDisplay = data.blockDisplay;
			Location loc = blockDisplay.getLocation().addY(0.2);
//			Vector3f translation = blockDisplay.getTransformation().getTranslation();
//			loc.add(translation.x(), translation.y(), translation.z());
			Block block = loc.getBlock();
			if (!block.isReplaceable()) {
				loc = loc.shiftTowards(BlockFace.UP);
				block = loc.getBlock();
			}
			BlockData blockData = blockDisplay.getBlock();
			if (block.isReplaceable()) {
				block.setBlockData(blockData);
			} else {
				var item = ItemStack.of(blockData.getMaterial());
				item.setBlockData(blockData);
				blockDisplay.getWorld().dropItemNaturally(loc, item);
			}
			blockDisplay.remove();
		}
	}

	private void tickFall(BlockDisplay blockDisplay, Location initialLoc, Location blockLoc, BlockFace fallDirection, double angle, float[] lastAngle) {
		if (fallDirection == BlockFace.SOUTH || fallDirection == BlockFace.EAST)
			blockLoc = blockLoc.clone().yaw(180);

		double x = blockLoc.getX();
		double y = blockLoc.getY();
		double z = blockLoc.getZ();
		double a = initialLoc.x();
		double b = initialLoc.y();
		double c = initialLoc.z();

		double xNew;
		double yNew;
		double zNew;
		Transformation transformation;

		if (fallDirection == BlockFace.WEST || fallDirection == BlockFace.NORTH) angle *= -1;
		lastAngle[0] += (float) Math.toRadians(angle);
		float currentRot = -lastAngle[0];

		if (fallDirection.getModX() == 0) {
			xNew = x;
			yNew = (z - c) * Math.sin(currentRot) + (y - b) * Math.cos(currentRot) + b;
			zNew = (z - c) * Math.cos(currentRot) - (y - b) * Math.sin(currentRot) + c;
			if (fallDirection == BlockFace.SOUTH) {
				float rotationX = currentRot;
				transformation = new Transformation(new Vector3f(-1F, 0F, -1F), new Quaternionf().rotationX(rotationX), new Vector3f(1F), new Quaternionf());
			} else { // North
				float rotationX = -currentRot;
				transformation = new Transformation(new Vector3f(), new Quaternionf().rotationX(rotationX), new Vector3f(1F), new Quaternionf());
			}
		} else {
			xNew = (x - a) * Math.cos(currentRot) - (y - b) * Math.sin(currentRot) + a;
			yNew = (x - a) * Math.sin(currentRot) + (y - b) * Math.cos(currentRot) + b;
			zNew = z;
			if (fallDirection == BlockFace.WEST) {
				float rotationZ = currentRot;
				transformation = new Transformation(new Vector3f(), new Quaternionf().rotationZ(rotationZ), new Vector3f(1F), new Quaternionf());
			} else { // East
				float rotationZ = -currentRot;
				transformation = new Transformation(new Vector3f(-1F, 0F, -1F), new Quaternionf().rotationZ(rotationZ), new Vector3f(1F), new Quaternionf());
			}
		}

		blockDisplay.setTransformation(transformation);
		blockDisplay.setInterpolationDelay(0);
		Location centerLocation = blockLoc.clone().x(xNew).z(zNew).y(yNew);
		blockDisplay.teleport(centerLocation);
	}

	private List<Block> getBlocks(Block block, List<Block> blocks) {
		if (!shouldAdd(block, blocks)) return blocks;
		blocks.add(block);
		for (BlockFace blockFace : LocationUtil.SURROUNDING_BLOCKS) {
			Block relativeBlock = block.getRelative(blockFace);
			getBlocks(relativeBlock, blocks);
		}
		getBlocks(block.getRelative(BlockFace.UP), blocks);
		return blocks;
	}

	private boolean shouldAdd(Block relativeBlock, List<Block> blocks) {
		if (blocks.contains(relativeBlock)) return false;
		Material type = relativeBlock.getType();
		if (Tag.LOGS.isTagged(type)) return true;
		if (isTreeAttachedBlock(type)) return true;
		return Tag.LEAVES.isTagged(type) && relativeBlock.getBlockData() instanceof Leaves leaves;// && !leaves.isPersistent();
	}

	private boolean isTreeAttachedBlock(Material type) {
		return type == Material.VINE
			|| type == Material.SNOW
			|| type == Material.COCOA_BEANS
			|| type == Material.BEE_NEST
			|| type == Material.CREAKING_HEART;
	}

	private static class FallingBlockData {

		private final BlockDisplay blockDisplay;
		private final Location initialLoc;
		private final boolean shouldCollide;
		private final float[] lastAngle = {0F};

		FallingBlockData(BlockDisplay blockDisplay) {
			this.blockDisplay = blockDisplay;
			this.initialLoc = blockDisplay.getLocation();
			this.shouldCollide = !Tag.LEAVES.isTagged(blockDisplay.getBlock().getMaterial());
		}

	}

}
