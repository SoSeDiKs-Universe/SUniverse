package me.sosedik.resourcelib.rpgenerator.extras;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import me.sosedik.utilizer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class PackRemapper {

	private final File resourcePackDir;
	private final File outputDir;
	private final Map<String, String> dirRemaps = new HashMap<>();
	private final Map<String, String> textureMappings = new HashMap<>();
	private final Map<String, String> modelMappings = new HashMap<>();
	private final Random random = new Random();
	private final String fontsRemapDirName = getRename0(1, dirRemaps);
	private final String itemsRemapDirName = getRename0(1, dirRemaps);

	public PackRemapper(@NotNull ResourcePackGenerator generator) {
		this.resourcePackDir = generator.getOutputDir();
		this.outputDir = new File(resourcePackDir.getParentFile(), resourcePackDir.getName() + "-obf");

		generator.setOutputDir(this.outputDir);
	}

	public void remap() {
		FileUtil.deleteFolder(this.outputDir);

		copyMcAssets();

		var dir = new File(resourcePackDir, "assets");

		for (File namespaceDir : Objects.requireNonNull(dir.listFiles())) {
			if (!namespaceDir.isDirectory() || namespaceDir.listFiles() == null) continue;

			String namespace = namespaceDir.getName();
			for (File typeDir : Objects.requireNonNull(namespaceDir.listFiles())) {
				String type = typeDir.getName();
				if ("minecraft".equals(namespace) && "models".equals(type)) {
					remapItemModels(typeDir);
				} else if ("font".equals(type)) {
					remapFonts(typeDir, namespace);
				} else if ("minecraft".equals(namespace)) {
					FileUtil.copyFile(typeDir, new File(this.outputDir, "assets/minecraft/" + type));
				}
			}
		}

		createItemsAtlas();

		ResourceLib.logger().info("Remapped assets");
	}

	private void remapItemModels(@NotNull File modelsDir) {
		// TODO
		modelsDir = new File(modelsDir, "item");
		if (!modelsDir.exists()) return;

		for (File vanillaModelFile : Objects.requireNonNull(modelsDir.listFiles())) {
			if (!vanillaModelFile.isFile()) continue;
			if (!vanillaModelFile.getName().endsWith(".json")) continue;

			var vanillaOutputFile = new File(this.outputDir, "assets/minecraft/models/item/" + vanillaModelFile.getName());

			JsonObject json = FileUtil.readJsonObject(vanillaModelFile);
			if (!json.has("overrides")) {
				FileUtil.copyFile(vanillaModelFile, vanillaOutputFile);
				continue;
			}
			JsonArray overrides = json.getAsJsonArray("overrides");
			for (JsonElement overrideEntryE : overrides) {
				JsonObject override = overrideEntryE.getAsJsonObject();
				String modelPath = override.get("model").getAsString();
				if (!modelPath.contains(":")) continue;

				String[] split = modelPath.split(":");
				String namespace = split[0];
				String path = split[1];
				if ("minecraft".equals(namespace)) continue;

				var modelFile = new File(this.resourcePackDir, "assets/" + namespace + "/models/" + path + ".json");
				if (!modelFile.exists()) {
					ResourceLib.logger().warn("[PackRemapper] Couldn't find requested model: {}", modelPath);
					continue;
				}

				String modelMapping = getModelRename(modelPath);
				override.addProperty("model", modelMapping);
				var outputFile = new File(this.outputDir, "assets/minecraft/models/" + modelMapping + ".json");
				remapModelTextures(modelFile, outputFile);
			}
			FileUtil.createJsonFile(vanillaOutputFile, json);
		}
	}

	private void remapModelTextures(@NotNull File modelFile, @NotNull File outputModelFile) {
		JsonObject json = FileUtil.readJsonObject(modelFile);
		if (!json.has("textures")) return;

		JsonObject textures = json.getAsJsonObject("textures");
		textures.entrySet().forEach(entry -> {
			String texturePath = entry.getValue().getAsString();
			if (!texturePath.contains(":")) return;

			String[] split = texturePath.split(":");
			String namespace = split[0];
			String path = split[1];
			if ("minecraft".equals(namespace)) return;

			var textureFile = new File(this.resourcePackDir, "assets/" + namespace + "/textures/" + path + ".png");
			if (!textureFile.exists()) {
				ResourceLib.logger().warn("[PackRemapper] Couldn't find requested texture: {}", texturePath);
				return;
			}

			String textureMapping = getTextureRename(texturePath);
			textures.addProperty(entry.getKey(), textureMapping);
			var outputFile = new File(this.outputDir, "assets/minecraft/textures/" + itemsRemapDirName + "/" + textureMapping + ".png");
			copyTexture(textureFile, outputFile);
		});
		FileUtil.createJsonFile(outputModelFile, json);
	}

	private void createItemsAtlas() {
		var atlasFile = new File(this.resourcePackDir, "assets/minecraft/atlases/blocks.json");
		JsonObject atlases = atlasFile.exists() ? FileUtil.readJsonObject(atlasFile) : emptyAtlases();
		JsonArray sources = atlases.getAsJsonArray("sources");
		var source = new JsonObject();
		source.addProperty("type", "directory");
		source.addProperty("source", itemsRemapDirName);
		source.addProperty("prefix", "");
		sources.add(source);
		atlasFile = new File(this.outputDir, "assets/minecraft/atlases/blocks.json");
		FileUtil.createJsonFile(atlasFile, atlases);
	}

	private @NotNull JsonObject emptyAtlases() {
		var atlases = new JsonObject();
		var sources = new JsonArray();
		atlases.add("sources", sources);
		return atlases;
	}

	private void remapFonts(@NotNull File fontsDir, @NotNull String namespace) {
		for (File fontFile : Objects.requireNonNull(fontsDir.listFiles())) {
			JsonObject json = FileUtil.readJsonObject(fontFile);
			JsonArray providers = json.getAsJsonArray("providers");
			for (JsonElement providerE : providers) {
				JsonObject provider = providerE.getAsJsonObject();
				if (!provider.has("file")) continue;

				String filePath = provider.get("file").getAsString();
				if (!filePath.contains(":")) continue;

				String fileNamespace = filePath.split(":")[0];
				if ("minecraft".equals(fileNamespace)) continue;

				filePath = filePath.split(":")[1].replace(".png", "");

				String newFilePath = textureMappings.get(filePath);
				if (newFilePath == null) {
					var textureFile = new File(resourcePackDir, "assets/" + fileNamespace + "/textures/" + filePath + ".png");
					if (!textureFile.exists()) {
						ResourceLib.logger().warn("Font file {} requires texture file {}, but it wasn't found", fontFile.getAbsolutePath().split("ResourceLib/", 2)[1], filePath);
						continue;
					}
					newFilePath = fontsRemapDirName + "/" + getTextureRename(filePath); // Font textures may me non-divisible by 2, which breaks mipmaps, hence separate dir
					copyTexture(textureFile, new File(outputDir, "assets/minecraft/textures/" + newFilePath + ".png"));
				} else {
					newFilePath = fontsRemapDirName + "/" + newFilePath;
				}

				provider.addProperty("file", newFilePath + ".png");
			}
			FileUtil.createJsonFile(new File(outputDir, "assets/" + namespace + "/font/" + fontFile.getName()), json);
		}
	}

	private void copyTexture(@NotNull File from, @NotNull File to) {
		if (to.exists()) return;

		FileUtil.copyFile(from, to);

		File mcMetaFile = new File(from.getParentFile(), from.getName().replace(".png", ".mcmeta"));
		if (mcMetaFile.exists())
			FileUtil.copyFile(mcMetaFile, new File(to.getParentFile(), to.getName().replace(".png", ".mcmeta")));
	}

	private void copyMcAssets() {
		for (File file : Objects.requireNonNull(resourcePackDir.listFiles())) {
			if ("pack.mcmeta".equals(file.getName())) {
				FileUtil.createJsonFile(new File(outputDir, file.getName()), FileUtil.readJsonElement(file));
				continue;
			}
			if (!file.isDirectory() || file.listFiles() == null) {
				FileUtil.copyFile(file, new File(outputDir, file.getName()));
			}
		}
	}

	private @NotNull String getTextureRename(@NotNull String name) {
		return getRename(4, name, textureMappings);
	}

	private @NotNull String getModelRename(@NotNull String name) {
		return getRename(4, name, modelMappings);
	}

	private @NotNull String getRename(int size, @NotNull String name, @NotNull Map<String, String> referenceMap) {
		return referenceMap.computeIfAbsent(name, k -> getRename0(size, referenceMap));
	}

	private @NotNull String getRename0(int size, @NotNull Map<String, String> referenceMap) {
		var key = new StringBuilder();
		for (var i = 0; i < size; i++)
			key.append((char) (97 + random.nextInt(26)));
		var rename = key.toString();
		if (referenceMap.containsValue(rename))
			return getRename0(size, referenceMap);
		return rename;
	}

}
