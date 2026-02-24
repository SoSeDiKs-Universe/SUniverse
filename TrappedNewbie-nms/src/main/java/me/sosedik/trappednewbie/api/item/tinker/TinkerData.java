package me.sosedik.trappednewbie.api.item.tinker;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface TinkerData {

	String DATA_TAG = "materials";

	@Unmodifiable List<String> serialize(boolean includeDefaults);

	default void saveToCustomModelData(ItemStack item, boolean includeDefaults) {
		item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addStrings(serialize(includeDefaults)).build());
	}

	default void saveToCustomData(ItemStack item, boolean includeDefaults) {
		NBT.modify(item, nbt -> {
			nbt.removeKey(DATA_TAG);
			nbt.getStringList(DATA_TAG).addAll(serialize(includeDefaults));
		});
	}

}
