package me.sosedik.utilizer.api.language;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.language.translator.TranslationLanguage;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LangOptionsStorage {

	private static final LangOptionsStorage LANG_OPTIONS_STORAGE = new LangOptionsStorage();

	private final Map<String, LangOptions> keyToLang = new HashMap<>();
	private final Map<String, LangOptions> countryToLang = new HashMap<>();
	private final Map<String, TranslationLanguage> keyToTranslator = new HashMap<>();
	private final Map<String, TranslationLanguage> langToTranslator = new HashMap<>();
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
		return LANG_OPTIONS_STORAGE.keyToLang.get(key);
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
	 * Gets language name by user's country
	 *
	 * @param country country to parse language name from
	 * @return language options
	 */
	public static @NotNull LangOptions getByCountry(@NotNull String country) {
		LangOptions langOptions = LANG_OPTIONS_STORAGE.countryToLang.get(country.toLowerCase());
		return langOptions != null ? langOptions : getDefaultLangOptions();
	}

	/**
	 * Gets language name by translator id
	 *
	 * @param translatorId translator id to parse language name from
	 * @return language options
	 */
	public static @Nullable TranslationLanguage getTranslator(@NotNull String translatorId) {
		return LANG_OPTIONS_STORAGE.keyToTranslator.get(translatorId);
	}

	/**
	 * Gets language name by Minecraft's lang key
	 *
	 * @param minecraftId translator id to parse language name from
	 * @return language options
	 */
	public static @NotNull TranslationLanguage getTranslatorLanguage(@NotNull String minecraftId) {
		TranslationLanguage lang = LANG_OPTIONS_STORAGE.langToTranslator.get(minecraftId);
		if (lang != null) return lang;

		String defaultKey = getDefaultLangOptions().minecraftId();
		return LANG_OPTIONS_STORAGE.langToTranslator.get(defaultKey);
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
		return LANG_OPTIONS_STORAGE.keyToLang.values();
	}

	/**
	 * Gets all supported languages on this server
	 *
	 * @return supported language options
	 */
	public static @NotNull Collection<@NotNull TranslationLanguage> getSupportedTranslators() {
		return LANG_OPTIONS_STORAGE.keyToTranslator.values();
	}

	public static void init(@NotNull Utilizer plugin) {
		FileConfiguration config = plugin.getConfig();

		if (config.contains("translators")) {
			ConfigurationSection translators = config.getConfigurationSection("translators");
			translators.getKeys(false).forEach(id -> {
				String displayName = translators.getString(id, id);
				LANG_OPTIONS_STORAGE.keyToTranslator.put(id, new TranslationLanguage(id, displayName));
			});
		}

		if (!config.contains("supported-languages")) {
			ConfigurationSection defaultLang = config.createSection("supported-languages").createSection("en_us");
			defaultLang.set("name", "English");
			defaultLang.set("extra-langs", List.of());
			defaultLang.set("translator", "en");
			defaultLang.set("countries", List.of("Worldwide"));
		}

		ConfigurationSection supportedLangs = config.getConfigurationSection("supported-languages");
		assert supportedLangs != null;
		for (String minecraftId : supportedLangs.getKeys(false)) {
			ConfigurationSection supportedLang = supportedLangs.getConfigurationSection(minecraftId);
			assert supportedLang != null;
			String displayName = supportedLang.getString("name", minecraftId);

			var langOptions = new LangOptions(minecraftId, displayName);
			LANG_OPTIONS_STORAGE.keyToLang.put(minecraftId, langOptions);

			List<String> extraLangs = supportedLang.getStringList("extra-langs");
			for (String langId: extraLangs)
				LANG_OPTIONS_STORAGE.keyToLang.put(langId, langOptions);

			List<String> countries = supportedLang.getStringList("countries");
			for (String country : countries)
				LANG_OPTIONS_STORAGE.countryToLang.put(country.toLowerCase(), langOptions);

			String translatorId = supportedLang.getString("translator");
			if (translatorId == null) continue;

			TranslationLanguage translator = LANG_OPTIONS_STORAGE.keyToTranslator.get(translatorId);
			if (translator == null)
				Utilizer.logger().error("Unknown translator: {}", translatorId);
			else
				LANG_OPTIONS_STORAGE.langToTranslator.put(minecraftId, translator);
		}

		if (!config.contains("default-language"))
			config.set("default-language", "en_us");

		String defaultLanguageId = config.getString("default-language");
		LangOptions defaultLanguage = getLangOptionsIfExist(defaultLanguageId);
		if (defaultLanguage == null) {
			Utilizer.logger().error("Couldn't find default language: {}", defaultLanguageId);
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
		LANG_OPTIONS_STORAGE.defaultLanguage = defaultLanguage;
		Utilizer.logger().info("Using {} language as the default", defaultLanguage.minecraftId());
	}

}
