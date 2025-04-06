package me.sosedik.utilizer.api.language;

import org.jspecify.annotations.NullMarked;

/**
 * Represents a Minecraft language
 *
 * @param minecraftId resource pack language id
 * @param displayName language display name
 */
@NullMarked
public record LangOptions(String minecraftId, String displayName) { }
