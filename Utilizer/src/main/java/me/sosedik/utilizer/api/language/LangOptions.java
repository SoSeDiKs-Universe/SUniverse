package me.sosedik.utilizer.api.language;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LangOptions {

	private final String minecraftId;
	private final Map<String, Object> metadata = new HashMap<>();

	/**
	 * Constructs language options
	 *
	 * @param minecraftId language's minecraft locale id in game
	 */
	public LangOptions(@NotNull String minecraftId) {
		this.minecraftId = minecraftId;
	}

	/**
	 * Gets minecraft's in-game language id
	 *
	 * @return minecraft language id
	 */
	public @NotNull String minecraftId() {
		return this.minecraftId;
	}

	/**
	 * Gets mutable language metadata
	 *
	 * @return mutable language metadata
	 */
	public @NotNull Map<@NotNull String, @NotNull Object> metadata() {
		return this.metadata;
	}

	/**
	 * Gets the country metadata, if specified
	 *
	 * @return country, if specified
	 */
	public @Nullable String country() {
		return (String) metadata().get("country");
	}

	/**
	 * Gets language id used by translator
	 *
	 * @return translator language id, if specified
	 */
	public @Nullable String translatorId() {
		return (String) metadata().get("translator_id");
	}

}
