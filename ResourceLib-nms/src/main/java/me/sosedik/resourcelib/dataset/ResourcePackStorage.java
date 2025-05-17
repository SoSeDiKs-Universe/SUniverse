package me.sosedik.resourcelib.dataset;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.resourcelib.api.item.FakeItemData;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.util.FileUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static me.sosedik.utilizer.api.message.Mini.combined;

// TODO init during bootstrap
@NullMarked
public class ResourcePackStorage {

	private final Map<NamespacedKey, FontData> fontMappings = new HashMap<>();
	private final Map<NamespacedKey, NamespacedKey> soundMappings = new HashMap<>();
	private final Map<NamespacedKey, NamespacedKey> itemMappings = new HashMap<>();
	private final Map<NamespacedKey, String> tripwireMappings = new HashMap<>();
	private final Map<NamespacedKey, JsonObject> sculkMappings = new HashMap<>();
	private final Map<NamespacedKey, JsonObject> weepingVinesMappings = new HashMap<>();
	private final Map<NamespacedKey, ItemData> itemOptions = new HashMap<>();

	public ResourcePackStorage(Plugin plugin) {
		var mappingsDir = new File(plugin.getDataFolder(), "mappings");
		FileUtil.deleteFolder(mappingsDir);

		plugin.saveResource("mappings/fonts.json", true);
		plugin.saveResource("mappings/sounds.json", true);
		plugin.saveResource("mappings/items.json", true);
		plugin.saveResource("mappings/tripwire.json", true);
		plugin.saveResource("mappings/sculk_sensor.json", true);
		plugin.saveResource("mappings/weeping_vines.json", true);

		loadFontMappings(new File(mappingsDir, "fonts.json"));
		loadSoundMappings(new File(mappingsDir, "sounds.json"));
		loadItemMappings(new File(mappingsDir, "items.json"));
		loadTripwireMappings(new File(mappingsDir, "tripwire.json"));
		loadSculkMappings(new File(mappingsDir, "sculk_sensor.json"));
		loadWeepingVinesMappings(new File(mappingsDir, "weeping_vines.json"));
	}

	private void loadFontMappings(File mappingsFile) {
		var gson = new Gson();
		JsonObject fontMappings = FileUtil.readJsonObject(mappingsFile);
		fontMappings.entrySet().forEach(entry -> {
			var key = NamespacedKey.fromString(entry.getKey());
			assert key != null;
			var fontKey = Key.key(key.namespace(), "fonts");
			JsonObject options = entry.getValue().getAsJsonObject();
			int width = options.get("width").getAsInt();
			int compensationPixels = options.has("compensation_pixels") ? options.get("compensation_pixels").getAsInt() : 0;

			JsonElement mappingEl= options.get("mapping");
			if (mappingEl instanceof JsonArray arr) {
				String[] rawMapping = gson.fromJson(arr, String[].class);
				boolean hardcoded = options.has("hardcoded") && options.get("hardcoded").getAsBoolean();
				if (hardcoded) {
					var combined = new StringBuilder();
					for (int i = 0; i < rawMapping.length; i++) {
						combined.append(rawMapping[i]);
						if (i + 1 != rawMapping.length)
							combined.append(SpacingUtil.NEGATIVE_PIXEL);
					}
					Component mapping = Component.text(combined.toString()).font(fontKey);
					if (compensationPixels > 0)
						mapping = combined(mapping, Component.text(SpacingUtil.POSITIVE_PIXEL.repeat(compensationPixels)));
					this.fontMappings.put(key, new FontData(mapping, width));
				} else {
					for (int i = 0; i < rawMapping.length; i++) {
						Component mapping = Component.text(rawMapping[i]).font(fontKey);
						if (compensationPixels > 0)
							mapping = combined(mapping, Component.text(SpacingUtil.POSITIVE_PIXEL.repeat(compensationPixels)));
						var extraKey = new NamespacedKey(key.namespace(), key.value() + "-" + (i + 1));
						this.fontMappings.put(extraKey, new FontData(mapping, width));
					}
				}
			} else {
				Component mapping = Component.text(mappingEl.getAsString()).font(fontKey);
				if (compensationPixels > 0)
					mapping = combined(mapping, Component.text(SpacingUtil.POSITIVE_PIXEL.repeat(compensationPixels)));
				this.fontMappings.put(key, new FontData(mapping, width));
			}
		});
	}

	private void loadSoundMappings(File mappingsFile) {
		JsonObject fontMappings = FileUtil.readJsonObject(mappingsFile);
		fontMappings.entrySet().forEach(entry -> {
			var from = requireNonNull(NamespacedKey.fromString(entry.getKey()));
			var to = requireNonNull(NamespacedKey.fromString(entry.getValue().getAsString()));
			this.soundMappings.put(from, to);
		});
	}

	private void loadItemMappings(File mappingsFile) {
		JsonObject fontMappings = FileUtil.readJsonObject(mappingsFile);
		fontMappings.entrySet().forEach(entry -> {
			var from = requireNonNull(NamespacedKey.fromString(entry.getKey()));
			var to = requireNonNull(NamespacedKey.fromString(entry.getValue().getAsString()));
			this.itemMappings.put(from, to);
		});
	}

	private void loadTripwireMappings(File mappingsFile) {
		JsonObject tripwireMappings = FileUtil.readJsonObject(mappingsFile);
		tripwireMappings.entrySet().forEach(entry -> {
			var from = requireNonNull(NamespacedKey.fromString(entry.getKey()));
			String to = Material.TRIPWIRE.key() + "[" + entry.getValue().getAsJsonObject().get("state").getAsString() + "]";
			this.tripwireMappings.put(from, to);
		});
	}

	private void loadSculkMappings(File mappingsFile) {
		JsonObject sculkMappings = FileUtil.readJsonObject(mappingsFile);
		sculkMappings.entrySet().forEach(entry -> {
			var from = requireNonNull(NamespacedKey.fromString(entry.getKey()));
			JsonObject props = entry.getValue().getAsJsonObject();
			this.sculkMappings.put(from, props);
		});
	}

	private void loadWeepingVinesMappings(File mappingsFile) {
		JsonObject weepingVinesMappings = FileUtil.readJsonObject(mappingsFile);
		weepingVinesMappings.entrySet().forEach(entry -> {
			var from = requireNonNull(NamespacedKey.fromString(entry.getKey()));
			JsonObject props = entry.getValue().getAsJsonObject();
			this.weepingVinesMappings.put(from, props);
		});
	}

	public @Nullable FontData getFontData(NamespacedKey key) {
		return this.fontMappings.get(key);
	}

	public NamespacedKey getSoundMapping(NamespacedKey key) {
		NamespacedKey mappedKey = this.soundMappings.get(key);
		if (mappedKey == null) {
			ResourceLib.logger().warn("Couldn't find sound mapping: {}", key);
			return key;
		}
		return mappedKey;
	}

	public NamespacedKey getItemModelMapping(NamespacedKey key) {
		return this.itemMappings.getOrDefault(key, key);
	}

	public @Nullable String getTripwireMapping(NamespacedKey key) {
		return this.tripwireMappings.get(key);
	}

	public @Nullable JsonObject getSculkMapping(NamespacedKey key) {
		return this.sculkMappings.get(key);
	}

	public @Nullable JsonObject getWeepingVinesMapping(NamespacedKey key) {
		return this.weepingVinesMappings.get(key);
	}

	public void addItemOption(NamespacedKey key, JsonObject resourceData, FakeItemData fakeItemData) {
		if (this.itemOptions.containsKey(key)) return;

		this.itemOptions.put(key, new ItemData(resourceData, fakeItemData));
	}

	public @Nullable FakeItemData getFakeItemData(NamespacedKey key) {
		ItemData itemData = this.itemOptions.get(key);
		return itemData == null ? null : itemData.fakeItemData();
	}

	private record ItemData(JsonObject options, FakeItemData fakeItemData) {}

}
