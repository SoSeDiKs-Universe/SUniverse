package me.sosedik.miscme.listener.block;

import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.api.math.WorldBlockPosition;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.Powerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * Button can be used as a doorbell
 */
@NullMarked
public class DoorBells implements Listener {

	private static final Map<WorldBlockPosition, Integer> BELLS_IN_USE = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBell(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getPlayer().getGameMode().isInvulnerable()) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Material buttonType = block.getType();
		if (!Tag.BUTTONS.isTagged(buttonType)) return;

		BlockData blockData = block.getBlockData();
		if (!(blockData instanceof FaceAttachable attachable)) return;
		if (!(blockData instanceof Directional directional)) return;
		if (!(blockData instanceof Powerable powerable)) return;

		var worldBlockPosition = WorldBlockPosition.worldPosition(block);
		if (powerable.isPowered() && !BELLS_IN_USE.containsKey(worldBlockPosition)) return;

		BlockFace facing = directional.getFacing();
		boolean attached = switch (attachable.getAttachedFace()) {
			case WALL -> {
				Block base = block.getRelative(facing.getOppositeFace());
				boolean doorOnSide = switch (facing) {
					case WEST, EAST -> isDoor(base, BlockFace.SOUTH) || isDoor(base, BlockFace.NORTH);
					case NORTH, SOUTH -> isDoor(base, BlockFace.WEST) || isDoor(base, BlockFace.EAST);
					default -> false;
				};
				yield doorOnSide || isDoor(base, BlockFace.DOWN) || isTrapdoor(block, BlockFace.DOWN);
			}
			case FLOOR -> {
				Block base = block;
				for (BlockFace blockFace : LocationUtil.SURROUNDING_BLOCKS) {
					if (isTrapdoor(base, blockFace) || isDoor(base, blockFace))
						yield true;
				}
				base = base.getRelative(BlockFace.DOWN);
				for (BlockFace blockFace : LocationUtil.SURROUNDING_BLOCKS) {
					if (isTrapdoor(base, blockFace))
						yield true;
				}
				yield false;
			}
			case CEILING -> {
				for (BlockFace blockFace : LocationUtil.SURROUNDING_BLOCKS) {
					if (isDoor(block, blockFace))
						yield true;
				}
				yield false;
			}
		};
		if (!attached) return;

		int dings = BELLS_IN_USE.getOrDefault(worldBlockPosition, 0) + 1;
		BELLS_IN_USE.put(worldBlockPosition, dings);
		block.emitSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 0.2F);
		if (!powerable.isPowered()) {
			powerable.setPowered(true);
			block.setBlockData(powerable, false);
		}

		MiscMe.scheduler().sync(() -> {
			if (BELLS_IN_USE.get(worldBlockPosition) != dings) return;

			BELLS_IN_USE.remove(worldBlockPosition);
			if (block.getType() != buttonType) return;

			block.emitSound(Sound.BLOCK_NOTE_BLOCK_CHIME, 1F, 0.8F);
			powerable.setPowered(false);
			block.setBlockData(powerable, false);
		}, 15L);
	}

	private boolean isTrapdoor(Block base, BlockFace blockFace) {
		return Tag.TRAPDOORS.isTagged(base.getRelative(blockFace).getType());
	}

	private boolean isDoor(Block base, BlockFace blockFace) {
		return Tag.DOORS.isTagged(base.getRelative(blockFace).getType());
	}

}
