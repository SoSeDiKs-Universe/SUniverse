package me.sosedik.utilizer.api.language;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LangOptionsStorage {

	private static final LangOptionsStorage LANG_OPTIONS_STORAGE = new LangOptionsStorage();

	private final Map<String, LangOptions> supportedLanguages = new HashMap<>();
	private LangOptions defaultLanguage;

	/**
	 * Gets language name by locale
	 *
	 * @param locale locale
	 * @return language options
	 */
	public static @NotNull LangOptions getByLocale(@NotNull Locale locale) {
		return getLangOptions(locale.getLanguage() + "_" + locale.getCountry());
	}

	/**
	 * Gets language name by Minecraft's minecraftId
	 *
	 * @param key Minecraft's minecraftId
	 * @return language options
	 */
	public static @NotNull LangOptions getLangOptions(@Nullable String key) {
		LangOptions langOptions = getLangOptionsIfExist(key);
		return langOptions == null ? getDefaultLangOptions() : langOptions;
	}

	/**
	 * Gets language name by Minecraft's minecraftId
	 *
	 * @param key Minecraft's minecraftId
	 * @return language options, or null
	 */
	public static @Nullable LangOptions getLangOptionsIfExist(@Nullable String key) {
		return LANG_OPTIONS_STORAGE.supportedLanguages.get(key);
	}

	/**
	 * Gets language name by Minecraft's minecraftId
	 *
	 * @param key Minecraft's minecraftId
	 * @return language options, or null
	 */
	public static @NotNull LangOptions getOrCompute(@NotNull String key) {
		LangOptions langOptions = getLangOptionsIfExist(key);
		if (langOptions == null) {
			langOptions = new LangOptions(key);
			LANG_OPTIONS_STORAGE.supportedLanguages.put(key, langOptions);
		}
		return langOptions;
	}

	/**
	 * Gets language name by user's IP address
	 *
	 * @param address IP address to parse country from
	 * @return language options
	 */
	public static @NotNull LangOptions getByAddress(@NotNull String address) {
		try (
			InputStream is = new URI("http://ip-api.com/json/" + address + "?fields=1").toURL().openStream();
			Reader reader = new InputStreamReader(is)
		) {
			JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
			return obj.has("country") ? getByCountry(obj.get("country").getAsString()) : getDefaultLangOptions();
		} catch (IOException | URISyntaxException e) {
			if (!"ip-api.com".equals(address))
				Utilizer.logger().warn("Invalid address link! {}", address);
			return getDefaultLangOptions();
		}
	}

	/**
	 * Gets language name by user's IP country
	 *
	 * @param country Country to parse language name from
	 * @return language options
	 */
	public static @NotNull LangOptions getByCountry(@NotNull String country) {
		for (LangOptions langOptions : getSupportedLanguages()) {
			if (country.equalsIgnoreCase(langOptions.country()))
				return langOptions;
		}
		return getDefaultLangOptions();
	}

	/**
	 * Gets default language of this server
	 *
	 * @return default language options
	 */
	public static @NotNull LangOptions getDefaultLangOptions() {
		return LANG_OPTIONS_STORAGE.defaultLanguage;
	}

	/**
	 * Gets all supported languages on this server
	 *
	 * @return supported language options
	 */
	public static @NotNull Collection<@NotNull LangOptions> getSupportedLanguages() {
		return LANG_OPTIONS_STORAGE.supportedLanguages.values();
	}

	public static void init(@NotNull Utilizer plugin) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("default-language"))
			config.set("default-language", "en_us");
		String defaultLanguageId = config.getString("default-language");
		LangOptions defaultLanguage = getLangOptionsIfExist(defaultLanguageId);
		if (defaultLanguage == null) {
			Utilizer.logger().error("Couldn't find default language: {}", defaultLanguageId);
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
		if (!config.contains("supported-languages")) {
			ConfigurationSection defaultLang = config.createSection("supported-languages").createSection("en_us");
			defaultLang.set("country", "Worldwide");
		}
		if (config.isConfigurationSection("supported-languages")) {
			ConfigurationSection supportedKeys = config.getConfigurationSection("supported-languages");
			assert supportedKeys != null;
			for (String minecraftId : supportedKeys.getKeys(false)) {
				LangOptions langOptions = getLangOptionsIfExist(minecraftId);
				if (langOptions == null) continue;

				if (supportedKeys.contains("country"))
					langOptions.metadata().put("country", Objects.requireNonNull(supportedKeys.getString("country")));
			}
		}
		LANG_OPTIONS_STORAGE.defaultLanguage = defaultLanguage;
	}

}
