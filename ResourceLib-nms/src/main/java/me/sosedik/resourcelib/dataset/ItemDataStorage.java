package me.sosedik.resourcelib.dataset;

import me.sosedik.kiterino.world.item.KiterinoNMSItem;
import me.sosedik.resourcelib.ResourceLib;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemDataStorage {

	private static final Map<Material, KiterinoNMSItem> ITEMS = new HashMap<>();

	public static @Nullable KiterinoNMSItem getItem(@NotNull Material type) {
		return ITEMS.get(type);
	}

	public static void registerItem(@NotNull Material type, @NotNull KiterinoNMSItem item) {
		if (ITEMS.put(type, item) != null)
			ResourceLib.logger().warn("Duplicate item implementation for {}", type.getKey());
	}

}
