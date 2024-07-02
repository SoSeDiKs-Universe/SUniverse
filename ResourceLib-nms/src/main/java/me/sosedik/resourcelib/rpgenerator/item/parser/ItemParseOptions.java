package me.sosedik.resourcelib.rpgenerator.item.parser;

import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public record ItemParseOptions(
	@NotNull Material vanillaType,
	@NotNull String namespace,
	@NotNull String path,
	@NotNull String key,
	@NotNull JsonObject options
) {

	public @NotNull String combinedKeyWithPath() {
		return this.namespace + ":" + this.path + this.key;
	}

	public @NotNull NamespacedKey namespacedKey() {
		return new NamespacedKey(this.namespace, this.key);
	}

}
