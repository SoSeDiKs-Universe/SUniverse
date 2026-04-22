package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.packet.EntityEquipmentPacketContext;
import me.sosedik.trappednewbie.api.item.tinker.SlingshotData;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.listener.item.BowArrowCache;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SlingshotArrowModifier extends ItemModifier {

	public SlingshotArrowModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != TrappedNewbieItems.SLINGSHOT) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();

		Player viewer = contextBox.getViewer();

		boolean fromCache = false;
		LivingEntity target;
		ItemModifierContext rootContext = contextBox.getContext().getRootContext();
		if (rootContext instanceof EntityEquipmentPacketContext ctx) {
			if (!(ctx.getEntity() instanceof LivingEntity other)) {
				SlingshotData.fromSlingshot(item, null).saveToCustomModelData(item, false);
				return ModificationResult.PASS;
			}
			target = other;
			fromCache = true;
		} else if (rootContext instanceof SlottedItemModifierContext ctx) {
			target = viewer;
			if (viewer != null && (ctx.getSlot() == InventorySlotHelper.OFF_HAND || ctx.getSlot() - InventorySlotHelper.FIRST_HOTBAR_SLOT == viewer.getInventory().getHeldItemSlot()))
				fromCache = true;
		} else {
			target = viewer;
		}

		ItemStack projectile = null;
		if (fromCache && target instanceof Player playerTarget && target.hasActiveItem()) {
			ItemStack activeItem = target.getActiveItem();
			if (activeItem.getType() == contextBox.getInitialType())
				projectile = BowArrowCache.getLastCachedProjectile(playerTarget, TrappedNewbieItems.SLINGSHOT);
		}

		if (projectile != null) {
			if (projectile.hasData(DataComponentTypes.DYED_COLOR))
				item.setData(DataComponentTypes.DYED_COLOR, projectile.getData(DataComponentTypes.DYED_COLOR));
			if (projectile.hasData(DataComponentTypes.POTION_CONTENTS))
				item.setData(DataComponentTypes.POTION_CONTENTS, projectile.getData(DataComponentTypes.POTION_CONTENTS));
		}

		SlingshotData.fromSlingshot(item, projectile).saveToCustomModelData(item, true);

		return ModificationResult.OK;
	}

}
