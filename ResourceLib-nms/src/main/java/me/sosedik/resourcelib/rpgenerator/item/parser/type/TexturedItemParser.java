package me.sosedik.resourcelib.rpgenerator.item.parser.type;

import com.google.gson.JsonObject;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.rpgenerator.item.ItemParser;
import me.sosedik.resourcelib.rpgenerator.item.parser.ItemParseOptions;
import me.sosedik.resourcelib.rpgenerator.item.parser.ItemParserOverrides;
import me.sosedik.utilizer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class TexturedItemParser extends BaseItemParser {

	private final String parentModel;
	private boolean modelOnly = false;

	public TexturedItemParser(@NotNull ItemParser itemParser, @NotNull ItemParseOptions itemParseOptions) {
		super(itemParser, itemParseOptions);

		ItemParserOverrides overrides = itemParser.getOverrides(itemParseOptions.vanillaType());
		if (getItemOptions().has("parent_model")) {
			String parentModel = getItemOptions().get("parent_model").getAsString();
			if (modelExists(parentModel)) {
				this.parentModel = parentModel;
			} else {
				ResourceLib.logger().warn("Couldn't find item model: {}, requested by {}", parentModel, itemParseOptions.combinedKeyWithPath());
				this.parentModel = overrides.getBaseModel().get("parent").getAsString();
			}
		} else if (getItemOptions().has("model")) {
			this.parentModel = getItemOptions().get("model").getAsString();
			this.modelOnly = true;
		} else {
			this.parentModel = overrides.getBaseModel().get("parent").getAsString();
		}
	}

	private boolean modelExists(@NotNull String parentModel) {
		String namespace;
		String path;
		if (parentModel.contains(":")) {
			String[] split = parentModel.split(":");
			namespace = split[0];
			path = split[1];
		} else {
			namespace = "minecraft";
			path = parentModel;
		}
		return modelExists(itemParser.getGenerator().getMcAssetsDir(), namespace, path)
				|| modelExists(itemParser.getGenerator().getOutputDir(), namespace, path);
	}

	private boolean modelExists(@NotNull File parent, @NotNull String namespace, @NotNull String path) {
		return new File(parent, "assets/" + namespace + "/models/" + path + ".json").exists();
	}

	@Override
	public @NotNull String getPath() {
		return super.getPath() + "item/";
	}

	@Override
	public void constructModel() {
		// If item had direct "model" property
		if (this.modelOnly) {
			var model = new JsonObject();
			model.addProperty("parent", this.parentModel);
			setModelJson(model);
			return;
		}

		// Construct simple model
		tryToCopyTexture();
		var model = new JsonObject();
		model.addProperty("parent", this.parentModel);
		var textures = new JsonObject();
		textures.addProperty("layer0", getNamespace() + ":" + getPath() + getKey());
		model.add("textures", textures);

		// Extra model properties
		if (getItemOptions().has("model_extras")) {
			getItemOptions().getAsJsonObject("model_extras")
				.entrySet()
				.forEach(entry -> model.add(entry.getKey(), entry.getValue()));
		}

		setModelJson(model);
	}

	private void tryToCopyTexture() {
		var textureOutputFile = new File(this.itemParser.getGenerator().getOutputDir(), "assets/" + getNamespace() + "/textures/" + getPath() + getKey() + ".png");
		if (textureOutputFile.exists()) return;

		var texturesFolder = new File(this.itemParser.getGenerator().getUserDataDir(), getNamespace());
		if (!texturesFolder.exists()) {
			ResourceLib.logger().warn("Couldn't find textures folder for {}", this.itemParseOptions.combinedKeyWithPath());
			return;
		}

		File textureFile = FileUtil.findFile(texturesFolder, getKey() + ".png");
		if (textureFile == null) {
			ResourceLib.logger().warn("Couldn't find texture file for {}", this.itemParseOptions.combinedKeyWithPath());
			return;
		}

		FileUtil.copyFile(textureFile, textureOutputFile);

		textureFile = new File(textureFile.getParentFile(), textureFile.getName() + ".mcmeta");
		if (textureFile.exists()) {
			textureOutputFile = new File(textureOutputFile.getParentFile(), textureOutputFile.getName() + ".mcmeta");
			FileUtil.copyFile(textureFile, textureOutputFile);
		}
	}

}
