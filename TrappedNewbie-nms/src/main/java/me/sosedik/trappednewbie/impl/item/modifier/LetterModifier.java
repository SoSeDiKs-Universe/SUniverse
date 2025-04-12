package me.sosedik.trappednewbie.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

/**
 * Letter-specific visuals
 */
@NullMarked
public class LetterModifier extends ItemModifier {

	public static final String CONTENTS_TAG = "contents";
	public static final String TYPE_TAG = "type";

	public LetterModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != TrappedNewbieItems.LETTER) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		boolean hasContents = NBT.get(item, nbt -> (boolean) nbt.hasTag(CONTENTS_TAG));
		LetterType type = NBT.get(item, nbt -> (LetterType) nbt.getOrDefault(TYPE_TAG, LetterType.LETTER));
		if (type == LetterType.LETTER && !hasContents) return ModificationResult.PASS;

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		if (contextBox.getContextType().hasVisibleName()) {
			Component itemName = messenger.getMessage(hasContents ? type.localeClosed : type.localeOpen);
			item.setData(DataComponentTypes.ITEM_NAME, itemName);
		}
		if (contextBox.getContextType().hasVisibleLore() && hasContents && isFriendshipLetter(item)) {
			contextBox.addLore(messenger.getMessage("item.modifier.letter.friendship"));
		}
		item.setData(DataComponentTypes.ITEM_MODEL, hasContents ? type.modelClosed : type.modelOpen);

		return ModificationResult.OK;
	}

	private boolean isFriendshipLetter(ItemStack item) {
		ItemStack[] contents = NBT.get(item, nbt -> (ItemStack[]) nbt.getItemStackArray(CONTENTS_TAG));
		if (contents == null) return false;
		if (contents.length != 1) return false;

		ItemStack contentItem = contents[0];
		if (contentItem.getType() != Material.PLAYER_HEAD) return false;

		return contentItem.hasData(DataComponentTypes.PROFILE);
	}

	public enum LetterType {
		LETTER(""),
		STAR;

		private final String localeOpen;
		private final String localeClosed;
		private final NamespacedKey modelOpen;
		private final NamespacedKey modelClosed;

		LetterType() {
			this("_letter");
		}

		LetterType(String suffix) {
			String mapping = name().toLowerCase(Locale.US) + suffix;
			this.localeOpen = "item." + TrappedNewbie.NAMESPACE + "." + mapping + ".name";
			this.localeClosed = "item." + TrappedNewbie.NAMESPACE + "." + mapping + "_closed.name";
			this.modelOpen = ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey(mapping));
			this.modelClosed = ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey(mapping + "_closed"));
		}

	}

}
