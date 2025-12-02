package me.sosedik.trappednewbie.api.item.tinker;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadableNBTList;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.sosedik.utilizer.util.MiscUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public record CrossbowData(
	@Nullable Material material,
	@Nullable Material base,
	@Nullable Material string,
	@Nullable Material modifier,
	@Nullable ArrowData arrowData
) {

	private static final String DATA_TAG = "materials";

	public List<String> serialize(boolean includeDefaults) {
		return MiscUtil.combineToList(
			List.of(
				this.material == null ? "" : this.material.getKey().asString(),
				this.base == null ? (includeDefaults ? Material.STICK.getKey().asString() : "") : this.base.getKey().asString(),
				this.string == null ? (includeDefaults ? Material.STRING.getKey().asString() : "") : this.string.getKey().asString(),
				this.modifier == null ? "" : this.modifier.getKey().asString()
			),
			this.arrowData == null
				? ArrowData.defaultData(null).serialize(includeDefaults)
				: this.arrowData.serialize(includeDefaults)
		);
	}

	public void saveToCustomModelData(ItemStack bow, boolean includeDefaults) {
		bow.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addStrings(serialize(includeDefaults)).build());
	}

	public void saveToCustomData(ItemStack arrow, boolean includeDefaults) {
		NBT.modify(arrow, (Consumer<ReadWriteItemNBT>) nbt -> nbt.getStringList(DATA_TAG).addAll(serialize(includeDefaults)));
	}

	public static CrossbowData fromCrossbow(ItemStack bow, @Nullable ItemStack arrow) {
		return NBT.get(bow, nbt -> {
			if (!nbt.hasTag(DATA_TAG)) return defaultData(arrow == null ? null : ArrowData.fromArrow(arrow));

			ReadableNBTList<String> strings = nbt.getStringList(DATA_TAG);
			Material material = strings.isEmpty() ? null : Material.matchMaterial(strings.get(0));
			Material base = strings.size() < 2 ? null : Material.matchMaterial(strings.get(1));
			Material string = strings.size() < 3 ? null : Material.matchMaterial(strings.get(2));
			Material modifier = strings.size() < 4 ? null : Material.matchMaterial(strings.get(3));

			return new CrossbowData(material, base, string, modifier, arrow == null ? null : ArrowData.fromArrow(arrow));
		});
	}

	public static CrossbowData defaultData(@Nullable ArrowData arrowData) {
		return new CrossbowData(null, null, null, null, arrowData);
	}

}
