package me.sosedik.resourcelib.rpgenerator.item.parser.type;

import com.google.gson.JsonObject;
import me.sosedik.resourcelib.rpgenerator.item.ItemParser;
import me.sosedik.resourcelib.rpgenerator.item.parser.ItemParseOptions;
import me.sosedik.utilizer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class BaseItemParser {

	protected final ItemParser itemParser;
	protected final ItemParseOptions itemParseOptions;
	private JsonObject modelJson;

	protected BaseItemParser(@NotNull ItemParser itemParser, @NotNull ItemParseOptions itemParseOptions) {
		this.itemParser = itemParser;
		this.itemParseOptions = itemParseOptions;
	}

	/**
	 * Gets the item's namespace
	 *
	 * @return item namespace
	 */
	public @NotNull String getNamespace() {
		return this.itemParseOptions.namespace();
	}

	/**
	 * Gets the item's path
	 *
	 * @return item path
	 */
	public @NotNull String getPath() {
		return this.itemParseOptions.path();
	}

	/**
	 * Gets the item's key
	 *
	 * @return item key
	 */
	public @NotNull String getKey() {
		return this.itemParseOptions.key();
	}

	/**
	 * Gets the item's options
	 *
	 * @return item options
	 */
	public @NotNull JsonObject getItemOptions() {
		return this.itemParseOptions.options();
	}

	/**
	 * Gets the json model for this item
	 *
	 * @return json model
	 */
	public @NotNull JsonObject getModelJson() {
		if (this.modelJson == null)
			throw new RuntimeException("Tried to get missing json model! Item ID: " + this.itemParseOptions.combinedKeyWithPath());
		return this.modelJson;
	}

	/**
	 * Sets the json model for this item
	 *
	 * @param modelJson json model
	 */
	public void setModelJson(@NotNull JsonObject modelJson) {
		this.modelJson = modelJson;
	}

	/**
	 * Constructs json model for this item
	 */
	public abstract void constructModel();

	/**
	 * Creates model file in resource pack directory
	 * and registers CMD for this item
	 */
	public void createModel() {
		createModelFile();
		this.itemParser.addCMDModel(this.itemParseOptions.vanillaType(), this.itemParseOptions.namespacedKey(), getNamespace() + ":" + getPath() + getKey());
	}

	protected void createModelFile() {
		String path = "assets/" + getNamespace() + "/models/" + getPath() + getKey();
		var modelFile = new File(this.itemParser.getGenerator().getOutputDir(), path + ".json");

		FileUtil.createPrettyJsonFile(modelFile, getModelJson());
	}

}
