package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

/**
 * Makes dyed lore fancier, displays color's hex value
 */
@NullMarked
public class FancierDyedLoreModifier extends ItemModifier {

	private static final Component DYED_LORE = Component.translatable("item.dyed", NamedTextColor.GRAY, TextDecoration.ITALIC);

	public FancierDyedLoreModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.DYED_COLOR)) return ModificationResult.PASS;

		Color color = Objects.requireNonNull(item.getData(DataComponentTypes.DYED_COLOR)).color();
		item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(color, false));
		contextBox.addLore(getDyedLore(color));

		return ModificationResult.OK;
	}

	public static Component getDyedLore(TextColor color) {
		return Mini.combine(Component.space(), DYED_LORE, Component.text(color.asHexString(), color, TextDecoration.ITALIC));
	}

}
