package me.sosedik.resourcelib.rpgenerator.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.sosedik.utilizer.util.FileUtil;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class PackOptions {

	private final File optionsFile;
	private final JsonObject options;
	private boolean dirty;

	public PackOptions(@NotNull File optionsDir) {
		this.optionsFile = new File(optionsDir, "pack_options.json");
		this.dirty = !optionsFile.exists();
		this.options = dirty ? new JsonObject() : FileUtil.readJsonObject(optionsFile);

		if (!this.options.has("true")) {
			this.options.addProperty("true", false);
			getPackVersion();
			shouldObfuscate();
			shouldCorrupt();
			getDescription();
			getPackNamespace();
			getDefaultItemMaterial();
			getResourcePackPort();
			this.dirty = true;
		}
	}

	public boolean isValid() {
		return getPackVersion() != -1;
	}

	public int getPackVersion() {
		if (!this.options.has("pack_version")) {
			this.options.addProperty("pack_version", -1);
			this.dirty = true;
		}
		return this.options.get("pack_version").getAsInt();
	}

	public boolean shouldObfuscate() {
		if (!this.options.has("obfuscate")) {
			this.options.addProperty("obfuscate", false);
			this.dirty = true;
		}
		return this.options.get("obfuscate").getAsBoolean();
	}

	public boolean shouldCorrupt() {
		if (!this.options.has("corrupt")) {
			this.options.addProperty("corrupt", false);
			this.dirty = true;
		}
		return this.options.get("corrupt").getAsBoolean();
	}

	public @NotNull String @NotNull [] getDescription() {
		if (!this.options.has("description")) {
			var description = new JsonArray();
			description.add("Server resource pack");
			this.options.add("description", description);
			this.dirty = true;
		}
		if (this.options.get("description") instanceof JsonArray jsonArray) {
			var description = new String[jsonArray.size()];
			for (int i = 0; i < jsonArray.size(); i++)
				description[i] = jsonArray.get(i).getAsString();
			return description;
		}
		return new String[]{this.options.get("description").getAsString()};
	}

	public @NotNull String getPackNamespace() {
		if (!this.options.has("pack_namespace")) {
			this.options.addProperty("pack_namespace", "rlib");
			this.dirty = true;
		}
		return this.options.get("pack_namespace").getAsString();
	}

	public @NotNull Material getDefaultItemMaterial() {
		if (!this.options.has("default_item_material")) {
			this.options.addProperty("default_item_material", Material.IRON_NUGGET.getKey().asString());
			this.dirty = true;
		}
		return Objects.requireNonNull(Material.matchMaterial(this.options.get("default_item_material").getAsString()));
	}

	public int getResourcePackPort() {
		if (!this.options.has("http_port")) {
			this.options.addProperty("http_port", -1);
			this.dirty = true;
		}
		return this.options.get("http_port").getAsInt();
	}

	public void save() {
		if (this.dirty) FileUtil.createPrettyJsonFile(this.optionsFile, this.options);
	}

}
