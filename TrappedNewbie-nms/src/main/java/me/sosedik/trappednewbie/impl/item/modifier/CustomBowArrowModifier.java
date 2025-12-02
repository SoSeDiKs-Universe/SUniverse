package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.packet.EntityEquipmentPacketContext;
import me.sosedik.trappednewbie.api.item.tinker.ArrowData;
import me.sosedik.trappednewbie.api.item.tinker.BowData;
import me.sosedik.trappednewbie.listener.item.BowArrowCache;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CustomBowArrowModifier extends ItemModifier {

	private static final ItemStack BOGGED_BOW = ItemStack.of(Material.BOW);
	private static final ItemStack STRAY_BOW = ItemStack.of(Material.BOW);

	static {
		BowData.defaultData(ArrowData.defaultData(Material.TIPPED_ARROW)).saveToCustomModelData(BOGGED_BOW, false);
		BowData.defaultData(ArrowData.defaultData(Material.TIPPED_ARROW)).saveToCustomModelData(STRAY_BOW, false);
		BOGGED_BOW.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(PotionType.POISON).build());
		STRAY_BOW.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(PotionType.SLOWNESS).build());
	}

	public CustomBowArrowModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (item.getType() != Material.BOW) return ModificationResult.PASS;
		if (item.isDataOverridden(DataComponentTypes.ITEM_MODEL)) return ModificationResult.PASS;

		Player viewer = contextBox.getViewer();

		boolean fromCache = false;
		LivingEntity target;
		ItemModifierContext rootContext = contextBox.getContext().getRootContext();
		if (rootContext instanceof EntityEquipmentPacketContext ctx) {
			if (!(ctx.getEntity() instanceof LivingEntity other)) {
				BowData.fromBow(item, null).saveToCustomModelData(item, false);
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

		if (target != null) {
			if (target.getType() == EntityType.BOGGED) {
				contextBox.setItem(BOGGED_BOW);
				return ModificationResult.RETURN;
			}

			if (target.getType() == EntityType.STRAY) {
				contextBox.setItem(STRAY_BOW);
				return ModificationResult.RETURN;
			}
		}

		ItemStack projectile = null;
		if (fromCache && target instanceof Player playerTarget && target.hasActiveItem()) {
			ItemStack activeItem = target.getActiveItem();
			if (item.equals(activeItem))
				projectile = BowArrowCache.getLastCachedProjectile(playerTarget);
		}

		if (projectile != null) {
			if (projectile.hasData(DataComponentTypes.DYED_COLOR))
				item.setData(DataComponentTypes.DYED_COLOR, projectile.getData(DataComponentTypes.DYED_COLOR));
			if (projectile.hasData(DataComponentTypes.POTION_CONTENTS))
				item.setData(DataComponentTypes.POTION_CONTENTS, projectile.getData(DataComponentTypes.POTION_CONTENTS));
		}

		BowData.fromBow(item, projectile).saveToCustomModelData(item, true);

		return ModificationResult.OK;
	}

}
