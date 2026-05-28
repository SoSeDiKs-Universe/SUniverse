package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * Curses no longer apply enchantment glint
 */
@NullMarked
public class NoCurseGlintModifier extends ItemModifier {

	public NoCurseGlintModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (item.isDataOverridden(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)) return ModificationResult.PASS;

		ItemEnchantments data = item.getData(DataComponentTypes.ENCHANTMENTS);
		if (data == null) return ModificationResult.PASS;

		Set<Enchantment> enchantments = data.enchantments().keySet();
		if (enchantments.isEmpty()) return ModificationResult.PASS;

		for (Enchantment enchantment : enchantments) {
			if (!enchantment.isCursed())
				return ModificationResult.PASS;
		}

		item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);

		return ModificationResult.OK;
	}

}
