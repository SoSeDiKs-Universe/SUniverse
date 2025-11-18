package me.sosedik.miscme.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.listener.item.ColoredShulkerShells;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.key.Key;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ColoredShulkerShellModifier extends ItemModifier {

	private static final Key MODEL_KEY = ResourceLib.storage().getItemModelMapping(MiscMe.miscMeKey("colored_shulker_shell"));

	public ColoredShulkerShellModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (item.getType() != Material.SHULKER_SHELL) return ModificationResult.PASS;
		if (item.hasData(DataComponentTypes.DYED_COLOR)) return ModificationResult.PASS;
		if (item.isDataOverridden(DataComponentTypes.ITEM_MODEL)) return ModificationResult.PASS;

		DyeColor dyeColor = NBT.get(item, nbt -> (DyeColor) nbt.getOrNull(ColoredShulkerShells.COLOR_TAG, DyeColor.class));
		if (dyeColor == null) return ModificationResult.PASS;

		item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(dyeColor.getColor()));

		contextBox.addHiddenComponents(DataComponentTypes.DYED_COLOR);

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		String localeKey = "item." + MiscMe.NAMESPACE + "." + dyeColor.name().toLowerCase(Locale.US) + "_shulker_shell.name";
		item.setData(DataComponentTypes.ITEM_NAME, messenger.getMessage(localeKey));
		item.setData(DataComponentTypes.ITEM_MODEL, MODEL_KEY);

		return ModificationResult.OK;
	}

}
