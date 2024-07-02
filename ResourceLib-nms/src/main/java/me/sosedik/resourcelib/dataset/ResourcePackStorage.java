package me.sosedik.resourcelib.dataset;

import com.google.gson.JsonObject;
import me.sosedik.resourcelib.api.FakeItemData;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ResourcePackStorage {

	private final Map<String, String> fontMappings = new HashMap<>();
	private final Map<NamespacedKey, ItemData> itemOptions = new HashMap<>();

	public @Nullable String getFontMapping(@NotNull String key) {
		return fontMappings.get(key);
	}

	public void addFontMapping(@NotNull String key, @NotNull String symbols) {
		fontMappings.put(key, symbols);
	}

	public void addItemOption(@NotNull NamespacedKey key, @NotNull JsonObject resourceData, @NotNull FakeItemData fakeItemData) {
		if (itemOptions.containsKey(key)) return;

		itemOptions.put(key, new ItemData(resourceData, fakeItemData));
	}

	public @Nullable JsonObject getItemOptions(@NotNull NamespacedKey key) {
		ItemData itemData = itemOptions.get(key);
		return itemData == null ? null : itemData.options();
	}

	public @Nullable FakeItemData getFakeItemData(@NotNull NamespacedKey key) {
		ItemData itemData = itemOptions.get(key);
		return itemData == null ? null : itemData.fakeItemData();
	}

	private record ItemData(@NotNull JsonObject options, @NotNull FakeItemData fakeItemData) {}

}
