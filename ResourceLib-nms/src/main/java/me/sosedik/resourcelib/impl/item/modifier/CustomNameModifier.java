package me.sosedik.resourcelib.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class CustomNameModifier extends ItemModifier {

	private static final String NAME_LOCALE_TAG = "custom_name_locale";
	private static final String NAME_COMPONENT_TAG = "custom_name_component";

	public CustomNameModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContext().getContextType().hasVisibleName()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();

		boolean componentName = NBT.get(item, nbt -> {
			if (!nbt.hasTag(NAME_COMPONENT_TAG)) return false;

			Component name = GsonComponentSerializer.gson().deserialize(nbt.getString(NAME_COMPONENT_TAG))
				.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
			item.setData(DataComponentTypes.ITEM_NAME, name);
			return true;
		});
		if (componentName) return ModificationResult.OK;

		String nameKey = NBT.get(contextBox.getItem(), nbt -> (String) nbt.getOrNull(NAME_LOCALE_TAG, String.class));
		if (nameKey == null) {
			NamespacedKey key = contextBox.getInitialType().getKey();
			if (NamespacedKey.MINECRAFT.equals(key.namespace())) return ModificationResult.PASS;

			nameKey = "item." + key.getNamespace() + "." + key.getKey() + ".name";
		}

		Component name = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()))
			.getMessageIfExists(nameKey);
		if (name == null) return ModificationResult.PASS;

		item.setData(DataComponentTypes.ITEM_NAME, name);

		return ModificationResult.OK;
	}

	public static ItemStack named(ItemStack item, String key) {
		NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setString(NAME_LOCALE_TAG, key));
		return item;
	}

	public static ItemStack named(ItemStack item, Component name) {
		NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setString(NAME_COMPONENT_TAG, GsonComponentSerializer.gson().serialize(name)));
		return item;
	}

}
