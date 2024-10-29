package me.sosedik.miscme.listener.entity;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Armor stands can change poses from a redstone signal
 */
public class ArmorStandBedrockPoses implements Listener {

	private static final String STORED_POSE_TAG = "stored_pose";
	private static final String VANILLA_POSE_TAG = "Pose";

	private static final Map<Integer, ArmorStandPose> POSES_BY_CURRENT = Map.ofEntries(
		Map.entry(1, ArmorStandPose.NO_POSE),
		Map.entry(2, ArmorStandPose.SOLEMN_POSE),
		Map.entry(3, ArmorStandPose.ATHENA_POSE),
		Map.entry(4, ArmorStandPose.BRANDISH_POSE),
		Map.entry(5, ArmorStandPose.HONOR_POSE),
		Map.entry(6, ArmorStandPose.ENTERTAIN_POSE),
		Map.entry(7, ArmorStandPose.SALUTE_POSE),
		Map.entry(8, ArmorStandPose.HERO_POSE),
		Map.entry(9, ArmorStandPose.RIPOSTE_POSE),
		Map.entry(10, ArmorStandPose.ZOMBIE_POSE),
		Map.entry(11, ArmorStandPose.CANCAN_A_POSE),
		Map.entry(12, ArmorStandPose.CANCAN_B_POSE),
		Map.entry(13, ArmorStandPose.DEFAULT_POSE)
	);

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onRedstone(@NotNull BlockRedstoneEvent event) {
		Block block = event.getBlock();
		if (!(block.getBlockData() instanceof AnaloguePowerable)) return;

		int current = event.getNewCurrent();

		handleArmorStands(current, block);
		for (BlockFace face : LocationUtil.SURROUNDING_BLOCKS_XZ)
			handleArmorStands(current, block.getRelative(face.getModX(), 1, face.getModZ()));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onArmorStandSpawn(@NotNull EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof ArmorStand armorStand)) return;
		if (!EntityUtil.isNaturallySpawned(armorStand)) return;
		if (!shouldBeCounted(armorStand)) return;

		if (NBT.get(armorStand, nbt -> (boolean) nbt.hasTag(STORED_POSE_TAG))) {
			restorePose(armorStand);
			return;
		}

		Block block = event.getLocation().getBlock();
		int maxCurrent = getRedstonePower(block);
		for (BlockFace face : LocationUtil.SURROUNDING_BLOCKS_XZ) {
			Block target = block.getRelative(face.getModX(), -1, face.getModZ());
			int current = getRedstonePower(target);
			if (current < maxCurrent) continue;
			if (isFacingTowards(target, face.getOppositeFace()))
				maxCurrent = current;
		}
		if (maxCurrent > 0)
			applyPose(maxCurrent, armorStand);
	}

	private boolean shouldBeCounted(@NotNull ArmorStand armorStand) {
		return !armorStand.isMarker() && armorStand.canTick();
	}

	private int getRedstonePower(@NotNull Block block) {
		return block.getBlockData() instanceof AnaloguePowerable powerable ? powerable.getPower() : 0;
	}

	private boolean isFacingTowards(@NotNull Block block, @NotNull BlockFace face) {
		BlockData blockData = block.getBlockData();
		if (blockData instanceof RedstoneWire redstoneWire)
			return redstoneWire.getFace(face) != RedstoneWire.Connection.NONE;
		return blockData instanceof AnaloguePowerable;
	}

	private void handleArmorStands(int newCurrent, @NotNull Block block) {
		block.getLocation().toCenterLocation().getNearbyEntitiesByType(ArmorStand.class, 0.2, this::shouldBeCounted).forEach(armorStand -> {
			if (!armorStand.getLocation().isBlockSame(block.getLocation())) return;
			if (newCurrent == 0) {
				restorePose(armorStand);
				return;
			}
			applyPose(newCurrent, armorStand);
		});
	}

	private void applyPose(int current, @NotNull ArmorStand armorStand) {
		storePose(armorStand);
		ArmorStandPose pose = POSES_BY_CURRENT.getOrDefault(current, MathUtil.getRandom(ArmorStandPose.values()));
		NBT.modify(armorStand, (Consumer<ReadWriteNBT>) nbt -> nbt.mergeCompound(pose.getPose()));
	}

	private void restorePose(@NotNull ArmorStand armorStand) {
		NBT.modifyPersistentData(armorStand, cnbt -> {
			NBT.modify(armorStand, nbt -> {
				ReadWriteNBT pose = nbt.hasTag(STORED_POSE_TAG) ? nbt.getCompound(STORED_POSE_TAG) : ArmorStandPose.DEFAULT_POSE.getPose();
				nbt.mergeCompound(pose);
			});
		});
	}

	private void storePose(@NotNull ArmorStand armorStand) {
		NBT.get(armorStand, nbt -> {
			if (!nbt.hasTag(VANILLA_POSE_TAG)) return;

			NBT.modifyPersistentData(armorStand, cnbt -> {
				cnbt.removeKey(STORED_POSE_TAG);
				cnbt.getOrCreateCompound(STORED_POSE_TAG).mergeCompound(nbt.getCompound(VANILLA_POSE_TAG));
			});
		});
	}

	private enum ArmorStandPose {

		DEFAULT_POSE("{Pose:{Body:[0f,0f,0f],Head:[0f,0f,0f],LeftArm:[-10f,0f,-10f],LeftLeg:[-1f,0f,-1f],RightArm:[-15f,0f,10f],RightLeg:[1f,0f,1f]}}"),
		NO_POSE("{Pose:{Body:[0f,0f,0f],Head:[0f,0f,0f],LeftArm:[0f,0f,0f],LeftLeg:[0f,0f,0f],RightArm:[0f,0f,0f],RightLeg:[0f,0f,0f]}}"),
		SOLEMN_POSE("{Pose:{Body:[0f,0f,2f],Head:[15f,0f,0f],LeftArm:[-30f,15f,15f],LeftLeg:[-1f,0f,-1f],RightArm:[-60f,-20f,-10f],RightLeg:[1f,0f,1f]}}"),
		ATHENA_POSE("{Pose:{Body:[0f,0f,2f],Head:[-5f,0f,0f],LeftArm:[10f,0f,-5f],LeftLeg:[-3f,-3f,-3f],RightArm:[-60f,20f,-10f],RightLeg:[3f,3f,3f]}}"),
		BRANDISH_POSE("{Pose:{Body:[0f,0f,-2f],Head:[-15f,0f,0f],LeftArm:[20f,0f,-10f],LeftLeg:[5f,-3f,-3f],RightArm:[-110f,50f,0f],RightLeg:[-5f,3f,3f]}}"),
		HONOR_POSE("{Pose:{Body:[0f,0f,0f],Head:[-15f,0f,0f],LeftArm:[-110f,35f,0f],LeftLeg:[5f,-3f,-3f],RightArm:[-110f,-35f,0f],RightLeg:[-5f,3f,3f]}}"),
		ENTERTAIN_POSE("{Pose:{Body:[0f,0f,0f],Head:[-15f,0f,0f],LeftArm:[-110f,-35f,0f],LeftLeg:[5f,-3f,-3f],RightArm:[-110f,35f,0f],RightLeg:[-5f,3f,3f]}}"),
		SALUTE_POSE("{Pose:{Body:[0f,0f,0f],Head:[0f,0f,0f],LeftArm:[10f,0f,-5f],LeftLeg:[-1f,0f,-1f],RightArm:[-70f,-40f,0f],RightLeg:[1f,0f,1f]}}"),
		HERO_POSE("{Pose:{Body:[0f,8f,0f],Head:[-4f,67f,0f],LeftArm:[16f,32f,-8f],LeftLeg:[0f,-75f,-8f],RightArm:[-99f,63f,0f],RightLeg:[4f,63f,8f]}}"),
		RIPOSTE_POSE("{Pose:{Body:[0f,0f,0f],Head:[16f,20f,0f],LeftArm:[4f,8f,237f],LeftLeg:[-14f,-18f,-16f],RightArm:[246f,0f,89f],RightLeg:[8f,20f,4f]}}"),
		ZOMBIE_POSE("{Pose:{Body:[0f,0f,0f],Head:[-10f,0f,-5f],LeftArm:[-105f,0f,0f],LeftLeg:[7f,0f,0f],RightArm:[-100f,0f,0f],RightLeg:[-46f,0f,0f]}}"),
		CANCAN_A_POSE("{Pose:{Body:[0f,22f,0f],Head:[-5f,18f,0f],LeftArm:[8f,0f,-114f],LeftLeg:[-111f,55f,0f],RightArm:[0f,84f,111f],RightLeg:[0f,23f,-13f]}}"),
		CANCAN_B_POSE("{Pose:{Body:[0f,-18f,0f],Head:[-10f,-20f,0f],LeftArm:[0f,0f,-112f],LeftLeg:[0f,0f,13f],RightArm:[8f,90f,111f],RightLeg:[-119f,-42f,0f]}}");

		private final ReadWriteNBT pose;

		ArmorStandPose(@NotNull String nbt) {
			this.pose = NBT.parseNBT(nbt);
		}

		public @NotNull ReadWriteNBT getPose() {
			return pose;
		}

	}

}
