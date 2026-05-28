package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * Curses make item's base color name red
 */
@NullMarked
public class RedCurseNameModifier extends ItemModifier {

	public RedCurseNameModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		Set<Enchantment> enchantments = item.getEnchantments().keySet();
		if (enchantments.isEmpty()) return ModificationResult.PASS;

		for (Enchantment enchantment : enchantments) {
			if (!enchantment.isCursed())
				return ModificationResult.PASS;
		}

		boolean changed = false;
		Component component = item.getData(DataComponentTypes.ITEM_NAME);
		if (component != null && component.color() == null) {
			changed = true;
			component = component.color(NamedTextColor.RED);
			item.setData(DataComponentTypes.ITEM_NAME, component);
		}
		component = item.getData(DataComponentTypes.CUSTOM_NAME);
		if (component != null && component.color() == null) {
			changed = true;
			component = component.color(NamedTextColor.RED);
			item.setData(DataComponentTypes.CUSTOM_NAME, component);
		}

		return changed ? ModificationResult.OK : ModificationResult.PASS;
	}

}
