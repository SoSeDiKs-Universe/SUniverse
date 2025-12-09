package me.sosedik.miscme.listener.item;

import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Disable insta block breaks when using swords (/melee weapons)
 */
@NullMarked
public class NoSwordInstaBreak implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreak(BlockDamageEvent event) {
		if (!event.getInstaBreak()) return;

		ItemStack itemInHand = event.getItemInHand();
		if (!shouldPreventBlockBreak(itemInHand, event.getBlock())) return;

		event.setCancelled(true);
	}

	public static boolean shouldPreventBlockBreak(ItemStack item, Block block) {
		if (Tag.FIRE.isTagged(block.getType())) return false;
		return ItemUtil.isMeleeWeapon(item)
			&& !Tag.ITEMS_AXES.isTagged(item.getType());
	}

}
