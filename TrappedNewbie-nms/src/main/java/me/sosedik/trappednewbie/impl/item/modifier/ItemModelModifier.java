package me.sosedik.trappednewbie.impl.item.modifier;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Some items might want another model applied
 */
@NullMarked
public class ItemModelModifier extends ItemModifier {

	public static final String SKIP_TAG = "skip_model_replacement";
	private static final Map<Material, Function<ItemContextBox, @Nullable NamespacedKey>> MODEL_REPLACEMENTS = new HashMap<>();

	public ItemModelModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		Material type = contextBox.getInitialType();
		Function<ItemContextBox, @Nullable NamespacedKey> modelSupplier = MODEL_REPLACEMENTS.get(type);
		if (modelSupplier == null) return ModificationResult.PASS;

		NamespacedKey modelKey = modelSupplier.apply(contextBox);
		if (modelKey == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (item.isDataOverridden(DataComponentTypes.ITEM_MODEL)) return ModificationResult.PASS;
		if (NBT.get(item, nbt -> (boolean) nbt.getOrDefault(SKIP_TAG, false))) return ModificationResult.PASS;

		item.setData(DataComponentTypes.ITEM_MODEL, modelKey);

		return ModificationResult.OK;
	}

	public static void addReplacement(Material type, NamespacedKey modelKey) {
		addReplacement(type, box -> modelKey);
	}

	public static void addReplacement(Material type, Function<ItemContextBox, @Nullable NamespacedKey> modelSupplier) {
		MODEL_REPLACEMENTS.put(type, modelSupplier);
	}

}
