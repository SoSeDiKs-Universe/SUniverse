package me.sosedik.delightfulfarming.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Fakes berries to prevent placing them onto blocks
 */
@NullMarked
public class BerriesModifier extends ItemModifier {

	public BerriesModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		Material type = contextBox.getInitialType();
		if (type != Material.SWEET_BERRIES && type != Material.GLOW_BERRIES) return ModificationResult.PASS;

		if (contextBox.getItem().getType() != type) return ModificationResult.PASS;

		contextBox.setType(Material.APPLE);

		ItemStack item = contextBox.getItem();
		item.setData(DataComponentTypes.ITEM_NAME, Component.translatable(type.translationKey()));
		item.setData(DataComponentTypes.ITEM_MODEL, type.key());

		return ModificationResult.OK;
	}

}
