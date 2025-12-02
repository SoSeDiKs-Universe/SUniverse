package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.packet.EntityEquipmentPacketContext;
import me.sosedik.trappednewbie.api.item.tinker.CrossbowData;
import me.sosedik.trappednewbie.listener.item.BowArrowCache;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class CustomCrossbowArrowModifier extends ItemModifier {

	public CustomCrossbowArrowModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (item.getType() != Material.CROSSBOW) return ModificationResult.PASS;
		if (item.isDataOverridden(DataComponentTypes.ITEM_MODEL)) return ModificationResult.PASS;

		Player viewer = contextBox.getViewer();

		boolean fromCache = false;
		Entity target;
		ItemModifierContext rootContext = contextBox.getContext().getRootContext();
		if (rootContext instanceof EntityEquipmentPacketContext ctx) {
			target = ctx.getEntity();
			fromCache = true;
		} else if (rootContext instanceof SlottedItemModifierContext ctx) {
			target = viewer;
			if (viewer != null && (ctx.getSlot() == InventorySlotHelper.OFF_HAND || ctx.getSlot() - InventorySlotHelper.FIRST_HOTBAR_SLOT == viewer.getInventory().getHeldItemSlot()))
				fromCache = true;
		} else {
			target = viewer;
		}

		ItemStack projectile = null;
		if (item.isDataOverridden(DataComponentTypes.CHARGED_PROJECTILES) && item.hasData(DataComponentTypes.CHARGED_PROJECTILES)) {
			ChargedProjectiles data = item.getData(DataComponentTypes.CHARGED_PROJECTILES);
			assert data != null;
			List<ItemStack> projectiles = data.projectiles();
			if (!projectiles.isEmpty())
				projectile = projectiles.getFirst();
		} else if (fromCache && target instanceof Player playerTarget && playerTarget.hasActiveItem()) {
			ItemStack activeItem = playerTarget.getActiveItem();
			if (item.equals(activeItem))
				projectile = BowArrowCache.getLastCachedProjectile(playerTarget);
		}

		if (projectile != null) {
			if (projectile.hasData(DataComponentTypes.DYED_COLOR))
				item.setData(DataComponentTypes.DYED_COLOR, projectile.getData(DataComponentTypes.DYED_COLOR));
			if (projectile.hasData(DataComponentTypes.POTION_CONTENTS))
				item.setData(DataComponentTypes.POTION_CONTENTS, projectile.getData(DataComponentTypes.POTION_CONTENTS));
		}

		CrossbowData.fromCrossbow(item, projectile).saveToCustomModelData(item, true);

		return ModificationResult.OK;
	}

}
