package me.sosedik.miscme.listener.block;

import me.sosedik.miscme.MiscMe;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Open blocks together!
 */
public class Couplings implements Listener {

	private static final Map<BlockFace, BlockFace[]> TALL_DOOR_FACINGS = Map.of(
		BlockFace.NORTH, new BlockFace[]{BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST, BlockFace.NORTH},
		BlockFace.WEST, new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.WEST},
		BlockFace.EAST, new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.EAST},
		BlockFace.SOUTH, new BlockFace[]{BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.SOUTH}
	);

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onOpen(@NotNull PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Material blockType = block.getType();
		if (Tag.MOB_INTERACTABLE_DOORS.isTagged(blockType)) {
			if (!(block.getBlockData() instanceof Door door)) return;

			Player player = event.getPlayer();
			if (player.isSneaking()) {
				if (event.isBlockInHand()) return;
				if (isEmptyHanded(player)) return;

				event.setCancelled(true);
				switchOpenableState(block, door, Sound.BLOCK_WOODEN_DOOR_OPEN, Sound.BLOCK_WOODEN_DOOR_CLOSE);
				return;
			}

			openNeighbourDoor(block, door);
		} else if (Tag.WOODEN_TRAPDOORS.isTagged(blockType)) {
			if (!(block.getBlockData() instanceof TrapDoor trapDoor)) return;

			Player player = event.getPlayer();
			if (player.isSneaking()) {
				if (event.isBlockInHand()) return;
				if (isEmptyHanded(player)) return;

				event.setCancelled(true);
				switchOpenableState(block, trapDoor, Sound.BLOCK_WOODEN_TRAPDOOR_OPEN, Sound.BLOCK_IRON_TRAPDOOR_CLOSE);
				return;
			}

			if (openTallDoor(block, trapDoor)) {
				event.setCancelled(true);
				return;
			}

			openTrapDoors(block, trapDoor);
		} else if (Tag.FENCE_GATES.isTagged(blockType)) {
			if (!(block.getBlockData() instanceof Gate gate)) return;

			Player player = event.getPlayer();
			if (player.isSneaking()) {
				if (event.isBlockInHand()) return;
				if (isEmptyHanded(player)) return;

				event.setCancelled(true);
				switchOpenableState(block, gate, Sound.BLOCK_FENCE_GATE_OPEN, Sound.BLOCK_FENCE_GATE_CLOSE);
				return;
			}

			// Small delay to fixup the opened gate facing direction
			MiscMe.scheduler().sync(() -> {
				if (!(block.getBlockData() instanceof Gate finalGate)) return;

				finalGate.setOpen(!finalGate.isOpen());
				openGates(block, finalGate);
			}, 1L);
		}
	}

	@EventHandler
	public void onRedstone(@NotNull BlockRedstoneEvent event) {
		int newCurrent = event.getNewCurrent();
		int oldCurrent = event.getOldCurrent();
		if (newCurrent > 7 && oldCurrent > 7) return;
		if (newCurrent <= 7 && oldCurrent <= 7) return;

		Block block = event.getBlock();
		BlockData blockData = block.getBlockData();
		switch (blockData) {
			case Door door -> {
				Block neighbourBlock = getDoorNeighbour(block, door);
				if (!(neighbourBlock.getBlockData() instanceof Door)) {
					// Already powered and opened
					if (door.isOpen() && newCurrent > 7) return;
					// Already closed and no power
					if (!door.isOpen() && newCurrent <= 7) return;

					openTallDoor(block, door);
					return;
				}

				// Don't close if neighbour is powered
				if (newCurrent <= 7 && neighbourBlock.getBlockPower() > 7) {
					event.setNewCurrent(neighbourBlock.getBlockPower());
					return;
				}

				// Edge cases: door is already in intended state (manually set), but calculations expect the "pre-state"
				if (newCurrent == 0 && !door.isOpen()) door.setOpen(true);
				else if (door.isOpen() && newCurrent > 7) door.setOpen(false);
				openNeighbourDoor(block, door);
			}
			case TrapDoor trapDoor -> {
				Block neighbourBlock = block.getRelative(trapDoor.getFacing());
				if (neighbourBlock.getBlockData() instanceof TrapDoor && neighbourBlock.getBlockPower() > 7) {
					event.setNewCurrent(neighbourBlock.getBlockPower());
					return;
				}

				for (BlockFace blockFace : getTrapDoorNeighbourFaces(trapDoor)) {
					neighbourBlock = block.getRelative(blockFace);
					if (neighbourBlock.getBlockData() instanceof TrapDoor && neighbourBlock.getBlockPower() > 7) {
						event.setNewCurrent(neighbourBlock.getBlockPower());
						return;
					}
				}

				openTrapDoors(block, trapDoor);
			}
			case Gate gate -> {
				// Small delay to fixup the opened gate facing direction
				MiscMe.scheduler().sync(() -> {
					if (!(block.getBlockData() instanceof Gate finalGate)) return;

					finalGate.setOpen(!finalGate.isOpen());
					openGates(block, finalGate);
				}, 1L);
			}
			default -> {}
		}
	}

	private boolean isEmptyHanded(@NotNull Player player) {
		PlayerInventory inv = player.getInventory();
		return inv.getItemInMainHand().getType() == Material.AIR && inv.getItemInOffHand().getType() == Material.AIR;
	}

	private void switchOpenableState(@NotNull Block block, @NotNull Openable openable, @NotNull Sound soundOpened, @NotNull Sound soundClosed) {
		boolean open = !openable.isOpen();
		openable.setOpen(open);
		block.setBlockData(openable);
		block.emitSound(open ? soundOpened : soundClosed, 1F, 1F);
	}

	private void openNeighbourDoor(@NotNull Block block, @NotNull Door door) {
		openTallDoor(block, door);
		Block neighbourBlock = getDoorNeighbour(block, door);
		if (!(neighbourBlock.getBlockData() instanceof Door neighbourDoor)) return;
		if (neighbourDoor.getFacing() != door.getFacing()) return;
		if (neighbourDoor.getHinge() == door.getHinge()) return;
		if (door.isOpen() != neighbourDoor.isOpen()) return;

		openTallDoor(neighbourBlock, neighbourDoor);
		neighbourDoor.setOpen(!door.isOpen());
		// Fixup potentially incorrect powered state due to holding the unpowered door open early
		neighbourDoor.setPowered(neighbourBlock.getBlockPower() > 0);
		neighbourBlock.setBlockData(neighbourDoor);
	}

	private void openTallDoor(@NotNull Block doorBlock, @NotNull Door door) {
		Block trapDoorBlock = doorBlock.getRelative(BlockFace.UP, door.getHalf() == Bisected.Half.BOTTOM ? 2 : 1);
		if (openTallTrapdoor(trapDoorBlock, doorBlock, door))
			openTallTrapdoor(trapDoorBlock.getRelative(BlockFace.UP), doorBlock, door);
	}

	private boolean openTallTrapdoor(@NotNull Block trapDoorBlock, @NotNull Block doorBlock, @NotNull Door door) {
		if (!(trapDoorBlock.getBlockData() instanceof TrapDoor trapDoor)) return false;
		if (!trapDoor.isOpen()) return false;
		if (!isCompatible(doorBlock, trapDoorBlock)) return false;

		BlockFace doorFace = door.getFacing();
		BlockFace trapDoorFace = trapDoor.getFacing();

		BlockFace[] faces = TALL_DOOR_FACINGS.get(doorFace);
		int index = (door.getHinge() == Door.Hinge.RIGHT) ? 0 : 2;

		if (door.isOpen()) {
			if (trapDoorFace != faces[index]) return false;
			trapDoor.setFacing(faces[index + 1]);
		} else {
			if (trapDoorFace != faces[index + 1]) return false;
			trapDoor.setFacing(faces[index]);
		}

		trapDoorBlock.setBlockData(trapDoor, false);
		return true;
	}

	private boolean isCompatible(@NotNull Block door, @NotNull Block trapDoor) {
		if (door.getType() == Material.IRON_DOOR) return trapDoor.getType() == Material.IRON_TRAPDOOR;
		if (door.getType().name().contains("COPPER")) return trapDoor.getType().name().contains("COPPER");
		if (Tag.WOODEN_DOORS.isTagged(door.getType())) return Tag.WOODEN_TRAPDOORS.isTagged(trapDoor.getType());
		return false;
	}

	private @NotNull Block getDoorNeighbour(@NotNull Block block, @NotNull Door door) {
		BlockFace face = door.getFacing();
		boolean right = door.getHinge() == Door.Hinge.RIGHT;
		return switch (face) {
			case EAST -> block.getRelative(right ? BlockFace.NORTH : BlockFace.SOUTH);
			case WEST -> block.getRelative(right ? BlockFace.SOUTH : BlockFace.NORTH);
			case NORTH -> block.getRelative(right ? BlockFace.WEST : BlockFace.EAST);
			case SOUTH -> block.getRelative(right ? BlockFace.EAST : BlockFace.WEST);
			default -> {
				MiscMe.logger().warn("[Couplings] Unknown door face: {}", face);
				yield block;
			}
		};
	}

	private void openTrapDoors(@NotNull Block block, @NotNull TrapDoor trapDoor) {
		boolean opened = !trapDoor.isOpen();
		BlockFace face = trapDoor.getFacing();
		BlockFace[] facings = getTrapDoorNeighbourFaces(trapDoor);
		if (facings.length == 0) return;

		BlockFace oppositeFace = trapDoor.getFacing().getOppositeFace();
		openTrapDoor(block.getRelative(face), oppositeFace, opened);

		Block onLeft = block.getRelative(facings[0]);
		Block onRight = block.getRelative(facings[1]);
		int leftovers = 5;
		while (leftovers > 0) {
			if (onLeft == null && onRight == null) return;

			if (onLeft != null) {
				boolean openedLeft = openTrapDoor(onLeft, face, opened);
				openedLeft = openTrapDoor(onLeft.getRelative(face), oppositeFace, opened) || openedLeft;
				if (openedLeft) {
					onLeft = onLeft.getRelative(facings[0]);
					leftovers--;
				} else {
					onLeft = null;
				}
			}

			if (onRight != null) {
				boolean openedRight = openTrapDoor(onRight, face, opened);
				openedRight = openTrapDoor(onRight.getRelative(face), oppositeFace, opened) || openedRight;
				if (openedRight) {
					onRight = onRight.getRelative(facings[1]);
					leftovers--;
				} else {
					onRight = null;
				}
			}
		}
	}

	private boolean openTallDoor(@NotNull Block trapDoorBlock, @NotNull TrapDoor trapDoor) {
		if (!trapDoor.isOpen()) return false;

		Block doorBlock = trapDoorBlock.getRelative(BlockFace.DOWN);
		if (!(doorBlock.getBlockData() instanceof Door door)) return false;
		if (!isCompatible(doorBlock, trapDoorBlock)) return false;

		BlockFace doorFace = door.getFacing();
		BlockFace trapDoorFace = trapDoor.getFacing();

		BlockFace[] faces = TALL_DOOR_FACINGS.get(doorFace);
		int index = (door.getHinge() == Door.Hinge.RIGHT) ? 0 : 2;

		if (door.isOpen()) {
			if (trapDoorFace != faces[index]) return false;
			trapDoor.setFacing(faces[index + 1]);
			door.setOpen(false);
		} else {
			if (trapDoorFace != faces[index + 1]) return false;
			trapDoor.setFacing(faces[index]);
			door.setOpen(true);
		}

		doorBlock.setBlockData(door);
		trapDoorBlock.setBlockData(trapDoor, false);

		Block upperBlock = trapDoorBlock.getRelative(BlockFace.UP);
		if (upperBlock.getBlockData() instanceof TrapDoor upperTrapDoor
				&& upperTrapDoor.isOpen()
				&& isCompatible(doorBlock, upperBlock)) {
			upperTrapDoor.setFacing(trapDoor.getFacing());
			upperBlock.setBlockData(upperTrapDoor, false);
		}

		Block neighbourBlock = getDoorNeighbour(doorBlock, door);
		if (!(neighbourBlock.getBlockData() instanceof Door neighbourDoor)) return true;
		if (neighbourDoor.getFacing() != door.getFacing()) return true;
		if (neighbourDoor.getHinge() == door.getHinge()) return true;
		if (door.isOpen() == neighbourDoor.isOpen()) return true;

		openTallDoor(neighbourBlock, neighbourDoor);
		neighbourDoor.setOpen(door.isOpen());
		neighbourBlock.setBlockData(neighbourDoor);

		return true;
	}

	private boolean openTrapDoor(@NotNull Block block, @NotNull BlockFace facing, boolean opened) {
		if (!(block.getBlockData() instanceof TrapDoor trapDoor)) return false;
		if (trapDoor.isOpen() == opened) return true;
		if (trapDoor.getFacing() != facing) return false;

		trapDoor.setOpen(opened);
		// Fixup potentially incorrect powered state due to holding the unpowered trapdoor open early
		trapDoor.setPowered(block.getBlockPower() > 0);
		block.setBlockData(trapDoor);
		return true;
	}

	private @NotNull BlockFace[] getTrapDoorNeighbourFaces(@NotNull TrapDoor trapDoor) {
		BlockFace face = trapDoor.getFacing();
		if (face == BlockFace.EAST || face == BlockFace.WEST)
			return new BlockFace[]{BlockFace.SOUTH, BlockFace.NORTH};
		if (face == BlockFace.NORTH || face == BlockFace.SOUTH)
			return new BlockFace[]{BlockFace.EAST, BlockFace.WEST};
		return new BlockFace[0];
	}

	private void openGates(@NotNull Block block, @NotNull Gate gate) {
		boolean opened = !gate.isOpen();
		Block upper = block.getRelative(BlockFace.UP);
		Block lower = block.getRelative(BlockFace.DOWN);
		int leftovers = 5;
		while (leftovers > 0) {
			if (upper == null && lower == null) return;

			if (upper != null) {
				if (openGate(upper, gate, opened)) {
					upper = upper.getRelative(BlockFace.UP);
					leftovers--;
				} else {
					upper = null;
				}
			}

			if (lower != null) {
				if (openGate(lower, gate, opened)) {
					lower = lower.getRelative(BlockFace.DOWN);
					leftovers--;
				} else {
					lower = null;
				}
			}
		}
	}

	private boolean openGate(@NotNull Block block, @NotNull Gate gate, boolean opened) {
		if (!(block.getBlockData() instanceof Gate gateBlock)) return false;

		BlockFace gateFacing = gateBlock.getFacing();
		BlockFace neededFacing = gate.getFacing();
		if (gateFacing != neededFacing && gateFacing != neededFacing.getOppositeFace()) return false;

		gateBlock.setOpen(opened);
		gateBlock.setFacing(gate.getFacing());
		block.setBlockData(gateBlock);
		return true;
	}

}
