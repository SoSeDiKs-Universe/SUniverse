package me.sosedik.resourcelib;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import me.sosedik.kiterino.registry.data.BlockRegistryEntity;
import me.sosedik.kiterino.registry.data.ItemRegistryEntity;
import me.sosedik.kiterino.registry.data.MobEffectRegistryEntity;
import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import me.sosedik.kiterino.util.KiterinoUnsafeUtil;
import me.sosedik.kiterino.world.block.KiterinoBlock;
import me.sosedik.utilizer.util.FileUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
import java.util.ArrayList;
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

	private static List<Runnable> postActions = new ArrayList<>();

	@Override
	public void bootstrap(BootstrapContext context) {
		var datapacksDir = new File(context.getDataDirectory().toAbsolutePath().getParent().getParent().toFile(), "world/datapacks");
		if (datapacksDir.exists()) {
			for (File datapackDir : requireNonNull(datapacksDir.listFiles())) {
				if (!datapackDir.isDirectory()) continue;
				if (datapackDir.getName().charAt(0) != '_') continue;

				FileUtil.deleteFolder(datapackDir);
			}
		}
		parseResources(context, null);
	}

	public static void setupItems(BootstrapContext context, Class<?> materialsClass, @Nullable BiConsumer<String, ItemRegistryEntity.Builder> modifier, @Nullable BiFunction<String, Object, @Nullable Object> itemsProvider) {
		goThroughDatasets(context, "item", (namespace, jsonEntries) -> {
			context.getLifecycleManager().registerEventHandler(RegistryEvents.ITEM.compose(),
				event -> jsonEntries.forEach(
					entry -> readItem(context, event, modifier, Key.key(namespace, entry.getKey()), entry.getValue().getAsJsonObject(), itemsProvider)
				)
			);
		});
		goThroughDatasets(context, "block", (namespace, jsonEntries) -> {
			context.getLifecycleManager().registerEventHandler(RegistryEvents.ITEM.compose(),
				event -> jsonEntries.forEach(
					entry -> readItem(context, event, modifier, Key.key(namespace, entry.getKey()), entry.getValue().getAsJsonObject(), itemsProvider == null ? null : (key, props) -> {
						Object blockItem = itemsProvider.apply(key, props);
						if (blockItem != null && !(blockItem instanceof BlockItem)) context.getLogger().error("Block item is not an instance of BlockItem: {}", key);
						return blockItem;
					})
				)
			);
		});
		context.getLifecycleManager().registerEventHandler(RegistryEvents.ITEM.compose(), event -> context.injectMaterials(materialsClass));
	}

	public static void setupBlocks(BootstrapContext context, @Nullable BiConsumer<String, BlockRegistryEntity.Builder> modifier, BiFunction<String, Object, KiterinoBlock> blocksProvider) {
		goThroughDatasets(context, "block", (namespace, jsonEntries) -> {
			context.getLifecycleManager().registerEventHandler(RegistryEvents.BLOCK.compose(),
				event -> jsonEntries.forEach(
					entry -> readBlock(context, event, modifier, Key.key(namespace, entry.getKey()), entry.getValue().getAsJsonObject(), blocksProvider)
				)
			);
		});
		goThroughDatasets(context, "blockstate", (namespace, jsonEntries) -> {
			context.getLifecycleManager().registerEventHandler(RegistryEvents.BLOCK.compose(),
				event -> jsonEntries.forEach(
					entry -> readBlock(context, event, modifier, Key.key(namespace, entry.getKey()), entry.getValue().getAsJsonObject(), blocksProvider)
				)
			);
		});
	}

	private static void readBlock(BootstrapContext context, RegistryComposeEvent<BlockType, BlockRegistryEntity.Builder> event, @Nullable BiConsumer<String, BlockRegistryEntity.Builder> modifier, Key blockKey, JsonObject json, BiFunction<String, Object, KiterinoBlock> blocksProvider) {
		var typedKey = TypedKey.create(RegistryKey.BLOCK, blockKey);
		event.registry().register(typedKey, b -> {
			try {
				b.nmsBlock(blocksProvider.apply(blockKey.toString(), applyBlockProperties(json, b.constructBlockProperties())));
			} catch (Exception e) {
				context.getLogger().error("Couldn't inject custom block", e);
			}

			if (modifier != null) modifier.accept(blockKey.value(), b);
		});
	}

	private static BlockBehaviour.Properties applyBlockProperties(JsonObject json, Object props) throws Exception {
		BlockBehaviour.Properties properties = (BlockBehaviour.Properties) props;
		if (json.has("destroy_time")) properties.destroyTime(json.get("destroy_time").getAsFloat());
		if (json.has("explosion_resistance")) properties.explosionResistance(json.get("explosion_resistance").getAsFloat());
		if (json.has("light_level")) {
			int lightLevel = json.get("light_level").getAsInt();
			properties.lightLevel(state -> lightLevel);
		}
		if (json.has("ignited_by_lava") && json.get("ignited_by_lava").getAsBoolean()) properties.ignitedByLava();
		if (json.has("no_collision") && json.get("no_collision").getAsBoolean()) properties.noCollision();
		if (json.has("replaceable") && json.get("replaceable").getAsBoolean()) properties.replaceable();
		if (json.has("require_correct_tool") && json.get("require_correct_tool").getAsBoolean()) properties.requiresCorrectToolForDrops();
		if (json.has("sound_type")) properties.sound((SoundType) SoundType.class.getDeclaredField(json.get("sound_type").getAsString().toUpperCase(Locale.US)).get(null));
		if (json.has("note_block_instrument")) properties.instrument(NoteBlockInstrument.valueOf(json.get("note_block_instrument").getAsString().toUpperCase(Locale.US)));
		if (json.has("map_color")) properties.mapColor((MapColor) MapColor.class.getDeclaredField(json.get("map_color").getAsString().toUpperCase(Locale.US)).get(null));
		return properties;
	}

	private static void readItem(BootstrapContext context, RegistryComposeEvent<ItemType, ItemRegistryEntity.Builder> event, @Nullable BiConsumer<String, ItemRegistryEntity.Builder> modifier, Key itemKey, JsonObject json, @Nullable BiFunction<String, Object, @Nullable Object> itemsProvider) {
		var typedKey = TypedKey.create(RegistryKey.ITEM, itemKey);
		event.registry().register(typedKey, b -> {
			Object nmsItem = null;
			Item.Properties properties = null;
			if (itemsProvider != null) {
				properties = applyItemProperties(context, itemKey, json, b.constructItemProperties());
				nmsItem = itemsProvider.apply(itemKey.toString(), properties);
			}
			if (nmsItem == null) {
				if (properties == null) properties = applyItemProperties(context, itemKey, json, b.constructItemProperties());
				if (b.hasAttachedBlock()) {
					b.nmsItem(new BlockItem((Block) b.asBlockOrThrow(), properties));
				} else {
					b.nmsItem(new Item(properties));
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
						if (model != null && !box.getItem().isDataOverridden(DataComponentTypes.ITEM_MODEL))
							box.getItem().setData(DataComponentTypes.ITEM_MODEL, model);
					}
				});
			} else {
				context.getLogger().warn("Missing client type for item {}", itemKey);
			}

			if (modifier != null) modifier.accept(itemKey.toString(), b);
		});
	}

	private static Reference2ObjectMap<DataComponentType<?>, Object> getComponentsMap(Item item) {
		if (!(item.components() instanceof DataComponentMap.Builder.SimpleMap(
			Reference2ObjectMap<DataComponentType<?>, Object> map
		)))
			throw new RuntimeException("Couldn't get Item's components map");
		return map;
	}

	private static Item.Properties applyItemProperties(BootstrapContext context, Key itemKey, JsonObject json, Object props) {
		Item.Properties properties = (Item.Properties) props;
		if (json.has("durability")) properties.durability(json.get("durability").getAsInt());
		if (json.has("repairable")) {
			String tag = json.get("repairable").getAsString();
			if (tag.charAt(0) == '#') {
				properties.repairable(TagKey.create(Registries.ITEM, ResourceLocation.parse(tag.substring(1))));
			} else {
				Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(tag));
				if (item == Items.AIR) {
					postActions.add(() -> {
						Item repairItem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(tag));
						if (repairItem == Items.AIR) {
							ResourceLib.logger().error("Couldn't set repairable ({} -> {}): missing item", itemKey, tag);
							return;
						}
						Item ogItem = BuiltInRegistries.ITEM.getValue(PaperAdventure.asVanilla(itemKey));
						getComponentsMap(ogItem).put(DataComponents.REPAIRABLE, new Repairable(HolderSet.direct(repairItem.builtInRegistryHolder())));
					});
				} else {
					properties.repairable(item);
				}
			}
		}
		if (json.has("remaining_item")) {
			String itemId = json.get("remaining_item").getAsString();
			Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemId));
			if (item == Items.AIR) {
				postActions.add(() -> {
					Item remainingItem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemId));
					if (remainingItem == Items.AIR) {
						ResourceLib.logger().error("Couldn't set remaining_item ({} -> {}): missing item", itemKey, itemId);
						return;
					}
					Item ogItem = BuiltInRegistries.ITEM.getValue(PaperAdventure.asVanilla(itemKey));
					try {
						KiterinoUnsafeUtil.getField(Item.class, "craftingRemainingItem").set(ogItem, remainingItem);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				});
			} else {
				properties.craftRemainder(item);
			}
		}
		if (json.has("using_converts_to")) {
			String itemId = json.get("using_converts_to").getAsString();
			Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemId));
			if (item == Items.AIR) {
				postActions.add(() -> {
					Item usingConvertsTo = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemId));
					if (usingConvertsTo == Items.AIR) {
						ResourceLib.logger().error("Couldn't set using_converts_to ({} -> {}): missing item", itemKey, itemId);
						return;
					}
					Item ogItem = BuiltInRegistries.ITEM.getValue(PaperAdventure.asVanilla(itemKey));
					getComponentsMap(ogItem).put(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStack(usingConvertsTo)));
				});
			} else {
				properties.usingConvertsTo(item);
			}
		}
		if (json.has("stack_size")) properties.stacksTo(json.get("stack_size").getAsInt());
		if (json.has("fire_resistance") && json.get("fire_resistance").getAsBoolean()) properties.fireResistant();
		if (json.has("rarity")) properties.rarity(Rarity.valueOf(json.get("rarity").getAsString().toUpperCase(Locale.ROOT)));
		if (json.has("dyeable")) properties.component(DataComponents.DYED_COLOR, new DyedItemColor(json.get("dyeable").getAsInt()));

		if (json.has("attributes")) {
			JsonObject attributesJson = json.getAsJsonObject("attributes");
			ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
			if (attributesJson.has("attack_damage")) {
				builder.add(
					Attributes.ATTACK_DAMAGE,
					new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attributesJson.get("attack_damage").getAsDouble(), AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND
				);
			}
			if (attributesJson.has("attack_speed")) {
				builder.add(
					Attributes.ATTACK_SPEED,
					new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attributesJson.get("attack_speed").getAsDouble(), AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND
				);
			}
			properties.attributes(builder.build());
		}

		if (json.has("food")) {
			JsonObject foodJson = json.getAsJsonObject("food");
			var foodProperties = new FoodProperties.Builder()
				.nutrition(foodJson.get("nutrition").getAsInt())
				.saturationModifier(foodJson.get("saturation").getAsFloat());
			if (foodJson.has("always_edible") && foodJson.get("always_edible").getAsBoolean())
				foodProperties.alwaysEdible();

			if (json.has("consumable")) {
				properties.food(foodProperties.build(), getConsumable(json.getAsJsonObject("consumable")));
			} else {
				properties.food(foodProperties.build());
			}
		} else if (json.has("consumable")) {
			properties.component(DataComponents.CONSUMABLE, getConsumable(json.getAsJsonObject("consumable")));
		}

		return properties;
	}

	private static Consumable getConsumable(JsonObject consumableJson) {
		if (consumableJson.has("type")) {
			String type = consumableJson.get("type").getAsString();
			if ("drink".equals(type)) return Consumables.DEFAULT_DRINK;
		}

		var consumable = Consumable.builder();

		if (consumableJson.has("consume_seconds"))
			consumable.consumeSeconds(consumableJson.get("consume_seconds").getAsFloat());

		if (consumableJson.has("effects")) {
			for (JsonElement effectElement : consumableJson.getAsJsonArray("effects")) {
				JsonObject effectJson = effectElement.getAsJsonObject();
				String type = effectJson.get("type").getAsString();
				if ("apply_status_effects".equals(type)) {
					float probability = effectJson.has("probability") ? effectJson.get("probability").getAsFloat() : 1F;
					List<PotionEffect> effectList = effectJson.getAsJsonArray("effects").asList().stream().map(e -> {
						String effectKey = e.getAsJsonObject().get("type").getAsString();
						int duration = e.getAsJsonObject().get("duration").getAsInt();
						int amplifier = e.getAsJsonObject().get("amplifier").getAsInt();
						PotionEffectType potionEffectType = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT).get(Key.key(effectKey));
						return new PotionEffect(potionEffectType, duration, amplifier);
					}).toList();
					consumable.onConsume(new ApplyStatusEffectsConsumeEffect(
							new ArrayList<>(Lists.transform(effectList, CraftPotionUtil::fromBukkit)),
							probability
					));
				}
			}
		}

		return consumable.build();
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

		if (effectsProvider != null)
			registerMobEffects(context, effectsProvider);
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

	private static void registerMobEffects(BootstrapContext context, Function<String, KiterinoMobEffectBehaviourWrapper> effectsProvider) {
		goThroughDatasets(context, "effect", (namespace, jsonEntries) -> {
			context.getLifecycleManager().registerEventHandler(RegistryEvents.MOB_EFFECT.compose(),
				event -> jsonEntries.forEach(
					entry -> readEffect(event, Key.key(namespace, entry.getKey()), entry.getValue().getAsJsonObject(), effectsProvider)
				)
			);
		});
	}

	private static void readEffect(RegistryComposeEvent<PotionEffectType, MobEffectRegistryEntity.Builder> event, Key key, JsonObject options, Function<String, KiterinoMobEffectBehaviourWrapper> provider) {
		var category = PotionEffectType.Category.valueOf(options.get("category").getAsString().toUpperCase(Locale.US));
		int color;
		try {
			color = NamedTextColor.NAMES.valueOrThrow(options.get("color").getAsString()).value();
		} catch (NoSuchElementException ignored) {
			color = requireNonNull(TextColor.fromHexString(options.get("color").getAsString())).value();
		}
		int finalColor = color;
		event.registry().register(potionEffectKey(key), b -> b
			.category(category)
			.color(finalColor)
			.wrapper(provider.apply(key.namespace() + ":" + key.value()))
		);
	}

	private static TypedKey<PotionEffectType> potionEffectKey(Key key) {
		return TypedKey.create(RegistryKey.MOB_EFFECT, key);
	}

	public static void runPostInitActions() {
		postActions.forEach(Runnable::run);
		postActions = null;
	}

}
