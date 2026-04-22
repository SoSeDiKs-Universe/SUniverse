package me.sosedik.trappednewbie.api.item.tinker;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBTList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record SlingshotData(
	@Nullable Material material,
	@Nullable Material base,
	@Nullable Material string,
	@Nullable Material projectile
) implements TinkerData {

	@Override
	public List<String> serialize(boolean includeDefaults) {
		return List.of(
			this.material == null ? (includeDefaults ? Material.LEATHER.getKey().asString() : "") : this.material.getKey().asString(),
			this.base == null ? (includeDefaults ? Material.STICK.getKey().asString() : "") : this.base.getKey().asString(),
			this.string == null ? (includeDefaults ? Material.STRING.getKey().asString() : "") : this.string.getKey().asString(),
			this.projectile == null ? "" : this.projectile.getKey().asString()
		);
	}

	public static SlingshotData fromSlingshot(ItemStack item, @Nullable ItemStack projectile) {
		return NBT.get(item, nbt -> {
			if (!nbt.hasTag(DATA_TAG)) return defaultData(projectile == null ? null : projectile.getType());

			ReadableNBTList<String> strings = nbt.getStringList(DATA_TAG);
			Material material = strings.isEmpty() ? null : Material.matchMaterial(strings.get(0));
			Material base = strings.size() < 2 ? null : Material.matchMaterial(strings.get(1));
			Material string = strings.size() < 3 ? null : Material.matchMaterial(strings.get(2));

			return new SlingshotData(material, base, string, projectile == null ? null : projectile.getType());
		});
	}

	public static SlingshotData defaultData(@Nullable Material projectile) {
		return new SlingshotData(null, null, null, projectile);
	}

}
