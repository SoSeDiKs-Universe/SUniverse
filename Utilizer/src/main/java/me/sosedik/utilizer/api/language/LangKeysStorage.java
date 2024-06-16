package me.sosedik.utilizer.api.language;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.sosedik.utilizer.Utilizer;
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
import java.util.Map;
import java.util.function.Supplier;

public class LangKeysStorage {

	private static final LangKeysStorage LANG_KEYS_STORAGE = new LangKeysStorage();

	private final Map<String, LangKey> supportedLangKeys = new HashMap<>();
	private LangKey defaultLangKey;

	/**
	 * Gets language name by Minecraft's locale
	 *
	 * @param key Minecraft's locale
	 * @return language name
	 */
	public static @NotNull LangKey getLangKey(@Nullable String key) {
		LangKey langKey = getLangKeyIfExists(key);
		return langKey == null ? getDefaultLangKey() : langKey;
	}

	/**
	 * Gets language name by Minecraft's locale
	 *
	 * @param key Minecraft's locale
	 * @return language name, or null
	 */
	public static @Nullable LangKey getLangKeyIfExists(@Nullable String key) {
		return LANG_KEYS_STORAGE.supportedLangKeys.get(key);
	}

	/**
	 * Gets language name by Minecraft's locale
	 *
	 * @param key Minecraft's locale
	 * @param supplier lang key supplier
	 * @return language name, or null
	 */
	public static @NotNull LangKey getOrCompute(@Nullable String key, @NotNull Supplier<@NotNull LangKey> supplier) {
		LangKey langKey = getLangKeyIfExists(key);
		if (langKey == null) {
			langKey = supplier.get();
			LANG_KEYS_STORAGE.supportedLangKeys.put(key, langKey);
		}
		return langKey;
	}

	/**
	 * Gets language name by user's IP address
	 *
	 * @param address IP address to parse country from
	 * @return language name
	 */
	public static @NotNull LangKey getByAddress(@NotNull String address) {
		try (
			InputStream is = new URI("http://ip-api.com/json/" + address + "?fields=1").toURL().openStream();
			Reader reader = new InputStreamReader(is)
		) {
			JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
			return obj.has("country") ? getByCountry(obj.get("country").getAsString()) : getDefaultLangKey();
		} catch (IOException | URISyntaxException e) {
			if (!"ip-api.com".equals(address))
				Utilizer.logger().warn("Invalid address link! {}", address);
			return getDefaultLangKey();
		}
	}

	/**
	 * Gets language name by user's IP country
	 *
	 * @param country Country to parse language name from
	 * @return language name
	 */
	public static @NotNull LangKey getByCountry(@NotNull String country) {
		for (LangKey langKey : getSupportedLanguages()) {
			if (langKey.country().equals(country))
				return langKey;
		}
		return getDefaultLangKey();
	}

	/**
	 * Gets default language of this server
	 *
	 * @return default language name
	 */
	public static @NotNull LangKey getDefaultLangKey() {
		return LANG_KEYS_STORAGE.defaultLangKey;
	}

	/**
	 * Gets all supported languages on this server
	 *
	 * @return supported languages
	 */
	public static @NotNull Collection<@NotNull LangKey> getSupportedLanguages() {
		return LANG_KEYS_STORAGE.supportedLangKeys.values();
	}

	public static void init(@NotNull Utilizer plugin) {
		String defaultLangKeyId = plugin.getConfig().getString("default-language", "en_us");
		LangKey langKey = getLangKeyIfExists(defaultLangKeyId);
		if (langKey == null) {
			Utilizer.logger().error("Couldn't find default language: {}", defaultLangKeyId);
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
		LANG_KEYS_STORAGE.defaultLangKey = langKey;
	}

}
