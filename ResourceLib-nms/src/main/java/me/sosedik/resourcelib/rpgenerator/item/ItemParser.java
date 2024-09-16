package me.sosedik.resourcelib.rpgenerator.item;

import com.google.gson.JsonObject;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.item.FakeItemData;
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import me.sosedik.resourcelib.rpgenerator.item.parser.ItemParseOptions;
import me.sosedik.resourcelib.rpgenerator.item.parser.ItemParserOverrides;
import me.sosedik.resourcelib.rpgenerator.item.parser.type.BaseItemParser;
import me.sosedik.resourcelib.rpgenerator.item.parser.type.TexturedItemParser;
import me.sosedik.utilizer.util.FileUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class ItemParser {

	private static final Map<String, BiFunction<ItemParser, ItemParseOptions, BaseItemParser>> ITEM_PARSERS = new HashMap<>();

	static {
		ITEM_PARSERS.put("item", TexturedItemParser::new);
	}

	private final ResourcePackGenerator generator;
	private final Map<NamespacedKey, ItemParseOptions> itemOptionsMap = new HashMap<>();
	private final Map<Material, ItemParserOverrides> itemOverridesMap = new HashMap<>();

	public ItemParser(@NotNull ResourcePackGenerator generator) {
		this.generator = generator;
	}

	public @NotNull ResourcePackGenerator getGenerator() {
		return this.generator;
	}

	public void parseItems(@NotNull File itemsDir, @NotNull String namespace, @NotNull String path) {
		if (itemsDir.listFiles() == null) return;

		for (File itemFile : Objects.requireNonNull(itemsDir.listFiles())) {
			if (itemFile.isDirectory()) {
				parseItems(itemFile, namespace, path + itemFile.getName() + "/");
				continue;
			}
			if (!itemFile.isFile()) continue;
			if (!itemFile.getName().endsWith(".json")) continue;

			JsonObject itemOptions = FileUtil.readJsonObject(itemFile);
			cacheItem(namespace, path, itemFile.getName().replace(".json", ""), itemOptions);
		}
	}

	private void cacheItem(@NotNull String namespace, @NotNull String path, @NotNull String key, @NotNull JsonObject options) {
		Material vanillaType = options.has("client_type")
				? Material.matchMaterial(options.get("client_type").getAsString())
				: this.generator.getPackOptions().getDefaultItemMaterial();
		if (vanillaType == null) {
			ResourceLib.logger().warn("[ItemParser] Unknown item type: {}, requested by {}", options.get("client_type").getAsString(), namespace + ":" + path + key);
			return;
		}

		var itemOptions = new ItemParseOptions(vanillaType, namespace, path, key, options);
		NamespacedKey namespacedKey = itemOptions.namespacedKey();
		if (this.itemOptionsMap.containsKey(namespacedKey)) return;

		this.itemOptionsMap.put(namespacedKey, itemOptions);
	}

	public void generateItems() {
		itemOptionsMap.values().forEach(itemParseOptions -> {
			JsonObject options = itemParseOptions.options();

			String parserType = options.has("parser") ? options.get("parser").getAsString() : "item";
			var parser = ITEM_PARSERS.get(parserType);
			if (parser == null) {
				ResourceLib.logger().warn("Item {} requested unknown parser: {}", itemParseOptions.combinedKeyWithPath(), parserType);
				parser = ITEM_PARSERS.get("item");
			}
			BaseItemParser itemParser = parser.apply(this, itemParseOptions);
			itemParser.constructModel();
			itemParser.createModel();
		});

		this.itemOverridesMap.forEach((type, options) -> {
			JsonObject model = options.getBaseModel();
			model.add("overrides", options.getExtraOverrides());
			var vanillaModelFile = new File(generator.getOutputDir(), "assets/minecraft/models/item/" + type.getKey().getKey() + ".json");
			FileUtil.createPrettyJsonFile(vanillaModelFile, model);
		});

		itemOptionsMap.values().forEach(itemParseOptions -> {
			Material vanillaType = itemParseOptions.vanillaType();
			NamespacedKey itemKey = itemParseOptions.namespacedKey();
			var fakeItemData = new FakeItemData(vanillaType, getOverrides(vanillaType).getCustomModelData(itemKey));
			this.generator.getStorage().addItemOption(itemKey, itemParseOptions.options(), fakeItemData);
		});
	}

	public ItemParserOverrides getOverrides(@NotNull Material vanillaType) {
		return this.itemOverridesMap.computeIfAbsent(vanillaType, k -> new ItemParserOverrides(this.generator, vanillaType));
	}

	/**
	 * Adds CMD json object to current overrides
	 *
	 * @param path Path to model file
	 */
	public void addCMDModel(@NotNull Material vanillaType, @NotNull NamespacedKey key, @NotNull String path) {
		ItemParserOverrides itemParserOverrides = getOverrides(vanillaType);
		var model = new JsonObject();
		var predicate = new JsonObject();
		predicate.addProperty("custom_model_data", itemParserOverrides.increaseCustomModelData(key));
		model.add("predicate", predicate);
		model.addProperty("model", path);
		itemParserOverrides.getExtraOverrides().add(model);
	}

}
