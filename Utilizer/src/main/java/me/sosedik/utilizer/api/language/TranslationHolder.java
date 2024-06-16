package me.sosedik.utilizer.api.language;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.util.FileUtil;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TranslationHolder {

	private static final TranslationHolder TRANSLATION_HOLDER = new TranslationHolder();
	private static final Random RANDOM = new Random();

	private final Map<LangKey, JsonObject> locales = new HashMap<>();

	public @NotNull String @NotNull [] getMessage(@NotNull LangKey langKey, @NotNull String path) {
		return getMessage(langKey, path, true);
	}

	@Contract("_, _, true -> !null")
	public @NotNull String @Nullable [] getMessage(@NotNull LangKey langKey, @NotNull String path, boolean scream) {
		JsonObject langJson = locales.get(langKey);
		if (langJson == null || !langJson.has(path)) {
			// Retry for default language
			LangKey defaultLangKey = LangKeysStorage.getDefaultLangKey();
			if (defaultLangKey != langKey)
				return getMessage(defaultLangKey, path, scream);
			if (scream) {
				Utilizer.logger().warn("Missing localization for {}", path);
				return new String[]{path};
			}
			return null;
		}

		JsonElement jsonElement = langJson.get(path);

		// Simple message
		if (jsonElement instanceof JsonPrimitive)
			return new String[]{jsonElement.getAsString()};

		// Multi-lined message
		if (jsonElement instanceof JsonArray jsonArray) {
			String[] locale = new String[jsonArray.size()];
			for (int i = 0; i < jsonArray.size(); i++)
				locale[i] = jsonArray.get(i).getAsString();
			return locale;
		}

		langJson = jsonElement.getAsJsonObject();

		// Multi-lined message
		if (langJson.has("1"))
			return parseMultiLined(langJson);

		// Simple randomized message
		if (langJson.has("1r"))
			return parseRandomized(langJson);

		// Complicated randomized message
		if (langJson.has("0r"))
			return parseRandomized(langJson, path);

		// Unreachable
		return new String[]{path};
	}

	private @NotNull String @NotNull [] parseRandomized(@NotNull JsonObject langJson) {
		var amount = 1;
		while (langJson.has((amount + 1) + "r"))
			amount++;
		return parseElement(langJson.get((RANDOM.nextInt(amount) + 1) + "r"));
	}

	private @NotNull String @NotNull [] parseRandomized(@NotNull JsonObject langJson, @NotNull String path) {
		// Get all available random messages
		List<Map.Entry<String, JsonElement>> randoms = langJson.entrySet().stream()
				.filter(entry -> entry.getKey().endsWith("r")).toList();
		// Whether zero was used only as detector
		boolean ignoreZero = langJson.get("0r") instanceof JsonPrimitive jsonPrimitive && jsonPrimitive.getAsString().isEmpty();
		// Calculate the range
		var roll = 0;
		for (Map.Entry<String, JsonElement> entry : randoms) {
			String[] split = entry.getKey().split("\\.");
			String num = split[split.length - 1].replace("r", "");
			if (num.contains("-")) {
				roll = Math.max(roll, Integer.parseInt(num.split("-")[1]));
			} else {
				roll = Math.max(roll, Integer.parseInt(num));
			}
		}
		// Pick random
		roll = RANDOM.nextInt(roll + 1);
		if (ignoreZero && roll == 0)
			roll++;
		// Find and return it
		for (Map.Entry<String, JsonElement> entry : randoms) {
			String[] split = entry.getKey().split("\\.");
			String num = split[split.length - 1].replace("r", "");
			if (num.contains("-")) {
				String[] ranges = num.split("-");
				if (roll >= Integer.parseInt(ranges[0]) && roll <= Integer.parseInt(ranges[1])) {
					return parseElement(entry.getValue());
				}
			} else if (roll == Integer.parseInt(num)) {
				return parseElement(entry.getValue());
			}
		}
		Utilizer.logger().error("Could not find randomized message for {}", path);
		return new String[]{path};
	}

	private @NotNull String @NotNull [] parseElement(@NotNull JsonElement jsonElement) {
		// Simple message
		if (jsonElement instanceof JsonPrimitive)
			return new String[]{jsonElement.getAsString()};
		// Multi message
		return parseMultiLined(jsonElement.getAsJsonObject());
	}

	private @NotNull String @NotNull [] parseMultiLined(@NotNull JsonObject langJson) {
		int amount = langJson.size();
		String[] lines = new String[amount];
		for (int i = 0; i < amount; i++)
			lines[i] = langJson.get(String.valueOf(i + 1)).getAsString();
		return lines;
	}

	/**
	 * Extracts locales for specified plugin
	 *
	 * @param plugin plugin instance
	 */
	public static void extractLocales(@NotNull Plugin plugin) {
		var jarFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		if (!jarFile.isFile()) {
			Utilizer.logger().error("Couldn't obtain the JAR file for {}", plugin.getName());
			Utilizer.logger().error("Last known location: {}", plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			return;
		}

		Map<String, List<JsonObject>> toCompute = new HashMap<>();
		try (var jar = new JarFile(jarFile)) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (!name.startsWith("localizations/")) continue;
				if (!name.endsWith(".json")) continue;

				name = name.substring("localizations/".length(), name.length() - ".json".length());
				try (
					InputStream inputStream = jar.getInputStream(entry);
					var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
				) {
					JsonObject locale = new Gson().fromJson(reader, JsonObject.class);
					if (!locale.has("locale.id")) {
						toCompute.computeIfAbsent(name.split("/")[0], k -> new ArrayList<>()).add(locale);
					} else {
						String rawLangKey = locale.get("locale.id").getAsString();
						LangKey langKey = LangKeysStorage.getOrCompute(rawLangKey, () -> LangKey.fromJson(locale));
						loadJson(locale, langKey);
					}
				}
//				if (name.startsWith("localizations/") && name.endsWith(".json"))
//					plugin.saveResource(name, true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		toCompute.forEach((id, jsons) -> {
			LangKey langKey = LangKeysStorage.getLangKeyIfExists(id);
			if (langKey != null)
				jsons.forEach(json -> loadJson(json, langKey));
		});
		reloadLangs(plugin);
	}

	/**
	 * Reloads locales for specified plugin
	 *
	 * @param plugin plugin instance
	 */
	public static void reloadLangs(@NotNull Plugin plugin) {
		var localesFolder = new File(plugin.getDataFolder() + File.separator + "localizations");
		if (!localesFolder.exists()) {
			localesFolder.mkdirs();
			return;
		}
		if (!localesFolder.isDirectory()) return;
		if (Objects.requireNonNull(localesFolder.listFiles()).length == 0) return;

		Map<String, List<JsonObject>> toCompute = new HashMap<>();
		for (File file : Objects.requireNonNull(localesFolder.listFiles())) {
			loadLangs(localesFolder, file.getName(), toCompute);
		}
		toCompute.forEach((id, jsons) -> {
			LangKey langKey = LangKeysStorage.getLangKeyIfExists(id);
			if (langKey != null)
				jsons.forEach(json -> loadJson(json, langKey));
		});
	}

	private static void loadLangs(@NotNull File file, @NotNull String parentName, @NotNull Map<String, List<JsonObject>> locales) {
		if (file.isDirectory()) {
			for (File f : Objects.requireNonNull(file.listFiles())) {
				loadLangs(f, parentName, locales);
			}
			return;
		}
		if (!file.getName().endsWith(".json")) return;

		JsonObject json = FileUtil.readJsonObject(file);
		if (json.has("locale.id")) {
			String rawLangKey = json.get("locale.id").getAsString();
			LangKey langKey = LangKeysStorage.getOrCompute(rawLangKey, () -> LangKey.fromJson(json));
			loadJson(json, langKey);
		} else {
			locales.computeIfAbsent(parentName, k -> new ArrayList<>()).add(json);
		}
	}

	private static void loadJson(@NotNull JsonObject json, @NotNull LangKey langKey) {
		var localesJson = TRANSLATION_HOLDER.locales.computeIfAbsent(langKey, k -> new JsonObject());
		try {
			json.entrySet().forEach(entry -> localesJson.add(entry.getKey(), entry.getValue()));
		} catch (JsonSyntaxException | JsonIOException e) {
			e.printStackTrace();
		}
	}

	public static @NotNull TranslationHolder translationHolder() {
		return TRANSLATION_HOLDER;
	}

}
