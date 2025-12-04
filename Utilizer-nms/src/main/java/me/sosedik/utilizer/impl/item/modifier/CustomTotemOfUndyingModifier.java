package me.sosedik.utilizer.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.utilizer.Utilizer;
import net.kyori.adventure.text.Component;
import org.bukkit.EntityEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Displaying custom totem of undying effects
 */
@NullMarked
public class CustomTotemOfUndyingModifier extends ItemModifier {

	private static final Map<UUID, ItemStack> TOTEM_CACHE = new HashMap<>();

	public CustomTotemOfUndyingModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!(contextBox.getContext() instanceof SlottedItemModifierContext ctx)) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;
		if (ctx.getSlot() != InventorySlotHelper.FIRST_HOTBAR_SLOT + player.getInventory().getHeldItemSlot()) return ModificationResult.PASS;

		ItemStack itemStack = TOTEM_CACHE.remove(player.getUniqueId());
		if (itemStack == null) return ModificationResult.PASS;

		itemStack = modifyItem(ctx, player, contextBox.getLocale(), itemStack.clone());
		if (itemStack == null) return ModificationResult.PASS;

		itemStack.setData(DataComponentTypes.ITEM_NAME, Component.empty());

		contextBox.setItem(itemStack);
		player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);
		player.updateInventory();

		return ModificationResult.RETURN;
	}

	@Override
	public boolean skipAir() {
		return false;
	}

	/**
	 * Plays the totem of undying effect
	 *
	 * @param player player
	 * @param item item
	 */
	public static void playTotemEffect(Player player, ItemStack item) {
		if (!item.hasData(DataComponentTypes.DEATH_PROTECTION)) {
			item = item.clone();
			item.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection().build());
		}
		TOTEM_CACHE.put(player.getUniqueId(), item);
		player.sendItem(InventorySlotHelper.FIRST_HOTBAR_SLOT + player.getInventory().getHeldItemSlot(), item);
		Utilizer.scheduler().sync(() -> {
			TOTEM_CACHE.remove(player.getUniqueId());
			player.updateInventory();
		}, 10L);
	}

}
