package me.sosedik.trappednewbie.listener.block;

import me.sosedik.trappednewbie.api.event.player.PlayerTargetBlockEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.misc.BlockBreakTask;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Some blocks can be broken by "hand" in some cases
 */
// MCCheck: 1.21.10, new blocks
@NullMarked
public class SoftBlockHandBreaking implements Listener {

	public SoftBlockHandBreaking() {
		BlockBreakTask.addBreakingRule((task, seconds) -> {
			Material blockType = task.getBlock().getType();
			if (!isRocky(task.getTool())) {
				if (blockType == Material.GRAVEL) return 1.75F;
				if (blockType == Material.SUSPICIOUS_GRAVEL) return 1.75F;
				return null;
			}

			return switch (blockType) {
				case GRASS_BLOCK, PODZOL, MYCELIUM -> 3F;
				case DIRT -> 2.5F;
				case COARSE_DIRT -> 4.5F;
				case GRAVEL, SUSPICIOUS_GRAVEL -> 1.5F;
				default -> null;
			};
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void onTarget(PlayerTargetBlockEvent event) {
		if (getConverted(event.getBlock().getType()) == null) return;

		Player player = event.getPlayer();
		if (!isRocky(player.getInventory().getItemInMainHand())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Material replacement = getConverted(block.getType());
		if (replacement == null) return;

		Player player = event.getPlayer();
		if (!isRocky(player.getInventory().getItemInMainHand())) return;

		event.setCancelled(true);

		BlockData blockData = block.getBlockData();
		SoundGroup soundGroup = block.getBlockSoundGroup();
		block.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, block.getLocation().toCenterLocation(), 50, 0.4, 0.4, 0.4, blockData);
		block.emitSound(soundGroup.getBreakSound(), 1F, 0.8F);

		block.setType(replacement);
		BlockFace blockFace = player.getTargetBlockFace(EntityUtil.PLAYER_REACH);
		if (blockFace == null) blockFace = player.getFacing().getOppositeFace();

		// Continue breaking
		new BlockDamageEvent(player, block, blockFace, player.getInventory().getItemInMainHand(), false).callEvent();
	}

	private boolean isRocky(ItemStack item) {
		return TrappedNewbieTags.ROCKS.isTagged(item.getType())
			|| item.getType() == Material.FLINT;
	}

	public static @Nullable Material getConverted(Material blockType) {
		return switch (blockType) {
			case GRASS_BLOCK, PODZOL, MYCELIUM -> Material.DIRT;
			case DIRT -> Material.COARSE_DIRT;
			case COARSE_DIRT -> Material.GRAVEL;
			default -> null;
		};
	}

}
