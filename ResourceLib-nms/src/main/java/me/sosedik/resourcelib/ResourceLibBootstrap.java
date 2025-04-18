package me.sosedik.resourcelib;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import me.sosedik.kiterino.registry.data.BlockRegistryEntity;
import me.sosedik.kiterino.registry.data.ItemRegistryEntity;
import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.utilizer.util.FileUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffectType;
import org.intellij.lang.annotations.Subst;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ResourceLibBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		parseResources(context, null);
	}

	public static void setupItems(BootstrapContext context, Class<?> materialsClass, @Nullable BiConsumer<String, ItemRegistryEntity.Builder> modifier, @Nullable BiFunction<String, Object, @Nullable Object> itemsProvider) {
		goThroughDatasets(context, "item", (namespace, jsonEntries) -> {
			context.getLifecycleManager().registerEventHandler(RegistryEvents.ITEM.freeze(),
				event -> jsonEntries.forEach(
					entry -> readItem(event, modifier, Key.key(namespace, entry.getKey()), entry.getValue().getAsJsonObject(), itemsProvider)
				)
			);
		});
		context.getLifecycleManager().registerEventHandler(RegistryEvents.ITEM.freeze(), event -> context.injectMaterials(materialsClass));
	}

	public static void setupBlocks(BootstrapContext context, @Nullable BiConsumer<String, BlockRegistryEntity.Builder> modifier, BiFunction<String, Object, KiterinoBlock> blocksProvider) {
		goThroughDatasets(context, "blockstate", (namespace, jsonEntries) -> {
			context.getLifecycleManager().registerEventHandler(RegistryEvents.BLOCK.freeze(),
				event -> jsonEntries.forEach(
					entry -> readBlock(event, modifier, Key.key(namespace, entry.getKey()), entry.getValue().getAsJsonObject(), blocksProvider)
				)
			);
		});
	}

	private static void readBlock(RegistryFreezeEvent<BlockType, BlockRegistryEntity.Builder> event, @Nullable BiConsumer<String, BlockRegistryEntity.Builder> modifier, Key blockKey, JsonObject json, BiFunction<String, Object, KiterinoBlock> blocksProvider) {
		var typedKey = TypedKey.create(RegistryKey.BLOCK, blockKey);
		event.registry().register(typedKey, b -> {
			b.nmsBlock(blocksProvider.apply(blockKey.toString(), applyBlockProperties(json, b.constructBlockProperties())));

			if (modifier != null) modifier.accept(blockKey.value(), b);
		});
	}

	private static BlockBehaviour.Properties applyBlockProperties(JsonObject json, Object props) {
		BlockBehaviour.Properties properties = (BlockBehaviour.Properties) props;
		if (json.has("explosion_resistance")) properties.explosionResistance(json.get("explosion_resistance").getAsFloat());
		return properties;
	}

	private static void readItem(RegistryFreezeEvent<ItemType, ItemRegistryEntity.Builder> event, @Nullable BiConsumer<String, ItemRegistryEntity.Builder> modifier, Key itemKey, JsonObject json, @Nullable BiFunction<String, Object, @Nullable Object> itemsProvider) {
		var typedKey = TypedKey.create(RegistryKey.ITEM, itemKey);
		event.registry().register(typedKey, b -> {
			Object nmsItem = null;
			if (itemsProvider != null) {
				nmsItem = itemsProvider.apply(itemKey.toString(), applyItemProperties(json, b.constructItemProperties()));
			}
			if (nmsItem == null) {
				if (b.hasAttachedBlock()) {
					b.nmsItem(new BlockItem((Block) b.asBlockOrThrow(), applyItemProperties(json, b.constructItemProperties())));
				} else {
					b.nmsItem(new Item(applyItemProperties(json, b.constructItemProperties())));
				}
			} else {
				b.nmsItem(nmsItem);
			}

			if (json.has("compost_chance")) b.compostChance(json.get("compost_chance").getAsFloat());

			if (json.has("client_type")) {
				Material type = Material.matchMaterial(json.get("client_type").getAsString());
				assert type != null;
				var namespacedKey = new NamespacedKey(itemKey.namespace(), itemKey.value());
				b.modifier(box -> {
					box.setType(type);
					var data = ResourceLib.storage().getFakeItemData(namespacedKey);
					if (data != null) {
						NamespacedKey model = data.model();
						if (model != null) box.getItem().setData(DataComponentTypes.ITEM_MODEL, model);
					}
				});
			}

			if (modifier != null) modifier.accept(itemKey.toString(), b);
		});
	}

	private static Item.Properties applyItemProperties(JsonObject json, Object props) {
		Item.Properties properties = (Item.Properties) props;
		if (json.has("durability")) properties.durability(json.get("durability").getAsInt());
		if (json.has("stack_size")) properties.stacksTo(json.get("stack_size").getAsInt());
		if (json.has("fire_resistance") && json.get("fire_resistance").getAsBoolean()) properties.fireResistant();
		if (json.has("rarity")) properties.rarity(Rarity.valueOf(json.get("rarity").getAsString().toUpperCase(Locale.ROOT)));

		if (json.has("food")) {
			JsonObject foodJson = json.getAsJsonObject("food");
			var foodProperties = new FoodProperties.Builder()
				.nutrition(foodJson.get("nutrition").getAsInt())
				.saturationModifier(foodJson.get("saturation").getAsFloat());
			if (foodJson.has("always_edible") && foodJson.get("always_edible").getAsBoolean())
				foodProperties.alwaysEdible();

			if (foodJson.has("consumable")) {
				JsonObject consumableJson = foodJson.getAsJsonObject("consumable");
				// TODO consumable
				properties.food(foodProperties.build());
			} else {
				properties.food(foodProperties.build());
			}
		}

		return properties;
	}

	private static void goThroughDatasets(BootstrapContext context, String subdir, BiConsumer<String, List<Map.Entry<String, JsonElement>>> consumer) {
		Path jarPath = context.getPluginSource();
		try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + jarPath.toUri()), new HashMap<>())) {
			Path datasetsPath = fs.getPath("/datasets");
			if (!Files.isDirectory(datasetsPath)) {
				context.getLogger().warn("Couldn't find datasets directory inside {}", context.getPluginMeta().getName());
				return;
			}

			try (Stream<Path> dirs = Files.list(datasetsPath)) {
				dirs.filter(Files::isDirectory)
					.forEach(dir -> {
						Path itemDir = dir.resolve(subdir);
						if (!Files.isDirectory(itemDir)) return;

						String namespace = dir.getFileName().toString();
						try (Stream<Path> itemContents = Files.walk(itemDir)) {
							List<Map.Entry<String, JsonElement>> jsonEntires = itemContents.filter(Files::isRegularFile)
								.filter(path -> path.toString().endsWith(".json"))
								.map(ResourceLibBootstrap::readJson)
								.toList();
							consumer.accept(namespace, jsonEntires);
						} catch (IOException e) {
							context.getLogger().error("Error reading 'item' contents in {}", itemDir, e);
						}
					});
			} catch (IOException e) {
				context.getLogger().error("Error accessing datasets directory", e);
			}

		} catch (IOException e) {
			context.getLogger().error("Couldn't read jar file", e);
		}
	}

	private static Map.Entry<String, JsonElement> readJson(Path path) {
		try (Reader reader = Files.newBufferedReader(path)) {
			return Map.entry(path.getFileName().toString().replace(".json", ""), JsonParser.parseReader(reader));
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read jar file", e);
		}
	}

	public static void parseResources(BootstrapContext context, @Nullable Function<String, KiterinoMobEffectBehaviourWrapper> effectsProvider) {
		extractDatapack(context);

		File datasetsDir = new File(context.getDataDirectory().toFile(), "datasets");
		if (!datasetsDir.exists()) return;
		if (!datasetsDir.isDirectory()) return;

		for (File namespaceDir : requireNonNull(datasetsDir.listFiles())) {
			if (!namespaceDir.isDirectory()) continue;

			var effectsDir = new File(namespaceDir, "effect");
			if (effectsProvider != null && effectsDir.exists() && effectsDir.isDirectory()) {
				registerMobEffects(context, effectsDir, namespaceDir.getName(), effectsProvider);
			}
		}
	}

	// Yes, ugly; better way?
	private static void extractDatapack(BootstrapContext context) {
		File jarFile = context.getPluginSource().toFile();
		if (!jarFile.isFile()) {
			context.getLogger().error("Couldn't obtain the JAR file for {}", context.getPluginMeta().getName());
			context.getLogger().error("Last known location: {}", context.getPluginSource());
			return;
		}

		var datapacksDir = new File(context.getDataDirectory().toAbsolutePath().getParent().getParent().toFile(), "world/datapacks");
		if (datapacksDir.exists()) {
			for (File datapackDir : requireNonNull(datapacksDir.listFiles())) {
				if (!datapacksDir.isDirectory()) continue;
				if (datapacksDir.getName().charAt(0) != '_') continue;

				FileUtil.deleteFolder(datapackDir);
			}
		}
		try (var jar = new JarFile(jarFile)) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory()) continue;

				String name = entry.getName();
				if (!name.startsWith("datapack/")) continue;

				File outputFile = new File(datapacksDir, "_" + name.substring("datapack/".length()));
				FileUtil.createFolder(outputFile.getParentFile());
				try (InputStream inputStream = jar.getInputStream(entry);
				     FileOutputStream outputStream = new FileOutputStream(outputFile)) {

					byte[] buffer = new byte[1024];
					int bytesRead;
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void registerMobEffects(BootstrapContext context, File effectsDir, String namespace, Function<String, KiterinoMobEffectBehaviourWrapper> provider) {
		context.getLifecycleManager().registerEventHandler(RegistryEvents.MOB_EFFECT.freeze(), event -> {
			for (File effectFile : requireNonNull(effectsDir.listFiles())) {
				if (!effectFile.getName().endsWith(".json")) continue;

				JsonObject options = FileUtil.readJsonObject(effectFile);
				String effectKey = effectFile.getName().substring(0, effectFile.getName().length() - ".json".length());
				var category = PotionEffectType.Category.valueOf(options.get("category").getAsString().toUpperCase(Locale.ROOT));
				int color;
				try {
					color = NamedTextColor.NAMES.valueOrThrow(options.get("color").getAsString()).value();
				} catch (NoSuchElementException ignored) {
					color = requireNonNull(TextColor.fromHexString(options.get("color").getAsString())).value();
				}
				int finalColor = color;
				event.registry().register(potionEffectKey(namespace, effectKey), b -> b
					.category(category)
					.color(finalColor)
					.wrapper(provider.apply(namespace + ":" + effectKey))
				);
			}
		});
	}

	private static TypedKey<PotionEffectType> potionEffectKey(@Subst("key") String namespace, @Subst("key") String key) {
		return TypedKey.create(RegistryKey.MOB_EFFECT, new NamespacedKey(namespace, key));
	}

}
