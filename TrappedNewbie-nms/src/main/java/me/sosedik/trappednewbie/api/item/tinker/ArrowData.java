package me.sosedik.trappednewbie.api.item.tinker;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBTList;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record ArrowData(
	@Nullable Material arrowType,
	@Nullable Material head,
	@Nullable Material stick,
	@Nullable Material fletching,
	@Nullable Material modifier
) {

	private static final String DATA_TAG = "materials";

	public List<String> serialize(boolean includeDefaults) {
		return List.of(
			this.arrowType == null ? (includeDefaults ? Material.ARROW.getKey().asString() : "") : this.arrowType.getKey().asString(),
			this.head == null ? (includeDefaults ? Material.FLINT.getKey().asString() : "") : this.head.getKey().asString(),
			this.stick == null ? (includeDefaults ? Material.STICK.getKey().asString() : "") : this.stick.getKey().asString(),
			this.fletching == null ? (includeDefaults ? Material.FEATHER.getKey().asString() : "") : this.fletching.getKey().asString()
		);
	}

	public void saveToCustomModelData(ItemStack arrow, boolean includeDefaults) {
		arrow.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addStrings(serialize(includeDefaults)).build());
	}

	public void saveToCustomData(ItemStack arrow, boolean includeDefaults) {
		NBT.modify(arrow, nbt -> {
			nbt.removeKey(DATA_TAG);
			nbt.getStringList(DATA_TAG).addAll(List.of(
				this.head == null ? (includeDefaults ? Material.FLINT.getKey().asString() : "") : this.head.getKey().asString(),
				this.stick == null ? (includeDefaults ? Material.STICK.getKey().asString() : "") : this.stick.getKey().asString(),
				this.fletching == null ? (includeDefaults ? Material.FEATHER.getKey().asString() : "") : this.fletching.getKey().asString()
			));
		});
	}

	public static ArrowData fromArrow(ItemStack arrow) {
		return fromArrow(arrow, arrow.getType());
	}

	public static ArrowData fromArrow(ItemStack arrow, Material arrowType) {
		return NBT.get(arrow, nbt -> {
			if (!nbt.hasTag(DATA_TAG)) return defaultData(arrowType);

			ReadableNBTList<String> strings = nbt.getStringList(DATA_TAG);
			Material head = strings.isEmpty() ? null : Material.matchMaterial(strings.get(0));
			Material stick = strings.size() < 2 ? null : Material.matchMaterial(strings.get(1));
			Material fletching = strings.size() < 3 ? null : Material.matchMaterial(strings.get(2));
			Material modifier = arrowType == Material.SPECTRAL_ARROW
				? Material.GLOWSTONE_DUST
				: (strings.size() < 4 ? null : Material.matchMaterial(strings.get(3)));

			return new ArrowData(arrowType, head, stick, fletching, modifier);
		});
	}

	public static ArrowData defaultData(@Nullable Material arrowType) {
		return new ArrowData(arrowType, null, null, null, null);
	}

}
