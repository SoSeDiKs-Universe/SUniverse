package me.sosedik.resourcelib;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
import me.sosedik.kiterino.registry.data.ItemRegistryEntity;
import net.kyori.adventure.key.Key;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class ResourceLibBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(@NotNull BootstrapContext context) {
		// Init
	}

	public static void setupItems(@NotNull BootstrapContext context, @NotNull Class<?> materialsClass) {
		setupItems(context, materialsClass, null);
	}

	public static void setupItems(@NotNull BootstrapContext context, @NotNull Class<?> materialsClass, @Nullable BiConsumer<String, ItemRegistryEntity.Builder> modifier) {
		goThroughDataset(context, "item", (namespace, itemDir) -> {
			List<Map.Entry<String, JsonElement>> jsons = readJsons(itemDir);
			context.getLifecycleManager().registerEventHandler(RegistryEvents.ITEM.freeze(), event -> {
				jsons.forEach(entry -> {
					readItem(event, modifier, Key.key(namespace, entry.getKey()), entry.getValue().getAsJsonObject());
				});

				context.injectMaterials(materialsClass);
			});
		});
	}

	private static void readItem(@NotNull RegistryFreezeEvent<ItemType, ItemRegistryEntity.Builder> event, @Nullable BiConsumer<String, ItemRegistryEntity.Builder> modifier, @NotNull Key itemKey, @NotNull JsonObject json) {
		var typedKey = TypedKey.create(RegistryKey.ITEM, itemKey);
		event.registry().register(typedKey, b -> {
			b.nmsItemFunction(properties -> applyItemProperties(json, properties));
			if (json.has("compost_chance")) b.compostChance(json.get("compost_chance").getAsFloat());

			if (json.has("client_type")) {
				Material type = Material.matchMaterial(json.get("client_type").getAsString());
				assert type != null;
				var namespacedKey = new NamespacedKey(itemKey.namespace(), itemKey.value());
				b.modifier(box -> {
					box.setType(type);
					var data = ResourceLib.storage().getFakeItemData(namespacedKey);
					if (data != null) box.getItem().setCustomModelData(data.customModelData());
				});
			}

			if (modifier != null) modifier.accept(itemKey.value(), b);
		});
	}

	private static @NotNull Object applyItemProperties(@NotNull JsonObject json, @NotNull Object props) {
		Item.Properties properties = (Item.Properties) props;
		if (json.has("durability")) properties.durability(json.get("durability").getAsInt());
		if (json.has("stack_size")) properties.stacksTo(json.get("stack_size").getAsInt());
		if (json.has("fire_resistance") && json.get("fire_resistance").getAsBoolean()) properties.fireResistant();
		if (json.has("rarity")) properties.rarity(Rarity.valueOf(json.get("rarity").getAsString().toUpperCase(Locale.ROOT)));
		return new Item(properties);
	}

	private static @NotNull List<Map.Entry<String, JsonElement>> readJsons(@NotNull Path path) {
		try (Stream<Path> jsonFiles = Files.list(path)) {
			return jsonFiles.filter(file -> file.toString().endsWith(".json")).map(file -> {
				try (Reader reader = Files.newBufferedReader(file)) {
					return Map.entry(file.getFileName().toString().replace(".json", ""), JsonParser.parseReader(reader));
				} catch (IOException e) {
					throw new RuntimeException("Couldn't read jar file", e);
				}
			}).toList();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read jar file", e);
		}
	}

	private static void goThroughDataset(@NotNull BootstrapContext context, @NotNull String subdir, @NotNull BiConsumer<String, Path> consumer) {
		Path jarPath = context.getPluginSource();
		try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + jarPath.toUri()), new HashMap<>())) {
			Path datasetsPath = fs.getPath("/datasets");
			if (!Files.exists(datasetsPath) || !Files.isDirectory(datasetsPath)) {
				context.getLogger().warn("Couldn't find datasets directory inside {}", context.getPluginMeta().getName());
				return;
			}

			try (Stream<Path> dirs = Files.list(datasetsPath)) {
				dirs.filter(Files::isDirectory)
					.forEach(dir -> {
						Path subDir = dir.resolve("item");
						if (!Files.exists(subDir) || !Files.isDirectory(subDir)) {
							context.getLogger().warn("Couldn't find {} directory inside {}", subdir, context.getPluginMeta().getName());
							return;
						}
						consumer.accept(dir.getFileName().toString(), subDir);
					});
			}
		} catch (IOException e) {
			context.getLogger().error("Couldn't read jar file", e);
		}
	}

}
