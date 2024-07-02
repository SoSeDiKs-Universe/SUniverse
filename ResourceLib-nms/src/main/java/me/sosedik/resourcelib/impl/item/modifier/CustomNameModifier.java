package me.sosedik.resourcelib.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class CustomNameModifier extends ItemModifier {

	public CustomNameModifier(@NotNull Plugin plugin) {
		super(new NamespacedKey(plugin, "custom_name"));
	}

	@Override
	public @NotNull ModificationResult modify(@NotNull ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleName()) return ModificationResult.PASS;

		Material type = contextBox.getItem().getType();
		if (NamespacedKey.MINECRAFT.equals(type.getKey().namespace())) return ModificationResult.PASS;

		ItemMeta meta = contextBox.getMeta();
		if (meta.hasItemName()) return ModificationResult.PASS;

		NamespacedKey key = type.getKey();
		Component name = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()))
				.getMessageIfExists("item." + key.getNamespace() + "." + key.getKey() + ".name");
		if (name == null) return ModificationResult.PASS;

		meta.itemName(name);

		return ModificationResult.OK;
	}

}
