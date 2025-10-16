package me.sosedik.trappednewbie.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.trappednewbie.listener.advancement.AdvancementTrophies;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Custom name and lore for trophies
 */
@NullMarked
public class AdvancementTrophyNameLoreModifier extends ItemModifier {

	public AdvancementTrophyNameLoreModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		String trophyId = NBT.get(item, nbt -> {
			if (!nbt.hasTag(AdvancementTrophies.TROPHY_TAG)) return null;
			return nbt.getString(AdvancementTrophies.TROPHY_TAG);
		});
		if (trophyId == null) return ModificationResult.PASS;

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		Component name = messenger.getMessageIfExists("trophy." + trophyId + ".name");
		Component[] lore = messenger.getMessagesIfExists("trophy." + trophyId + ".lore");
		if (name == null && lore == null) return ModificationResult.PASS;

		if (name != null) {
			name = name.decorationIfAbsent(TextDecoration.BOLD, TextDecoration.State.TRUE);
			if (item.hasData(DataComponentTypes.CUSTOM_NAME))
				item.setData(DataComponentTypes.ITEM_NAME, name);
			else
				item.setData(DataComponentTypes.CUSTOM_NAME, name.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
		}

		if (lore != null)
			contextBox.addLore(lore);

		return ModificationResult.OK;
	}

}
