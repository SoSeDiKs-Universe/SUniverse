package me.sosedik.utilizer.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.MiscUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.Objects;

/**
 * Custom dye-specific names for custom items
 */
@NullMarked
public class DyedItemNamesModifier extends ItemModifier {

	public DyedItemNamesModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleName()) return ModificationResult.PASS;
		if (!contextBox.getInitialType().isInjected()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.DYED_COLOR)) return ModificationResult.PASS;

		Key key = contextBox.getInitialType().key();
		Color color = Objects.requireNonNull(item.getData(DataComponentTypes.DYED_COLOR)).color();
		DyeColor dyeColor = MiscUtil.closestTo(color);
		String localeKy = "item." + key.namespace() + "." + dyeColor.name().toLowerCase(Locale.US) + "_" + key.value() + ".name";

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		Component message = messenger.getMessageIfExists(localeKy);
		if (message == null) return ModificationResult.PASS;

		item.setData(DataComponentTypes.ITEM_NAME, message);

		return ModificationResult.OK;
	}

}
