package me.sosedik.resourcelib.dataset;

import com.google.gson.JsonObject;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.resourcelib.api.item.FakeItemData;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ResourcePackStorage {

	private final Map<String, FontData> fontMappings = new HashMap<>();
	private final Map<NamespacedKey, ItemData> itemOptions = new HashMap<>();

	public @Nullable String getFontMapping(@NotNull String key) {
		FontData data = this.fontMappings.get(key);
		return data == null ? null : data.rawMapping();
	}

	public @Nullable FontData getFontData(@NotNull String key) {
		return this.fontMappings.get(key);
	}

	public void addFontMapping(@NotNull String key, @NotNull Component symbols, int width) {
		this.fontMappings.put(key, new FontData(key, symbols, width));
	}

	public void addItemOption(@NotNull NamespacedKey key, @NotNull JsonObject resourceData, @NotNull FakeItemData fakeItemData) {
		if (this.itemOptions.containsKey(key)) return;

		this.itemOptions.put(key, new ItemData(resourceData, fakeItemData));
	}

	public @Nullable JsonObject getItemOptions(@NotNull NamespacedKey key) {
		ItemData itemData = this.itemOptions.get(key);
		return itemData == null ? null : itemData.options();
	}

	public @Nullable FakeItemData getFakeItemData(@NotNull NamespacedKey key) {
		ItemData itemData = this.itemOptions.get(key);
		return itemData == null ? null : itemData.fakeItemData();
	}

	private record ItemData(@NotNull JsonObject options, @NotNull FakeItemData fakeItemData) {}

}
