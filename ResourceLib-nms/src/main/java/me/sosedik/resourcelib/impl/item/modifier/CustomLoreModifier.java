package me.sosedik.resourcelib.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class CustomLoreModifier extends ItemModifier {

	public CustomLoreModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		Material type = contextBox.getInitialType();
		if (NamespacedKey.MINECRAFT.equals(type.getKey().namespace())) return ModificationResult.PASS;

		NamespacedKey key = type.getKey();
		Component[] lores = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()))
				.getMessagesIfExists("item." + key.getNamespace() + "." + key.getKey() + ".lore");
		if (lores == null) return ModificationResult.PASS;

		contextBox.addLore(List.of(lores));

		return ModificationResult.OK;
	}

}
