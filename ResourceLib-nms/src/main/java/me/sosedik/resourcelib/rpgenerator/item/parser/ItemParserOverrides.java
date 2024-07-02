package me.sosedik.resourcelib.rpgenerator.item.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonStreamParser;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemParserOverrides {

	private final JsonObject baseModel;
	private final File itemModelsDir;
	private final JsonArray extraOverrides = new JsonArray();
	private int customModelData = 0;
	private final Map<NamespacedKey, Integer> storedCmds = new HashMap<>();

	public ItemParserOverrides(@NotNull ResourcePackGenerator generator, @NotNull Material type) {
		this.baseModel = getBaseModel(generator, type);
		this.itemModelsDir = new File(generator.getMcAssetsDir(), "assets/minecraft/models/item");

		if (this.baseModel.has("overrides")) {
			JsonArray overrides = this.baseModel.getAsJsonArray("overrides");
			extraOverrides.addAll(overrides);
			for (JsonElement json : overrides) {
				if (!json.isJsonObject()) continue;
				if (!json.getAsJsonObject().has("predicate")) continue;

				var predicate = json.getAsJsonObject().getAsJsonObject("predicate");
				if (!predicate.has("custom_model_data")) continue;

				this.customModelData = Math.max(this.customModelData, predicate.get("custom_model_data").getAsInt());
			}
		}
	}

	public @NotNull JsonObject getBaseModel() {
		return this.baseModel;
	}

	public @NotNull JsonArray getExtraOverrides() {
		return this.extraOverrides;
	}

	public @Nullable Integer getCustomModelData(@NotNull NamespacedKey key) {
		return this.storedCmds.get(key);
	}

	public int increaseCustomModelData(@NotNull NamespacedKey key) {
		return this.storedCmds.computeIfAbsent(key, k -> ++this.customModelData);
	}

	/**
	 * Creates a copy of vanilla model
	 * ready for adding overrides
	 *
	 * @return Json object representing vanilla model
	 */
	private @NotNull JsonObject getBaseModel(@NotNull ResourcePackGenerator generator, @NotNull Material type) {
		String itemKey = type.getKey().getKey().toLowerCase(Locale.ENGLISH);
		var modelFile = new File(generator.getMcAssetsDir(), "assets/minecraft/models/item/" + itemKey + ".json");
		if (!modelFile.exists())
			modelFile = new File(this.itemModelsDir, itemKey + ".json");
		JsonObject model;
		try (var reader = new FileReader(modelFile)) {
			var obj = new JsonStreamParser(reader).next().getAsJsonObject();
			model = JsonParser.parseString(obj.toString().replace("minecraft:", "")).getAsJsonObject();
		} catch (IOException e) {
			ResourceLib.logger().error("[ItemParser] Couldn't find vanilla item model: {}.json", itemKey);
			return new JsonObject();
		}
		return model;
	}

}
