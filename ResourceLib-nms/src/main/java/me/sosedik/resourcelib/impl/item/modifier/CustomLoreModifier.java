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

import java.util.ArrayList;
import java.util.List;

public class CustomLoreModifier extends ItemModifier {

	public CustomLoreModifier(@NotNull Plugin plugin) {
		super(new NamespacedKey(plugin, "custom_lore"));
	}

	@Override
	public @NotNull ModificationResult modify(@NotNull ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		Material type = contextBox.getItem().getType();
		if (NamespacedKey.MINECRAFT.equals(type.getKey().namespace())) return ModificationResult.PASS;

		ItemMeta meta = contextBox.getMeta();
		if (meta.hasItemName()) return ModificationResult.PASS;

		NamespacedKey key = type.getKey();
		Component[] lores = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()))
				.getMessagesIfExists("item." + key.getNamespace() + "." + key.getKey() + ".lore");
		if (lores == null) return ModificationResult.PASS;

		List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
		assert lore != null;
		lore.addAll(List.of(lores));
		meta.lore(lore);

		return ModificationResult.OK;
	}

}
