package me.sosedik.utilizer.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HiddenTooltipsModifier extends ItemModifier {

	private static final Tag<Material> LIGHT_SOURCES = ItemUtil.itemTag(Utilizer.utilizerKey("no_tooltip_items"));

	public HiddenTooltipsModifier(@NotNull NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public @NotNull ModificationResult modify(@NotNull ItemContextBox contextBox) {
		if (!LIGHT_SOURCES.isTagged(contextBox.getInitialType())) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		item.setData(DataComponentTypes.HIDE_TOOLTIP);
		item.setData(DataComponentTypes.ITEM_NAME, Component.empty());

		return ModificationResult.OK;
	}

}
