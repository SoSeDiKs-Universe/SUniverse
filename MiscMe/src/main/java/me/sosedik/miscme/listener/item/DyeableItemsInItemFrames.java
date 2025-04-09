package me.sosedik.miscme.listener.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
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
		Player player = event.getPlayer();

		if (!tryToDye(event, itemFrame, item, player, EquipmentSlot.HAND))
			tryToDye(event, itemFrame, item, player, EquipmentSlot.OFF_HAND);
	}

	private boolean tryToDye(PlayerInteractEntityEvent event, ItemFrame itemFrame, ItemStack frameItem, Player player, EquipmentSlot hand) {
		ItemStack handItem = player.getInventory().getItem(hand);
		if (!ImmersiveDyes.isDyingItem(handItem)) return false;

		Material applied = ImmersiveDyes.getApplied(handItem.getType(), frameItem.getType());
		if (applied != null) {
			if (frameItem.getType() == applied) return false;

			event.setCancelled(true);
			player.swingHand(hand);

			itemFrame.setItem(frameItem.withType(applied));

			ImmersiveDyes.playEffect(player, null, itemFrame.getLocation(), null);
			if (!player.getGameMode().isInvulnerable() && Math.random() < ImmersiveDyes.DYE_REDUCE_CHANCE)
				handItem.subtract();

			return true;
		}

		ItemStack appliedItem = ImmersiveDyes.getDyedFromExtras(frameItem, handItem);
		if (appliedItem != null) {
			event.setCancelled(true);
			player.swingHand(hand);

			itemFrame.setItem(appliedItem);

			ImmersiveDyes.playEffect(player, null, itemFrame.getLocation(), null);
			if (!player.getGameMode().isInvulnerable() && Math.random() < ImmersiveDyes.DYE_REDUCE_CHANCE)
				handItem.subtract();

			return true;
		}

		if (!Tag.ITEMS_DYEABLE.isTagged(frameItem.getType())) return true;

		event.setCancelled(true);
		player.swingHand(hand);

		DyeColor dyeColor = handItem.getType() == ImmersiveDyes.CLEARING_MATERIAL ? null : ImmersiveDyes.getDyeColor(handItem);
		Color currentColor = extractColor(frameItem);
		if (currentColor == null && dyeColor == null) return false;

		Color newColor = currentColor == null ? dyeColor.getColor() : (dyeColor == null ? null : currentColor.mixDyes(dyeColor));
		if (currentColor != null && currentColor.equals(newColor)) return false;

		if (newColor == null)
			frameItem.resetData(DataComponentTypes.DYED_COLOR);
		else
			frameItem.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(newColor));
		itemFrame.setItem(frameItem);

		ImmersiveDyes.playEffect(player, null, itemFrame.getLocation(), null);
		if (!player.getGameMode().isInvulnerable() && Math.random() < ImmersiveDyes.DYE_REDUCE_CHANCE)
			handItem.subtract();

		return true;
	}

	private @Nullable Color extractColor(ItemStack item) {
		if (!item.hasData(DataComponentTypes.DYED_COLOR)) return null;
		return Objects.requireNonNull(item.getData(DataComponentTypes.DYED_COLOR)).color();
	}

}
