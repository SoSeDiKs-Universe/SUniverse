package me.sosedik.resourcelib.rpgenerator.misc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.util.FileUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static me.sosedik.utilizer.api.message.Mini.combined;

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
		advancesJson.addProperty(SpacingUtil.POSITIVE_PIXEL, 1); // Positive space
		spacingsJson = new JsonObject();
		spacingsJson.addProperty("type", "space");
		spacingsJson.add("advances", advancesJson);
		defLangJson.getAsJsonArray("providers").add(spacingsJson);
	}

	public void parseIcons(@NotNull File inputDir, @NotNull String namespace) {
		var optionsFile = new File(inputDir, "options.json");
		JsonObject options = optionsFile.exists() ? FileUtil.readJsonObject(optionsFile) : new JsonObject();
		parseIcons(inputDir, options, namespace, "");

		if (options.has("vanilla")) {
			JsonObject vanillaFontsJson = options.getAsJsonObject("vanilla");
			for (Map.Entry<String, JsonElement> entry : vanillaFontsJson.entrySet()) {
				var texture = entry.getKey();
				addIcon(null, texture, entry.getValue().getAsJsonObject(), "minecraft");
			}
		}
	}

	private void parseIcons(@NotNull File iconsDir, @NotNull JsonObject options, @NotNull String namespace, @NotNull String prefix) {
		JsonObject fontOptions = options.has("options") ? options.getAsJsonObject("options") : new JsonObject();
		for (File iconFile : Objects.requireNonNull(iconsDir.listFiles())) {
			if (iconFile.isDirectory()) {
				parseIcons(iconFile, options, namespace, prefix + iconFile.getName() + "/");
				continue;
			}
			if (!iconFile.getName().endsWith(".png")) continue;

			String iconKey = prefix + iconFile.getName().replace(".png", "");
			if (fontOptions.has(iconKey + "$1")) {
				int current = 1;
				String extraKey = iconKey + "$" + current;
				while (fontOptions.has(extraKey)) {
					JsonObject iconJson = fontOptions.getAsJsonObject(extraKey);
					addIcon(current == 1 ? iconFile : null, iconKey, iconJson, namespace);
					extraKey = iconKey + "$" + ++current;
				}
			} else {
				var iconJson = fontOptions.has(iconKey) ? fontOptions.getAsJsonObject(iconKey) : new JsonObject();
				addIcon(iconFile, iconKey, iconJson, namespace);
			}
		}
	}

	public void addCustomEffects(@NotNull String namespace) {
		var effectsDir = new File(this.generator.getUserDataDir(), namespace + "/effect");
		if (!effectsDir.exists()) return;

		Iterator<PotionEffectType> iterator = Registry.EFFECT.iterator();
		iterator.forEachRemaining(potionEffectType -> {
			NamespacedKey key = potionEffectType.getKey();
			if (!namespace.equals(key.getNamespace())) return;

			var iconFile = new File(effectsDir, key.getKey() + ".png");
			if (!iconFile.exists()) {
				ResourceLib.logger().error("Couldn't find effect texture: {}", key);
				return;
			}

			var options = new JsonObject();
			options.addProperty("ascent", potionEffectType.getCategory() == PotionEffectTypeCategory.BENEFICIAL ? -68 : -94);
			options.addProperty("height", 20);
			addIcon(iconFile, "effect/" + key.getKey(), options, namespace);
			addIcon(iconFile, "effect/" + key.getKey() + "_icon", new JsonObject(), namespace);
		});
	}

	public @NotNull String addIcon(@Nullable File iconFile, @NotNull String iconKey, @NotNull JsonObject options, @Subst("rlib") @Nullable String namespace) {
		return addIcon(iconFile, iconKey, options, namespace, false);
	}

	public @NotNull String addIcon(@Nullable File iconFile, @NotNull String iconKey, @NotNull JsonObject options, @Subst("rlib") @Nullable String namespace, boolean hardcoded) {
		if (iconKey.contains("$"))
			iconKey = iconKey.split("\\$")[0];
		boolean referenceTexture = iconFile == null;
		String textureMapping = referenceTexture ? iconKey : "font/" + iconKey;
		String mappingKey = options.has("map") ? options.get("map").getAsString() : iconKey;
		boolean vanilla = "minecraft".equals(namespace);
		if (namespace == null)
			namespace = generator.getPackOptions().getPackNamespace();

		if (referenceTexture) {
			if (vanilla) {
				iconFile = new File(generator.getMcAssetsDir(), "assets/minecraft/textures/" + textureMapping + ".png");
				if (!iconFile.exists())
					iconFile = new File(generator.getOutputDir(), "assets/minecraft/textures/" + textureMapping + ".png");
				if (!iconFile.exists()) {
					ResourceLib.logger().error("[FontCreator] Minecraft texture mapping not found: {}", textureMapping);
					return iconKey;
				}
			} else {
				iconFile = FileUtil.findFile(generator.getOutputDir(), iconKey + ".png");
				if (iconFile == null) {
					ResourceLib.logger().error("[FontCreator] Custom texture mapping not found: {}", iconKey);
					return iconKey;
				}
				textureMapping = iconFile.getPath().split("textures")[1].substring(1);
				textureMapping = textureMapping.split("\\.", 2)[0];
				textureMapping = textureMapping.replace(File.pathSeparator, "/");
			}
		} else {
			FileUtil.copyFile(iconFile, new File(generator.getOutputDir(), "assets/" + namespace + "/textures/" + textureMapping + ".png"));
		}

		// We may have multiple icons for one texture (with different ascent, height, etc.)
		if (generator.getStorage().getFontMapping(mappingKey) != null) {
			if (!referenceTexture)
				ResourceLib.logger().warn("Unexpected duplicate font found: {}", mappingKey);
			int id = 2;
			while (generator.getStorage().getFontMapping(mappingKey + "$" + id) != null)
				id++;
			mappingKey += "$" + id;
		}

		var iconJson = new JsonObject();
		iconJson.addProperty("type", "bitmap");
		iconJson.addProperty("ascent", options.has("ascent") ? options.get("ascent").getAsInt() : 7);
		if (options.has("height"))
			iconJson.addProperty("height", options.get("height").getAsInt());
		if (vanilla) {
			iconJson.addProperty("file", textureMapping + ".png");
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

		int width = 0;
		int height = iconJson.has("height") ? iconJson.get("height").getAsInt() : 8;
		int compensationPixels = 0;
		try {
			BufferedImage bimg = ImageIO.read(iconFile);
			int iheight = bimg.getHeight();
			double scale = height / (double) iheight;
			// Texture width might not equal to height, and MC scales by height, so calculate width manually
			width = (int) Math.floor(bimg.getWidth() * scale);

			// Minecraft strips trailing empty columns from font textures, compensate...
			compensationPixels = (int) Math.floor(countEmptyColumnsFromEnd(bimg) * scale);
		} catch (IOException e) {
			ResourceLib.logger().warn("[FontCreator] Couldn't read image data: {} at {}", iconKey, iconFile.getPath());
		}

		var fontKey = Key.key(generator.getPackOptions().getPackNamespace(), "fonts"); // TODO separate namespaces
		if (size > 1) {
			char[] chars = sb.toString().toCharArray();
			if (hardcoded) {
				var combined = new StringBuilder();
				for (var i = 0; i < chars.length; i++) {
					combined.append(chars[i]);
					if (i + 1 != chars.length)
						combined.append(SpacingUtil.NEGATIVE_PIXEL);
				}
				Component mapping = Component.text(combined.toString()).font(fontKey);
				if (compensationPixels > 0)
					mapping = combined(mapping, Component.text(SpacingUtil.POSITIVE_PIXEL.repeat(compensationPixels)));
				generator.getStorage().addFontMapping(namespace + ":" + mappingKey, mapping, width);
			} else {
				width /= size;
				for (var i = 0; i < chars.length; i++) {
					Component mapping = Component.text(String.valueOf(chars[i])).font(fontKey);
					if (i == chars.length - 1 && compensationPixels > 0)
						mapping = combined(mapping, Component.text(SpacingUtil.POSITIVE_PIXEL.repeat(compensationPixels)));
					generator.getStorage().addFontMapping(namespace + ":" + mappingKey + "$" + (i + 1), mapping, width);
				}
			}
		} else {
			Component mapping = Component.text(sb.toString()).font(fontKey);
			if (compensationPixels > 0)
				mapping = combined(mapping, Component.text(SpacingUtil.POSITIVE_PIXEL.repeat(compensationPixels)));
			generator.getStorage().addFontMapping(namespace + ":" + mappingKey, mapping, width);
		}
		iconChars.add(sb.toString());
		iconJson.add("chars", iconChars);
		if (hardcoded)
			defLangJson.getAsJsonArray("providers").add(iconJson);
		else
			fontsJson.getAsJsonArray("providers").add(iconJson);
		return sb.toString();
	}

	private int countEmptyColumnsFromEnd(@NotNull BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int emptyColumns = 0;

		for (int x = width - 1; x >= 0; x--) {
			boolean isEmpty = true;
			for (int y = 0; y < height; y++) {
				int pixel = image.getRGB(x, y);
				if ((pixel & 0xFF000000) != 0x00000000) {  // Check if the pixel is not fully transparent
					isEmpty = false;
					break;
				}
			}
			if (!isEmpty) break;

			emptyColumns++;
		}
		return emptyColumns;
	}

	public void generateFonts() {
		FileUtil.createPrettyJsonFile(new File(generator.getOutputDir(), "assets/" + generator.getPackOptions().getPackNamespace() + "/font/fonts.json"), fontsJson);

		var langFilesDir = new File(generator.getOutputDir(), "assets/minecraft/font");
		var defLangFile = new File(langFilesDir, "default.json");
		FileUtil.createPrettyJsonFile(defLangFile, defLangJson);
		FileUtil.copyFile(defLangFile, new File(langFilesDir, "uniform.json"));
	}

}
