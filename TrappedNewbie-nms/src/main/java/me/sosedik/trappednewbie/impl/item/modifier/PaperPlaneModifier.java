package me.sosedik.trappednewbie.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.entity.api.PaperPlane;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Paper plane-specific visuals
 */
@NullMarked
public class PaperPlaneModifier extends ItemModifier {

	public PaperPlaneModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;
		if (contextBox.getInitialType() != TrappedNewbieItems.PAPER_PLANE) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		boolean blazified = NBT.get(item, nbt -> (boolean) nbt.getOrDefault(PaperPlane.BLAZIFIED_TAG, false));
		boolean fragile = NBT.get(item, nbt -> (boolean) nbt.getOrDefault(PaperPlane.FRAGILE_TAG, false));
		if (!blazified && !fragile) return ModificationResult.PASS;

		Messenger messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		if (blazified) {
			contextBox.addLore(messenger.getMessage("item.modifier.blazified"));
			item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
		}
		if (fragile) contextBox.addLore(messenger.getMessage("item.modifier.fragile"));

		return ModificationResult.OK;
	}

}
