package me.sosedik.trappednewbie.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.miscme.impl.item.modifier.BookAuthorOnlineModifier;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Letter-specific visuals
 */
@NullMarked
public class LetterModifier extends ItemModifier {

	public static final String CONTENTS_TAG = "contents";
	private static final String FRIENDSHIP_TAG = "friendship";
	private static final String FRIENDSHIP_FROM_TAG = "friendship_from";
	private static final String FRIENDSHIP_TO_TAG = "friendship_to";
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

		if (!hasContents && contextBox.getContextType().hasVisibleLore()) {
			hasContents = NBT.get(item, nbt -> {
				if (!nbt.hasTag(FRIENDSHIP_TAG)) return false;

				UUID from = nbt.getOrNull(FRIENDSHIP_FROM_TAG, UUID.class);
				UUID to = nbt.getOrNull(FRIENDSHIP_TO_TAG, UUID.class);
				if (from == null || to == null) {
					contextBox.addLore(List.of(messenger.getMessages("item.modifier.letter.friendship")));
					return true;
				}

				Component fromName = BookAuthorOnlineModifier.getStatus(from);
				if (fromName == null) return true;

				Player viewer = contextBox.getViewer();
				if (viewer != null && viewer.getUniqueId().equals(to)) {
					contextBox.addLore(List.of(messenger.getMessages("item.modifier.letter.friendship.bound.you", raw("from", fromName))));
					return true;
				}

				Component toName = BookAuthorOnlineModifier.getStatus(to);
				if (toName == null) return true;

				contextBox.addLore(List.of(messenger.getMessages("item.modifier.letter.friendship.bound", raw("from", fromName), raw("to", toName))));
				return true;
			});
		}

		if (contextBox.getContextType().hasVisibleName()) {
			Component itemName = messenger.getMessage(hasContents ? type.localeClosed : type.localeOpen);
			item.setData(DataComponentTypes.ITEM_NAME, itemName);
		}

		item.setData(DataComponentTypes.ITEM_MODEL, hasContents ? type.modelClosed : type.modelOpen);

		return ModificationResult.OK;
	}

	/**
	 * Gets the unbound friendship letter
	 *
	 * @return the unbound friendship letter
	 */
	public static ItemStack getFriendshipLetter() {
		var item = ItemStack.of(TrappedNewbieItems.LETTER);
		NBT.modify(item, nbt -> {
			nbt.setEnum(TYPE_TAG, LetterType.STAR);
			nbt.setBoolean(FRIENDSHIP_TAG, true);
		});
		return item;
	}

	/**
	 * Tries to bind the friendship letter.
	 * <p>Expects the passed item to be a letter.
	 *
	 * @param letter letter item
	 * @param from player from
	 * @param to player to
	 * @return modified item if successful
	 */
	public static @Nullable ItemStack tryToBind(ItemStack letter, Player from, Player to) {
		return NBT.modify(letter, nbt -> {
			if (!nbt.getOrDefault(FRIENDSHIP_TAG, false)) return null;
			if (nbt.hasTag(FRIENDSHIP_FROM_TAG)) return null;
			if (nbt.hasTag(FRIENDSHIP_TO_TAG)) return null;

			nbt.setUUID(FRIENDSHIP_FROM_TAG, from.getUniqueId());
			nbt.setUUID(FRIENDSHIP_TO_TAG, to.getUniqueId());

			return letter;
		});
	}

	/**
	 * Checks whether the letter is bound.
	 * <p>Expects the passed item to be a letter.
	 *
	 * @param letter letter item
	 * @param from player from
	 * @param to player to
	 * @return whether the letter is bound
	 */
	public static boolean isBound(ItemStack letter, Player from, Player to) {
		return NBT.get(letter, nbt -> {
			if (!nbt.getOrDefault(FRIENDSHIP_TAG, false)) return false;
			return from.getUniqueId().equals(nbt.getOrNull(FRIENDSHIP_FROM_TAG, UUID.class))
					&& to.getUniqueId().equals(nbt.getOrNull(FRIENDSHIP_TO_TAG, UUID.class));
		});
	}

	/**
	 * Checks whether the letter is unbound.
	 * <p>Expects the passed item to be a letter.
	 *
	 * @param letter letter item
	 * @return whether the letter is unbound
	 */
	public static boolean isUnboundFriendshipLetter(ItemStack letter) {
		return NBT.get(letter, nbt -> nbt.getOrDefault(FRIENDSHIP_TAG, false) && !nbt.hasTag(FRIENDSHIP_FROM_TAG) && !nbt.hasTag(FRIENDSHIP_TO_TAG));
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
