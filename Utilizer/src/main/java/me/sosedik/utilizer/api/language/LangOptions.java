package me.sosedik.utilizer.api.language;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a Minecraft language
 *
 * @param minecraftId resource pack language id
 * @param displayName language display name
 */
public record LangOptions(
		@NotNull String minecraftId,
		@NotNull String displayName
) { }
