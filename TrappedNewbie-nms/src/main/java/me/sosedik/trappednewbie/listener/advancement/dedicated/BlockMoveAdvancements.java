package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.spawner.Spawner;
import org.jspecify.annotations.NullMarked;

/**
 * Advancements for moving on top of blocks
 */
@NullMarked
public class BlockMoveAdvancements implements Listener {

	private static final int FULL_CHEST_AMOUNT = 9 * 3 * 64;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!event.hasChangedBlock()) return;

		Player player = event.getPlayer();
		if (player.isFlying()) return;

		Block block = event.getTo().getBlock();
		block = block.getRelative(BlockFace.DOWN);
		if (block.getType() == Material.CHEST) {
			if (block.getState(false) instanceof Chest chest && chest.getBlockInventory().contains(Material.SKELETON_SKULL, FULL_CHEST_AMOUNT))
				TrappedNewbieAdvancements.CHESTFUL_OF_SKELETON_SKULLS.awardAllCriteria(player);
		} else if (block.getType() == Material.BARREL) {
			if (block.getState(false) instanceof Barrel barrel && barrel.getInventory().contains(Material.GUNPOWDER, FULL_CHEST_AMOUNT))
				TrappedNewbieAdvancements.BARRELFUL_OF_GUNPOWDER.awardAllCriteria(player);
		} else if (block.getType() == Material.SPAWNER) {
			if (!(block.getState(false) instanceof Spawner spawner)) return;

			EntityType spawnedType = spawner.getSpawnedType();
			if (spawnedType == null) return;

			if (spawnedType == EntityType.SPIDER && block.getWorld().hasStructureAt(block.getLocation(), Structure.MANSION))
				TrappedNewbieAdvancements.STAND_ON_A_MANSION_SPIDER_SPAWNER.awardAllCriteria(player);

			if (!UtilizerTags.NATURAL_SPAWNERS.isTagged(spawnedType)) return;

			TrappedNewbieAdvancements.STAND_ON_ALL_NATURAL_SPAWNERS.awardCriteria(player, spawnedType.key().value());
		}
	}

}
