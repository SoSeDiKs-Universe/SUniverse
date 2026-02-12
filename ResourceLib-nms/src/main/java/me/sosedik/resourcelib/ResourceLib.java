package me.sosedik.resourcelib;

import com.google.gson.JsonObject;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.resourcelib.api.item.FakeItemData;
import me.sosedik.resourcelib.command.CEffectCommand;
import me.sosedik.resourcelib.dataset.ResourcePackStorage;
import me.sosedik.resourcelib.feature.ScoreboardRenderer;
import me.sosedik.resourcelib.feature.TabRenderer;
import me.sosedik.resourcelib.impl.item.modifier.CustomLoreModifier;
import me.sosedik.resourcelib.impl.item.modifier.CustomNameModifier;
import me.sosedik.resourcelib.impl.item.modifier.ExtraItemComponentsModifier;
import me.sosedik.resourcelib.impl.message.tag.IconTag;
import me.sosedik.resourcelib.impl.message.tag.ItemTag;
import me.sosedik.resourcelib.impl.message.tag.SpaceTag;
import me.sosedik.resourcelib.listener.block.RefreshCustomBlockLightning;
import me.sosedik.resourcelib.listener.misc.ActionBarCatcher;
import me.sosedik.resourcelib.listener.misc.LocalizedDeathMessages;
import me.sosedik.resourcelib.listener.misc.LocalizedResourcePackMessage;
import me.sosedik.resourcelib.listener.misc.ResourcePackHoster;
import me.sosedik.resourcelib.listener.player.DisplayCustomPotionEffectsOnHud;
import me.sosedik.resourcelib.listener.player.LoadSaveHudMessengerOnJoinLeave;
import me.sosedik.resourcelib.listener.player.LoadSaveTabAndScoreboardRenderersOnJoinLeave;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ResourceLib extends JavaPlugin {

	private static @UnknownNullability ResourceLib instance;
	private static final Key BLOCKS_ATLAS = Key.key("blocks");
	private static final Key ITEMS_ATLAS = Key.key("items");
	private static final Set<Material> BLOCKS_AS_ITEMS = new HashSet<>();

	private @UnknownNullability Scheduler scheduler;
	private @UnknownNullability ResourcePackStorage storage;

	@Override
	public void onLoad() {
		ResourceLib.instance = this;
		ResourceLibBootstrap.runPostInitActions();
		this.scheduler = new Scheduler(this);
		this.storage = new ResourcePackStorage(this);

		TranslationHolder.extractLocales(this);

		ScoreboardRenderer.init(this);
		TabRenderer.init(this);
	}

	@Override
	public void onEnable() {
		registerCommands();

		addBlockAsItemTextureRef(
			Material.PITCHER_PLANT,
			Material.STRING,
			Material.FLOWER_POT
		);

		Mini.registerTagResolvers(
			IconTag.ICON,
			ItemTag.ITEM,
			SpaceTag.SPACE
		);

		new CustomLoreModifier(resourceLibKey("custom_lore")).register();
		new CustomNameModifier(resourceLibKey("custom_name")).register();
		new ExtraItemComponentsModifier(resourceLibKey("extra_components")).register();

		EventUtil.registerListeners(this,
			// block
			RefreshCustomBlockLightning.class,
			// misc
			ActionBarCatcher.class,
			LocalizedDeathMessages.class,
			// LocalizedDeathMessages.class,
			ResourcePackHoster.class,
			// player
			DisplayCustomPotionEffectsOnHud.class,
			LoadSaveHudMessengerOnJoinLeave.class,
			LoadSaveTabAndScoreboardRenderersOnJoinLeave.class
		);

		// RP message depends on FancyMotd Pinger's locale
		if (getServer().getPluginManager().isPluginEnabled("FancyMotd")) {
			EventUtil.registerListeners(this, LocalizedResourcePackMessage.class);
		}

		saveConfig();
	}

	private void registerCommands() {
		CommandManager.commandManager().registerCommands(this,
			CEffectCommand.class
		);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static ResourceLib instance() {
		return ResourceLib.instance;
	}

	/**
	 * Gets the plugin's task scheduler
	 *
	 * @return the plugin's task scheduler
	 */
	public static Scheduler scheduler() {
		return instance().scheduler;
	}

	/**
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static ComponentLogger logger() {
		return instance().getComponentLogger();
	}

	/**
	 * Gets the resource pack storage
	 *
	 * @return the resource pack storage
	 */
	public static ResourcePackStorage storage() {
		return instance().storage;
	}

	/**
	 * Makes a namespaced key with this plugin's namespace
	 *
	 * @param value value
	 * @return namespaced key
	 */
	public static NamespacedKey resourceLibKey(String value) {
		return new NamespacedKey("resourcelib", value);
	}

	/**
	 * Loads default plugin resources under /resources/dataset.
	 * <br />
	 * Should be called only during {@link Plugin#onLoad()}.
	 *
	 * @param plugin plugin instance
	 */
	public static void loadDefaultResources(Plugin plugin) {
		var jarFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		if (!jarFile.isFile()) {
			logger().error("Couldn't obtain the JAR file for {}", plugin.getName());
			logger().error("Last known location: {}", plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			return;
		}

		var datasetsDir = new File(plugin.getDataFolder(), "datasets");
		FileUtil.deleteFolder(datasetsDir);

		try (var jar = new JarFile(jarFile)) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory()) continue;

				String name = entry.getName();
				if (name.startsWith("datasets"))
					plugin.saveResource(name, true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			var dummyFile = new File(datasetsDir, "AUTO_GENERATED_FOLDER___DO_NOT_MODIFY");
			if (!dummyFile.createNewFile())
				logger().warn("Couldn't create dummy file");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		loadItemMappings(plugin);
	}
	
	private static void loadItemMappings(Plugin plugin) {
		var datasetsDir = new File(plugin.getDataFolder(), "datasets");
		for (File datasetDir : requireNonNull(datasetsDir.listFiles())) {
			if (!datasetDir.isDirectory()) continue;

			var itemsDir = new File(datasetDir, "item");
			if (itemsDir.exists() && itemsDir.isDirectory())
				loadItemMappings(itemsDir, datasetDir.getName());
			itemsDir = new File(datasetDir, "block");
			if (itemsDir.exists() && itemsDir.isDirectory())
				loadItemMappings(itemsDir, datasetDir.getName());
		}
	}

	private static void loadItemMappings(File itemsDir, String namespace) {
		for (File itemFile : requireNonNull(itemsDir.listFiles())) {
			if (itemFile.isDirectory()) {
				loadItemMappings(itemFile, namespace);
				continue;
			}
			if (!itemFile.getName().endsWith(".json")) continue;

			String key = itemFile.getName().substring(0, itemFile.getName().length() - ".json".length());
			var namespacedKey = new NamespacedKey(namespace, key);
			JsonObject itemData = FileUtil.readJsonObject(itemFile);
			var fakeItemData = new FakeItemData(
				requireNonNull(Material.matchMaterial(itemData.get("client_type").getAsString())),
				storage().getItemModelMapping(namespacedKey)
			);
			storage().addItemOption(namespacedKey, itemData, fakeItemData);
		}
	}

	/**
	 * Gets the font data by key
	 *
	 * @param key font key
	 * @return font data
	 */
	public static FontData requireFontData(NamespacedKey key) {
		FontData fontData = storage().getFontData(key);
		return requireNonNull(fontData, "Couldn't find font mapping: %s".formatted(key));
	}

	/**
	 * Gets the mapped sound key
	 *
	 * @param key sound key
	 * @return mapped sound key
	 */
	public static NamespacedKey getSound(NamespacedKey key) {
		return storage().getSoundMapping(key);
	}

	/**
	 * Gets the item icon
	 *
	 * @param itemKey item key
	 * @return item icon
	 */
	public static Component getItemIcon(Key itemKey) {
		Key textureKey = getTextureMapping(itemKey);
		textureKey = storage().getItemModelMapping(new NamespacedKey(textureKey.namespace(), textureKey.value()));

		ItemType itemType = Registry.ITEM.get(itemKey);
		boolean blocksAtlas = (itemType == null && Registry.BLOCK.get(itemKey) != null) || (itemType != null && itemType.hasBlockType() && !useItemTexture(itemType.asMaterial()));
		if (blocksAtlas && itemKey.value().endsWith("_bucket")) blocksAtlas = false;
		String value = (blocksAtlas ? "block/" : "item/") + textureKey.value();
		Key atlas = blocksAtlas ? BLOCKS_ATLAS : ITEMS_ATLAS;
		Key texture = Key.key(textureKey.namespace(), value);
		return Mini.asIcon(Component.object(ObjectContents.sprite(atlas, texture)).color(getItemIconColor(itemType)));
	}

	private static boolean useItemTexture(Material type) {
		return BLOCKS_AS_ITEMS.contains(type);
	}

	private static Key getTextureMapping(Key key) {
		return switch (key.namespace()) {
			case "trapped_newbie" -> switch (key.value()) {
				case "oak_chopping_block" -> Key.key("oak_log_top");
				case "oak_work_station" -> Key.key("trapped_newbie", "oak_crafting_grid");
				case "oak_totem_base" -> Key.key("stripped_oak_log");
				case "clay_kiln" -> Key.key("clay");
				default -> key;
			};
			default -> switch (key.value()) {
				case "compass" -> Key.key("compass_16");
				case "lilac" -> Key.key("lilac_top");
				case "peony" -> Key.key("peony_top");
				case "rose_bush" -> Key.key("rose_bush_top");
				case "sunflower" -> Key.key("sunflower_front");
				case "flowering_azalea" -> Key.key("flowering_azalea_top");
				case "campfire" -> Key.key("campfire_log_lit");
				case "soul_campfire" -> Key.key("soul_campfire_log_lit");
				case "grass_block" -> Key.key("grass_block_side");
				case "dirt_path" -> Key.key("dirt_path_side");
				case "water" -> Key.key("water_still");
				case "lava" -> Key.key("lava_still");
				case "white_carpet" -> Key.key("white_wool");
				default -> key;
			};
		};
	}

	public static void addBlockAsItemTextureRef(Material... types) {
		addBlockAsItemTextureRef(List.of(types));
	}

	public static void addBlockAsItemTextureRef(Collection<Material> types) {
		BLOCKS_AS_ITEMS.addAll(types);
	}

	private static TextColor getItemIconColor(@Nullable ItemType itemType) {
		if (itemType == null) return NamedTextColor.WHITE;

		// Grass and some leaves use biome color and are gray by default
		if (itemType == ItemType.SHORT_GRASS
		|| itemType == ItemType.TALL_GRASS
		|| itemType == ItemType.FERN
		|| itemType == ItemType.LARGE_FERN
		|| itemType == ItemType.BUSH
		|| itemType == ItemType.OAK_LEAVES
		)
			return TextColor.fromHexString("#618549");

		return NamedTextColor.WHITE;
	}

}
