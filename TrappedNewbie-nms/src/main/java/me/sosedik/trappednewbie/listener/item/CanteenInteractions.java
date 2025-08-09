package me.sosedik.trappednewbie.listener.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.impl.item.modifier.ScrapModifier;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.trappednewbie.listener.thirst.DrinkableWater;
import me.sosedik.utilizer.util.DurabilityUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Canteens can take and place water
 */
@NullMarked
public class CanteenInteractions implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;

		Player player = event.getPlayer();
		EquipmentSlot hand = getHand(player);
		if (hand == null) return;

		ItemStack handItem = player.getInventory().getItem(hand);

		if (player.isSneaking()) {
			if (handItem.getType() == TrappedNewbieItems.SCRAP) return;

			Block block = event.getClickedBlock();
			if (block == null) return;
			if (!DrinkableWater.increaseWater(block)) return;

			block.emitSound(Sound.ITEM_BOTTLE_EMPTY, 1F, 0.5F);
			player.clearActiveItem();
			player.swingHand(hand);
			handItem.damage(1, player);
			return;
		}

		Block block = FillingBowlWithWater.getTargetBlock(event);
		if (block == null) return;

		ItemStack canteenItem = handItem.getType() == TrappedNewbieItems.SCRAP ? ScrapModifier.extractScrap(handItem) : handItem.clone();
		int damage = canteenItem.getData(DataComponentTypes.DAMAGE);
		if (damage == 0) return;

		boolean fullFill = block.getType() == Material.WATER;

		if (canteenItem.hasData(DataComponentTypes.DAMAGE)) {
			if (fullFill) {
				canteenItem.setData(DataComponentTypes.DAMAGE, 0);
			} else {
				canteenItem.setData(DataComponentTypes.DAMAGE, damage - 1);
			}
		}

		var blockThirstData = ThirstData.of(block);

		if (DurabilityUtil.getDurability(canteenItem) > 0) {
			var itemThirstData = ThirstData.of(canteenItem);
			canteenItem = blockThirstData.withThirstChance((itemThirstData.thirstChance() + blockThirstData.thirstChance()) / 2).saveInto(canteenItem);
		} else {
			canteenItem = blockThirstData.saveInto(canteenItem);
		}

		player.getInventory().setItem(hand, canteenItem);
		player.swingHand(hand);
		block.emitSound(Sound.AMBIENT_UNDERWATER_EXIT, SoundCategory.PLAYERS, 0.5F, 2F);
//		TrappedNewbieAdvancements.PICK_UP_SOME_WATER.awardAllCriteria(player); // TODO adv
	}

	private @Nullable EquipmentSlot getHand(Player player) {
		if (isCanteen(player.getInventory().getItemInMainHand()))
			return EquipmentSlot.HAND;
		if (isCanteen(player.getInventory().getItemInOffHand()))
			return EquipmentSlot.OFF_HAND;
		return null;
	}

	private boolean isCanteen(ItemStack item) {
		if (item.getType() == TrappedNewbieItems.SCRAP)
			item = ScrapModifier.extractScrap(item);
		return TrappedNewbieTags.CANTEENS.isTagged(item.getType());
	}

}
