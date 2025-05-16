package me.sosedik.trappednewbie.misc;

import com.destroystokyo.paper.MaterialTags;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.event.player.PlayerTargetBlockEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.listener.block.SoftBlockHandBreaking;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

/**
 * Switches between survival and adventure depending on circumstances
 */
@NullMarked
public class GameModeSwitcherTask extends BukkitRunnable {

	private final Player player;

	public GameModeSwitcherTask(Player player) {
		this.player = player;
		runTaskTimer(TrappedNewbie.instance(), 1L, 1L);
	}

	@Override
	public void run() {
		if (!this.player.isOnline()) {
			cancel();
			return;
		}
		if (this.player.isDead()) return;

		GameMode oldGameMode = this.player.getGameMode();
		if (oldGameMode.isInvulnerable()) return;

		GameMode newGameMode = isTargetingAllowed() ? GameMode.SURVIVAL : GameMode.ADVENTURE;
		if (oldGameMode == newGameMode) return;

		boolean wasFlying = this.player.isFlying(); // GameMode change resets flying state :f
		this.player.setGameMode(newGameMode);
		if (wasFlying) {
			this.player.setAllowFlight(true);
			this.player.setFlying(true);
		}
	}

	private boolean isTargetingAllowed() {
		if (GhostyPlayer.isGhost(this.player)) return false;

		ItemStack mainHand = this.player.getInventory().getItemInMainHand();
		if (isPlaceable(mainHand)) return true;

		ItemStack offHand = this.player.getInventory().getItemInOffHand();
		if (isPlaceable(offHand)) return true;

		Block targetBlock = this.player.getTargetBlockExact(5);
		if (targetBlock == null) return false;

		BlockFace targetBlockFace = this.player.getTargetBlockFace(5);
		if (targetBlockFace == null) return false;

		Material blockType = targetBlock.getType();
		if (isInteractable(blockType)) return true;
		if (isApplicableTool(blockType, mainHand)) return true;
		if (targetBlockFace == BlockFace.UP && UtilizerTags.TILLABLES.isTagged(blockType)) {
			if (MaterialTags.HOES.isTagged(mainHand)) return true;
			if (MaterialTags.HOES.isTagged(offHand)) return true;
		}
		if (Tag.RAILS.isTagged(blockType)) {
			if (MaterialTags.MINECARTS.isTagged(mainHand)) return true;
			if (MaterialTags.MINECARTS.isTagged(offHand)) return true;
		}
		if (blockType == Material.FARMLAND) return true;
		if (mainHand.getType() == Material.BONE_MEAL) return true;
		if (offHand.getType() == Material.BONE_MEAL) return true;
		if (Tag.JUNGLE_LOGS.isTagged(blockType) && mainHand.getType() == Material.COCOA_BEANS) return true;
		if (!new PlayerTargetBlockEvent(this.player, targetBlock, targetBlockFace).callEvent()) return true;

		Entity entityTarget = this.player.getTargetEntity(5);
		if (entityTarget == null) return false;
		// Armor stands can't be broken in adventure
		return entityTarget.getType() == EntityType.ARMOR_STAND;
	}

	private boolean isInteractable(Material blockType) {
		if (blockType.isInteractable()) return true;
		return Tag.FIRE.isTagged(blockType);
	}

	private boolean isPlaceable(ItemStack item) {
		Material itemType = item.getType();
		if (itemType == Material.AIR) return false;
		if (itemType.isBlock()) return true;
		if (Tag.ITEMS_BOATS.isTagged(itemType)) return true;
		if (MaterialTags.FISH_BUCKETS.isTagged(itemType)) return true;
		if (Tag.ITEMS_EGGS.isTagged(itemType)) return true;
		if (TrappedNewbieTags.ROCKS.isTagged(itemType)) return true;
		return switch (itemType) {
			case ARMOR_STAND, ITEM_FRAME, GLOW_ITEM_FRAME, PAINTING, REDSTONE, STRING,
			     BUCKET, WATER_BUCKET, LAVA_BUCKET, POWDER_SNOW_BUCKET, AXOLOTL_BUCKET, TADPOLE_BUCKET,
			     FLINT_AND_STEEL, FIRE_CHARGE, FIREWORK_ROCKET -> true;
			default -> false;
		};
	}

	public static boolean isApplicableTool(Material type, ItemStack tool) {
		if (TrappedNewbieTags.MINEABLE_BY_HAND.isTagged(type)) return true;

		if (tool.isEmpty()) {
			if (Tag.WOODEN_BUTTONS.isTagged(type)) return true;
			if (Tag.WOODEN_PRESSURE_PLATES.isTagged(type)) return true;
		}

		Material toolType = tool.getType();
		if (TrappedNewbieTags.ROCKS.isTagged(toolType) && SoftBlockHandBreaking.getConverted(type) != null) return true;

		if (Tag.MINEABLE_AXE.isTagged(type)) return Tag.ITEMS_AXES.isTagged(toolType);
		if (Tag.MINEABLE_PICKAXE.isTagged(type)) return Tag.ITEMS_PICKAXES.isTagged(toolType);
		if (Tag.MINEABLE_SHOVEL.isTagged(type)) return Tag.ITEMS_SHOVELS.isTagged(toolType);
		if (Tag.MINEABLE_HOE.isTagged(type)) return Tag.ITEMS_HOES.isTagged(toolType);
		return true;
	}

}
