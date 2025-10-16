package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.listener.BlockStorage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Knifes can create totem bases from logs
 */
// MCCheck: 1.21.10, new wood types
@NullMarked
public class KnifeCarvesTotemBases implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Material totemBase = switch (block.getType()) {
			case ACACIA_LOG -> TrappedNewbieItems.ACACIA_TOTEM_BASE;
			case BIRCH_LOG -> TrappedNewbieItems.BIRCH_TOTEM_BASE;
			case CHERRY_LOG -> TrappedNewbieItems.CHERRY_TOTEM_BASE;
			case DARK_OAK_LOG -> TrappedNewbieItems.DARK_OAK_TOTEM_BASE;
			case JUNGLE_LOG -> TrappedNewbieItems.JUNGLE_TOTEM_BASE;
			case MANGROVE_LOG -> TrappedNewbieItems.MANGROVE_TOTEM_BASE;
			case OAK_LOG -> TrappedNewbieItems.OAK_TOTEM_BASE;
			case PALE_OAK_LOG -> TrappedNewbieItems.PALE_OAK_TOTEM_BASE;
			case SPRUCE_LOG -> TrappedNewbieItems.SPRUCE_TOTEM_BASE;
			case CRIMSON_STEM -> TrappedNewbieItems.CRIMSON_TOTEM_BASE;
			case WARPED_STEM -> TrappedNewbieItems.WARPED_TOTEM_BASE;
			default -> null;
		};
		if (totemBase == null) return;

		if (tryToCarve(player, block, totemBase, EquipmentSlot.HAND)
			|| tryToCarve(player, block, totemBase, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToCarve(Player player, Block block, Material totemType, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!UtilizerTags.KNIFES.isTagged(item.getType())) return false;

		player.swingHand(hand);
		item.damage(1, player);
		block.setType(totemType);
		BlockStorage.initBlock(block, null);
		block.emitSound(Sound.ITEM_AXE_STRIP, 1F, 0.9F + (float) Math.random() * 0.2F);

		TrappedNewbieAdvancements.MAKE_A_TOTEM_BASE.awardAllCriteria(player);

		return true;
	}

}
