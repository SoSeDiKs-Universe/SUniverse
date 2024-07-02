package me.sosedik.resourcelib.rpgenerator.misc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

public class FontCreator {

	private final ResourcePackGenerator generator;
	private final Gson gson = new Gson();
	private JsonObject defLangJson;
	private final JsonObject fontsJson = gson.fromJson("{\"providers\":[]}", JsonObject.class);
	private char currentFontChar = '\uE000';

	public FontCreator(@NotNull ResourcePackGenerator generator) {
		this.generator = generator;
	}

	public void prepareFonts() {
		// Emoji contain & load default language json
		addEmoji();
		if (this.defLangJson == null) return;

		addSpacings();
	}

	private void addEmoji() {
		var emojiDir = new File(generator.getUserDataDir(), "emoji");
		if (!emojiDir.exists() || Objects.requireNonNull(emojiDir.listFiles()).length == 0) {
			ResourceLib.logger().error("Emoji folder is empty! (plugins/ResourceLib/datasets/emoji)");
			ResourceLib.logger().warn("Download and extract emoji font from https://github.com/AmberWat/PixelTwemojiMC-18/releases");
			this.defLangJson = null;
			return;
		}

		JsonObject emojiJson = FileUtil.readJsonObject(new File(emojiDir, "assets/minecraft/font/default.json"));
		// Remove emoji categories
		JsonObject emojiCategories = emojiJson.getAsJsonArray("providers").remove(2).getAsJsonObject();
		if (!emojiCategories.get("file").getAsString().equals("emoji_categories:font/categories.png"))
			ResourceLib.logger().warn("Couldn't verify removed emoji categories, emoji font might not work as expected!");

		String defLangContent = FileUtil.getPrettyJson(emojiJson);

		var fontsOutput = new File(generator.getOutputDir(), "assets/twemoji/textures/font");

		var fontTexturesDir = new File(emojiDir, "assets/twemoji/textures/font");
		FileUtil.copyFile(fontTexturesDir, fontsOutput);

		this.defLangJson = gson.fromJson(defLangContent, JsonObject.class);

		ResourceLib.logger().info("Added emoji font");
	}

	private void addSpacings() {
		var fixedLayeringFile = new File(generator.getUserDataDir(), "spacing/splitter.png");
		if (!fixedLayeringFile.exists()) {
			ResourceLib.logger().warn("Missing splitter texture for negative font (plugins/ResourceLib/datasets/spacing/splitter.png)");
			return;
		}

		// "spacings" font
		int range = 4096;
		final int middleChar = 688128;
		String namespace = SpacingUtil.SPACINGS_FONT.namespace();

		var advancesJson = new JsonObject();
		for (int value = -range; value <= range; value++) {
			int codePoint = middleChar + value;
			String ch = new String(Character.toChars(codePoint));
			advancesJson.addProperty(ch, value);
		}

		var spacingsJson = new JsonObject();
		spacingsJson.addProperty("type", "space");
		spacingsJson.add("advances", advancesJson);

		var fontJson = new JsonObject();
		var providers = new JsonArray();
		providers.add(spacingsJson);

		var fixedLayering = new JsonObject();
		String path = "font/" + namespace + "/splitter" + ".png";
		FileUtil.copyFile(fixedLayeringFile, new File(generator.getOutputDir(), "assets/" + namespace + "/textures/" + path));
		fixedLayering.addProperty("type", "bitmap");
		fixedLayering.addProperty("file", namespace + ":" + path);
		fixedLayering.addProperty("ascent", -99999);
		fixedLayering.addProperty("height", -2);
		var charArray = new JsonArray();
		charArray.add(new String(Character.toChars(middleChar + range + 1)));
		fixedLayering.add("chars", charArray);
		providers.add(fixedLayering);

		fontJson.add("providers", providers);

		var spacingsFile = new File(generator.getOutputDir(), "assets/" + namespace + "/font/" + SpacingUtil.SPACINGS_FONT.value() + ".json");
		FileUtil.createPrettyJsonFile(spacingsFile, fontJson);

		// Global
		advancesJson = new JsonObject();
		advancesJson.addProperty(SpacingUtil.FAKE_SPACE, 4); // Fake space
		advancesJson.addProperty(SpacingUtil.NEGATIVE_PIXEL, -1); // Negative space
		spacingsJson = new JsonObject();
		spacingsJson.addProperty("type", "space");
		spacingsJson.add("advances", advancesJson);
		defLangJson.getAsJsonArray("providers").add(spacingsJson);
	}

	public void parseIcons(@NotNull File inputDir, @NotNull String namespace) {
		// TODO
	}

	public @NotNull String addIcon(@Nullable File iconFile, @NotNull String iconKey, @NotNull JsonObject options, @NotNull String namespace) {
		return addIcon(iconFile, iconKey, options, namespace, false);
	}

	public @NotNull String addIcon(@Nullable File iconFile, @NotNull String iconKey, @NotNull JsonObject options, @NotNull String namespace, boolean hardcoded) {
		String textureMapping = iconKey;
		String mappingKey = iconKey;
		boolean vanilla = "minecraft".equals(namespace);
		boolean customNamespace = iconFile == null && !vanilla;
		if (customNamespace)
			namespace = generator.getPackOptions().getPackNamespace();

		if (customNamespace) {
			File textureFile = FileUtil.findFile(generator.getOutputDir(), iconKey + ".png");
			if (textureFile == null) {
				ResourceLib.logger().error("[FontCreator] Custom texture mapping not found: {}", iconKey);
				return iconKey;
			}
			textureMapping = textureFile.getPath().split("textures\\\\")[1];
			textureMapping = textureMapping.split("\\.", 2)[0];
			textureMapping = textureMapping.replace("\\", "/");
			mappingKey = namespace + "." + mappingKey;
		} else if (vanilla) {
			mappingKey = "vanilla." + mappingKey;
		}

		// We may have multiple icons for one texture (with different ascent, height, etc.)
		if (generator.getStorage().getFontMapping(mappingKey) != null) {
			if (iconFile != null)
				ResourceLib.logger().error("Unexpected duplicate font found: {}", mappingKey);
			int id = 2;
			while (generator.getStorage().getFontMapping(mappingKey + "$" + id) != null)
				id++;
			mappingKey += "$" + id;
		}

		if (iconFile != null)
			FileUtil.copyFile(iconFile, new File(generator.getOutputDir(), "assets/" + namespace + "/textures/" + textureMapping + ".png"));

		var iconJson = new JsonObject();
		iconJson.addProperty("type", "bitmap");
		iconJson.addProperty("ascent", options.has("ascent") ? options.get("ascent").getAsInt() : 7);
		if (options.has("height"))
			iconJson.addProperty("height", options.get("height").getAsInt());
		if (vanilla) {
			iconJson.addProperty("file", textureMapping + ".png");
		} else if (customNamespace) {
			iconJson.addProperty("file", namespace + ":" + textureMapping + ".png");
		} else {
			iconJson.addProperty("file", namespace + ":" + textureMapping + ".png");
		}

		var iconChars = new JsonArray();
		var size = options.has("size") ? options.get("size").getAsInt() : 1;
		var sb = new StringBuilder();
		for (var i = 0; i < size; i++) {
			sb.append(currentFontChar);
			currentFontChar++;
		}

		if (size > 1) {
			char[] chars = sb.toString().toCharArray();
			if (hardcoded) {
				var combined = new StringBuilder();
				for (var i = 0; i < chars.length; i++) {
					combined.append(chars[i]);
					if (i + 1 != chars.length)
						combined.append(SpacingUtil.NEGATIVE_PIXEL);
				}
				generator.getStorage().addFontMapping(mappingKey, combined.toString());
			} else {
				for (var i = 0; i < chars.length; i++) {
					generator.getStorage().addFontMapping(mappingKey + "$" + (i + 1), String.valueOf(chars[i]));
				}
			}
		} else {
			generator.getStorage().addFontMapping(mappingKey, sb.toString());
		}
		iconChars.add(sb.toString());
		iconJson.add("chars", iconChars);
		if (hardcoded)
			defLangJson.getAsJsonArray("providers").add(iconJson);
		else
			fontsJson.getAsJsonArray("providers").add(iconJson);
		return sb.toString();
	}

	public void generateFonts() {
		FileUtil.createPrettyJsonFile(new File(generator.getOutputDir(), "assets/" + generator.getPackOptions().getPackNamespace() + "/font/fonts.json"), fontsJson);

		var langFilesDir = new File(generator.getOutputDir(), "assets/minecraft/font");
		var defLangFile = new File(langFilesDir, "default.json");
		FileUtil.createPrettyJsonFile(defLangFile, defLangJson);
		FileUtil.copyFile(defLangFile, new File(langFilesDir, "uniform.json"));
	}

}
