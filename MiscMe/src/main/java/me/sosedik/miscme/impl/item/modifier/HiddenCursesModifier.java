package me.sosedik.miscme.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.value.qual.IntRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Hides curses from item
 */
@NullMarked
public class HiddenCursesModifier extends ItemModifier {

	public static final String REVEAL_CURSES_TAG = "reveal_curses";

	public HiddenCursesModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (NBT.get(item, nbt -> (boolean) nbt.getOrDefault(REVEAL_CURSES_TAG, false))) return ModificationResult.PASS;

		boolean renderLore = contextBox.getContext().getContextType().hasVisibleLore();
		boolean modifiedEnchants = false;

		if (item.hasData(DataComponentTypes.STORED_ENCHANTMENTS)) {
			contextBox.addHiddenComponents(DataComponentTypes.STORED_ENCHANTMENTS);
			modifiedEnchants = hideCurses(item, item.getData(DataComponentTypes.STORED_ENCHANTMENTS), true);
			if (modifiedEnchants && renderLore) {
				var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
				contextBox.addLore(messenger.getMessage("attribute.has_curses").colorIfAbsent(NamedTextColor.RED));
			}
		}

		if (item.hasData(DataComponentTypes.ENCHANTMENTS)) {
			contextBox.addHiddenComponents(DataComponentTypes.ENCHANTMENTS);
			boolean hidden = hideCurses(item, item.getData(DataComponentTypes.ENCHANTMENTS), false);
			modifiedEnchants = hidden || modifiedEnchants;
			if (hidden && renderLore) {
				var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
				contextBox.addLore(messenger.getMessage("attribute.cursed").colorIfAbsent(NamedTextColor.RED));
			}
		}

		return modifiedEnchants ? ModificationResult.OK : ModificationResult.PASS;
	}

	private boolean hideCurses(ItemStack item, @Nullable ItemEnchantments data, boolean storedCurses) {
		if (data == null) return false;

		Map<Enchantment, @IntRange(from = 1L, to = 255L) Integer> enchantments = data.enchantments();
		if (enchantments.isEmpty()) return false;

		enchantments = new HashMap<>(enchantments);
		if (!enchantments.keySet().removeIf(Enchantment::isCursed)) return false;

		if (enchantments.isEmpty())
			item.resetData(storedCurses ? DataComponentTypes.STORED_ENCHANTMENTS : DataComponentTypes.ENCHANTMENTS);
		else
			item.setData(storedCurses ? DataComponentTypes.STORED_ENCHANTMENTS : DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments(enchantments));

		return true;
	}

}
