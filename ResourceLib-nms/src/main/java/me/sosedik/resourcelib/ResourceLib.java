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
import me.sosedik.resourcelib.impl.message.tag.IconTag;
import me.sosedik.resourcelib.listener.block.RefreshCustomBlockLightning;
import me.sosedik.resourcelib.listener.misc.ActionBarCatcher;
import me.sosedik.resourcelib.listener.misc.LocalizedDeathMessages;
import me.sosedik.resourcelib.listener.misc.LocalizedResourcePackMessage;
import me.sosedik.resourcelib.listener.player.DisplayCustomPotionEffectsOnHud;
import me.sosedik.resourcelib.listener.player.LoadSaveHudMessengerOnJoinLeave;
import me.sosedik.resourcelib.listener.player.LoadSaveTabAndScoreboardRenderersOnJoinLeave;
import me.sosedik.resourcelib.util.ResourcePackHoster;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ResourceLib extends JavaPlugin {

	private static @UnknownNullability ResourceLib instance;

	private @UnknownNullability Scheduler scheduler;
	private @UnknownNullability ResourcePackStorage storage;

	@Override
	public void onLoad() {
		ResourceLib.instance = this;
		ResourceLibBootstrap.runPostInitActions();
		this.scheduler = new Scheduler(this);
		this.storage = new ResourcePackStorage(this);

		TranslationHolder.extractLocales(this);
		ResourcePackHoster.hostResourcePack(this);

		ScoreboardRenderer.init(this);
		TabRenderer.init(this);
	}

	@Override
	public void onEnable() {
		registerCommands();

		Mini.registerTagResolvers(
			IconTag.ICON
		);

		new CustomNameModifier(resourceLibKey("custom_name")).register();
		new CustomLoreModifier(resourceLibKey("custom_lore")).register();

		EventUtil.registerListeners(this,
			// block
			RefreshCustomBlockLightning.class,
			// misc
			ActionBarCatcher.class,
			LocalizedDeathMessages.class,
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
	 * @param key item key
	 * @return item icon
	 */
	public static Component getItemIcon(Key key) {
		ItemType itemType = Registry.ITEM.get(key);
		if (itemType == null) return Component.space();

		FakeItemData fakeItemData = storage().getFakeItemData(new NamespacedKey(key.namespace(), key.value()));
		if (fakeItemData != null && fakeItemData.model() != null)
			key = Key.key(fakeItemData.model().namespace(), fakeItemData.model().value());

		boolean blocksAtlas = itemType.hasBlockType() && !useItemTexture(itemType.asMaterial());
		Key texture = Key.key(key.namespace(), (blocksAtlas ? "block/" : "item/") + getReplacement(key.value()));
		return Mini.asIcon(Component.object(ObjectContents.sprite(texture)));
	}

	private static boolean useItemTexture(Material type) {
		return type == Material.PITCHER_PLANT;
	}

	private static String getReplacement(String key) {
		return switch (key) {
			case "lilac" -> "lilac_top";
			case "peony" -> "peony_top";
			case "rose_bush" -> "rose_bush_top";
			case "sunflower" -> "sunflower_front";
			case "flowering_azalea" -> "flowering_azalea_top";
			default -> key;
		};
	}

}
