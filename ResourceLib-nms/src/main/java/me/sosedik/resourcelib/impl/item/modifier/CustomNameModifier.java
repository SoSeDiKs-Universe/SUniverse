package me.sosedik.resourcelib.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CustomNameModifier extends ItemModifier {

	public CustomNameModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleName()) return ModificationResult.PASS;

		Material type = contextBox.getInitialType();
		if (NamespacedKey.MINECRAFT.equals(type.getKey().namespace())) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		NamespacedKey key = type.getKey();
		Component name = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()))
				.getMessageIfExists("item." + key.getNamespace() + "." + key.getKey() + ".name");
		if (name == null) return ModificationResult.PASS;

		item.setData(DataComponentTypes.ITEM_NAME, name);

		return ModificationResult.OK;
	}

}
