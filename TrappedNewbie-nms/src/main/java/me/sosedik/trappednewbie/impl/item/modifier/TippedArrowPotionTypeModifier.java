package me.sosedik.trappednewbie.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Shows the type of tipped arrow in lore
 */
@NullMarked
public class TippedArrowPotionTypeModifier extends ItemModifier {

	private static final String ARROW_TYPE_TAG = "arrow_type";

	public TippedArrowPotionTypeModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != Material.TIPPED_ARROW) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		ArrowPotionType arrowPotionType = ArrowPotionType.fromItem(item);
		if (arrowPotionType == ArrowPotionType.POTION) return ModificationResult.PASS;

		contextBox.addLore(Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale())).getMessage(arrowPotionType.localeKey));

		return ModificationResult.OK;
	}

	public enum ArrowPotionType {

		POTION,
		SPLASH,
		LINGERING;

		private final String localeKey;

		ArrowPotionType() {
			this.localeKey = "item.modifier." + name().toLowerCase(Locale.US) + "_arrow";
		}

		public static ArrowPotionType fromItem(ItemStack item) {
			return NBT.get(item, nbt -> (ArrowPotionType) nbt.getOrDefault(ARROW_TYPE_TAG, POTION));
		}

		public ItemStack saveTo(ItemStack item) {
			NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setEnum(ARROW_TYPE_TAG, this));
			return item;
		}

	}

}
