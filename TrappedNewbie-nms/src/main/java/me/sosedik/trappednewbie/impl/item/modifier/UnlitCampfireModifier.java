package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.TrappedNewbie;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.data.Lightable;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Applies unlit campfire texture/locale to non-lit campfires
 */
@NullMarked
public class UnlitCampfireModifier extends ItemModifier {

	private static final NamespacedKey MODEL = ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("unlit_campfire"));

	public UnlitCampfireModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (!Tag.CAMPFIRES.isTagged(item.getType())) return ModificationResult.PASS;

		if (item.hasBlockData()) {
			if (item.getBlockData(item.getType()) instanceof Lightable lightable && lightable.isLit()) return ModificationResult.PASS;
		}
		if (item.isDataOverridden(DataComponentTypes.ITEM_MODEL)) return ModificationResult.PASS;

		item.setData(DataComponentTypes.ITEM_MODEL, MODEL);
		// Fake block type to disable dynamic lightning on the client
		Component name = Component.translatable(item.translationKey(), item.getDataOrDefault(DataComponentTypes.RARITY, ItemRarity.COMMON).color());
		contextBox.setType(Material.BARRIER);
		contextBox.getItem().setData(DataComponentTypes.ITEM_NAME, name);

		return ModificationResult.OK;
	}

}
