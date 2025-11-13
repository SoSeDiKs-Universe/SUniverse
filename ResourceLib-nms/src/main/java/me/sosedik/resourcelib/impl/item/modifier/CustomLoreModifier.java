package me.sosedik.resourcelib.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class CustomLoreModifier extends ItemModifier {

	private static final String LORE_TAG = "custom_lore_translatable";
	private static final String LORE_COMPONENTS_TAG = "custom_lore_components";

	public CustomLoreModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		boolean componentsLore = NBT.get(contextBox.getItem(), nbt -> {
			if (!nbt.hasTag(LORE_COMPONENTS_TAG)) return false;

			Iterable<String> lores = nbt.getStringList(LORE_COMPONENTS_TAG);
			lores.forEach(str -> {
				Component lore = GsonComponentSerializer.gson().deserialize(str)
					.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
					.colorIfAbsent(NamedTextColor.GRAY);
				contextBox.addLore(lore);
			});
			return true;
		});
		if (componentsLore) return ModificationResult.OK;

		String loreKey = NBT.get(contextBox.getItem(), nbt -> (String) nbt.getOrNull(LORE_TAG, String.class));
		if (loreKey == null) {
			NamespacedKey key = contextBox.getInitialType().getKey();
			if (NamespacedKey.MINECRAFT.equals(key.namespace())) return ModificationResult.PASS;

			loreKey = "item." + key.getNamespace() + "." + key.getKey() + ".lore";
		}

		Component[] lores = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale())).getMessagesIfExists(loreKey);
		if (lores == null) return ModificationResult.PASS;

		for (int i = 0; i < lores.length; i++) {
			lores[i] = lores[i]
				.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				.colorIfAbsent(NamedTextColor.GRAY);
		}

		contextBox.addLore(List.of(lores));

		return ModificationResult.OK;
	}

	public static ItemStack lored(ItemStack item, String key) {
		NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setString(LORE_TAG, key));
		return item;
	}

	public static ItemStack lored(ItemStack item, List<Component> lores) {
		NBT.modify(item, nbt -> {
			ReadWriteNBTList<String> list = nbt.getStringList(LORE_COMPONENTS_TAG);
			lores.forEach(lore -> list.add(GsonComponentSerializer.gson().serialize(lore)));
		});
		return item;
	}

}
