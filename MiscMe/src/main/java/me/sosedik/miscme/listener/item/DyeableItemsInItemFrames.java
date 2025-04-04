package me.sosedik.miscme.listener.item;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Tag;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Dyeable items in item frames can be dyed
 */
@NullMarked
public class DyeableItemsInItemFrames implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame itemFrame)) return;

		ItemStack item = itemFrame.getItem();
		if (!Tag.ITEMS_DYEABLE.isTagged(item.getType())) return;

		Player player = event.getPlayer();

		if (tryToDye(event, itemFrame, item, player, EquipmentSlot.HAND))
			tryToDye(event, itemFrame, item, player, EquipmentSlot.OFF_HAND);
	}

	private boolean tryToDye(PlayerInteractEntityEvent event, ItemFrame itemFrame, ItemStack item, Player player, EquipmentSlot hand) {
		ItemStack handItem = player.getInventory().getItem(hand);
		if (!MaterialTags.DYES.isTagged(handItem)) return false;

		event.setCancelled(true);
		player.swingHand(hand);

		DyeColor appliedColor = ImmersiveDyes.getDyeColor(handItem);
		if (appliedColor == null) return false;

		Color currentColor = extractColor(item);
		Color newColor = currentColor == null ? appliedColor.getColor() : currentColor.mixDyes(appliedColor);
		if (newColor.equals(currentColor)) return false;

		item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(newColor, true));
		itemFrame.setItem(item);

		ImmersiveDyes.playEffect(player, null, itemFrame.getLocation(), null);
		if (Math.random() < ImmersiveDyes.DYE_REDUCE_CHANCE)
			handItem.subtract();

		return true;
	}

	private @Nullable Color extractColor(ItemStack item) {
		if (!item.hasData(DataComponentTypes.DYED_COLOR)) return null;
		return Objects.requireNonNull(item.getData(DataComponentTypes.DYED_COLOR)).color();
	}

}
