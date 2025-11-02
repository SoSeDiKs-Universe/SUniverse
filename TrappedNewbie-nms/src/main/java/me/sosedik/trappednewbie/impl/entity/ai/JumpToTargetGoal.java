package me.sosedik.trappednewbie.impl.entity.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@NullMarked
public class JumpToTargetGoal implements Goal<Mob> {

	public static final GoalKey<Mob> JUMP_TO_TARGET = GoalKey.of(Mob.class, TrappedNewbie.trappedNewbieKey("jump_to_target"));

	/**
	 * Max distance an entity can be from edge for AI to consider going to it
	 */
	private static final double EDGE_DETECTION_DISTANCE = 3.75;
	/**
	 * The amount of points to check between the entity and the target
	 */
	private static final int DETECTION_POINTS = (int) Math.floor(EDGE_DETECTION_DISTANCE * 8);
	/**
	 * Max ticks to try to jump before resetting
	 */
	private static final int MAX_TICKS_ATTEMPT_NAVIGATION = 40;
	/**
	 * Valid jumping angles
	 */
	private static final int[] JUMP_ANGLES = {0, 5, -5, 10, -10, 15, -15, 20, -20, 25, -25, 30, -30, 35, -35, 40, -40, 45, -45, 50, -50};

	private final Mob entity;
	private @Nullable JumpData jumpData;
	private boolean active = false;

	public JumpToTargetGoal(Mob entity) {
		this.entity = entity;
	}

	private static class JumpData {

		private final Location jumpFrom;
		private final Location jumpTo;
		private double height = 0.5;
		private int ticksAttempted = 0;

		JumpData(Location jumpFrom, Location jumpTo) {
			this.jumpFrom = jumpFrom;
			this.jumpTo = jumpTo;
		}

	}

	@Override
	public boolean shouldActivate() {
		if (this.jumpData != null) return true;
		if (this.entity.isInsideVehicle()) return false;
		if (!this.entity.isOnGround()) return false;

		LivingEntity targetMob = this.entity.getTarget();
		if (targetMob == null) return false;

		if (this.entity.getPathfinder().hasPath()) return false;

		Location targetLoc = targetMob.getLocation();
		Location entityLoc = this.entity.getLocation();
		Vector directionToTarget = targetLoc.toVector().subtract(entityLoc.toVector()).normalize();

		for (int angle : JUMP_ANGLES) {
			Vector jumpDirection = directionToTarget.clone().setY(0).rotateAroundY(Math.toRadians(angle)).normalize();

			Location endPos = entityLoc.clone().center(MathUtil.getDecimalPartAbs(entityLoc.getY())).add(jumpDirection.clone().multiply(EDGE_DETECTION_DISTANCE));
			List<Map.Entry<Location, BlockType>> gaps = interpolateLine(entityLoc, endPos).stream().map(loc -> Map.entry(loc, getNode(loc))).toList();

			Location edgePos = null;
			Location landPos = null;

			for (int i = 1; i < gaps.size(); i++) {
				Map.Entry<Location, BlockType> gap1 = gaps.get(i - 1);
				Map.Entry<Location, BlockType> gap2 = gaps.get(i);
				if (edgePos == null && gap1.getValue() == BlockType.WALKABLE && gap2.getValue() == BlockType.PASSABLE_OBSTACLE) {
					edgePos = gap1.getKey();
				} else if (edgePos != null && gap1.getValue() == BlockType.PASSABLE_OBSTACLE && gap2.getValue() != BlockType.PASSABLE_OBSTACLE) {
					landPos = gap2.getKey();
				}
				if (edgePos != null && landPos != null) break;
			}
			if (edgePos == null) continue;
			if (landPos == null) continue;

			Location jumpLoc = landPos.clone().center(0);
			if (LocationUtil.isTrulySolid(this.entity, jumpLoc.getBlock())) {
				if (LocationUtil.isTrulySolid(this.entity, jumpLoc.getBlock().getRelative(BlockFace.UP)))
					continue;
			} else if (LocationUtil.isTrulySolid(this.entity, jumpLoc.getBlock().getRelative(BlockFace.DOWN))) {
				jumpLoc = jumpLoc.shiftTowards(BlockFace.DOWN);
			} else if (LocationUtil.isTrulySolid(this.entity, jumpLoc.getBlock().getRelative(BlockFace.DOWN, 2))) {
				jumpLoc = jumpLoc.shiftTowards(BlockFace.DOWN, 2);
			}
			double height = calculateNiceHeight(jumpLoc.toVector(), landPos.toVector());

			if (!LocationUtil.isTrulySolid(this.entity, jumpLoc.getBlock())) continue;
			if (LocationUtil.isTrulySolid(this.entity, jumpLoc.getBlock().getRelative(BlockFace.UP))) continue;
			if (LocationUtil.isTrulySolid(this.entity, jumpLoc.getBlock().getRelative(BlockFace.UP, 2))) continue;
			if (LocationUtil.isTrulySolid(this.entity, jumpLoc.getBlock().getRelative(BlockFace.UP, 3))) continue;

			jumpLoc = jumpLoc.center(1);

			this.jumpData = new JumpData(edgePos, jumpLoc);
			this.jumpData.height = height;
			return true;
		}
		return false;
	}

	private double calculateNiceHeight(Vector start, Vector target) {
		double distance = start.clone().setY(0).distance(target.clone().setY(0));
		double height = distance / 1.2;
		if (start.getY() < target.getY()) height += target.getY() - start.getY();
		return height;
	}

	@Override
	public void tick() {
		if (this.jumpData == null) return;
		if (this.active) return;

		if (++this.jumpData.ticksAttempted > MAX_TICKS_ATTEMPT_NAVIGATION) {
			this.entity.getPathfinder().stopPathfinding();
			this.jumpData = null;
			return;
		}

		Location entityLocation = this.entity.getLocation();
		if (!entityLocation.isBlockSame(this.jumpData.jumpFrom)) {
			if (!this.entity.getPathfinder().moveTo(this.jumpData.jumpFrom, EntityUtil.getAttackSpeedBonus(this.entity)))
				this.jumpData = null;
			return;
		}

		this.active = true;
		this.entity.setFrictionState(TriState.FALSE);

		jumpToLocation(this.jumpData.jumpTo.clone().center(0), this.jumpData.height);
	}

	/**
	 * Calls a function that linearly interpolates between two points. Includes both ends of the line.
	 * Callback returns the position and the point number from 1 to points.
	 */
	private List<Location> interpolateLine(Location start, Location end) {
		int points = DETECTION_POINTS;
		List<Location> vectors = new ArrayList<>();
		Vector dir = end.clone().subtract(start).multiply(1D / (points - 1D)).toVector();
		Location pos = start.clone();
		vectors.add(pos);
		for (int i = 0; i < points - 2; i++) {
			pos = pos.clone().add(dir);
			vectors.add(pos);
		}
		vectors.add(end.clone());
		return vectors;
	}

	public void jumpToLocation(Location targetLocation, double height) {
		Vector startLocation = this.entity.getLocation().toVector();
		Vector selectedTarget = targetLocation.toVector();

		double drag = 0.02;
		double acceleration = 0.08;
		double inertia = 0.91;

		// Calculate Y velocity to reach the specified height
		double startHeight = 0.1;
		double startVelocity = 0;
		int startTicks = 0;

		while (startHeight < height) {
			startTicks++;
			startVelocity = (startVelocity / (1 - drag)) + acceleration;
			startHeight += startVelocity;
		}

		// Calculate time to end height
		double endHeight = startLocation.getY() + startHeight;
		double endVelocity = 0;
		int endTicks = 0;

		double targetLocationHeight = selectedTarget.getY();

		while (endHeight > targetLocationHeight) {
			endTicks++;
			endVelocity = (endVelocity + acceleration) * (1 - drag);
			endHeight -= endVelocity;
		}

		// Calculate horizontal velocity
		Vector flatEntityLocation = startLocation.clone().setY(0);
		Vector flatTargetLocation = selectedTarget.clone().setY(0);

		double distance = flatEntityLocation.distance(flatTargetLocation);
		double flatVelocity = ((inertia - 1) * distance) / (Math.pow(inertia, startTicks + endTicks - 1D) - 1) * 0.5;
		Vector flatVelocityVector = flatTargetLocation.clone().subtract(flatEntityLocation).normalize().multiply(flatVelocity);

		// Combine velocity vectors
		Vector flingVelocity = flatVelocityVector.clone().setY(startVelocity);
		this.entity.setVelocity(flingVelocity);

		TrappedNewbie.scheduler().sync(task -> {
			if (!this.entity.isValid()) return true;
			if (!this.entity.isOnGround()) return false;

			this.entity.setFrictionState(TriState.NOT_SET);
			this.entity.setVelocity(new Vector(0, this.entity.getVelocity().getY(), 0));

			this.active = false;
			this.entity.getPathfinder().stopPathfinding();
			this.jumpData = null;

			return true;
		}, 5L, 1L);
	}

	private BlockType getNode(Location pos) {
		return getBlockType(pos, 2);
	}

	private BlockType getBlockType(Location pos, int callsLeft) {
		Block block = pos.getBlock();
		Material blockType = block.getType();

		if (blockType == Material.SWEET_BERRY_BUSH
			|| Tag.FIRE.isTagged(blockType)
			|| blockType == Material.WATER
			|| (block.getBlockData() instanceof Campfire campfire && campfire.isLit())
		) return BlockType.PASSABLE_OBSTACLE;

		if (blockType == Material.LAVA
			|| blockType == Material.CACTUS
			|| blockType == Material.HONEY_BLOCK
			|| blockType == Material.MAGMA_BLOCK
		) return BlockType.SOLID_OBSTACLE;

		if (Tag.LEAVES.isTagged(blockType)
			|| Tag.FENCES.isTagged(blockType)
			|| Tag.WALLS.isTagged(blockType)
			|| (block.getBlockData() instanceof Gate gate && !gate.isOpen())
			|| (block.getBlockData() instanceof Door door && !door.isOpen())
			|| (block.isSolid() && MathUtil.getDecimalPartAbs(pos.getY()) < 0.9)
		) return BlockType.BLOCKED;

		BlockType belowType = pos.getBlockY() > pos.getWorld().getMinHeight() && callsLeft > 0
			? getBlockType(pos.clone().shiftTowards(BlockFace.DOWN), callsLeft - 1)
			: BlockType.OPEN;

		return switch (belowType) {
			case BLOCKED -> BlockType.WALKABLE;
			case OPEN, PASSABLE_OBSTACLE, SOLID_OBSTACLE -> BlockType.PASSABLE_OBSTACLE;
			default -> BlockType.OPEN;
		};
	}

	private enum BlockType {

		OPEN,
		BLOCKED,
		SOLID_OBSTACLE,
		PASSABLE_OBSTACLE,
		WALKABLE

	}

	@Override
	public GoalKey<Mob> getKey() {
		return JUMP_TO_TARGET;
	}

	@Override
	public EnumSet<GoalType> getTypes() {
		return EnumSet.of(GoalType.MOVE, GoalType.JUMP);
	}

}
