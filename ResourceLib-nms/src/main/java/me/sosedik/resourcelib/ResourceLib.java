package me.sosedik.resourcelib;

import com.google.gson.JsonObject;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.resourcelib.api.item.FakeItemData;
import me.sosedik.resourcelib.command.CEffectCommand;
import me.sosedik.resourcelib.dataset.ResourcePackStorage;
import me.sosedik.resourcelib.feature.TabRenderer;
import me.sosedik.resourcelib.impl.item.modifier.CustomLoreModifier;
import me.sosedik.resourcelib.impl.item.modifier.CustomNameModifier;
import me.sosedik.resourcelib.listener.misc.LocalizedDeathMessages;
import me.sosedik.resourcelib.listener.misc.LocalizedResourcePackMessage;
import me.sosedik.resourcelib.listener.player.DisplayCustomPotionEffectsOnHud;
import me.sosedik.resourcelib.listener.player.LoadSaveHudMessengerOnJoinLeave;
import me.sosedik.resourcelib.listener.player.LoadSaveTabRendererOnJoinLeave;
import me.sosedik.resourcelib.util.ResourcePackHoster;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.Objects.requireNonNull;

public class ResourceLib extends JavaPlugin {

	private static ResourceLib instance;

	private Scheduler scheduler;
	private ResourcePackStorage storage;

	@Override
	public void onLoad() {
		ResourceLib.instance = this;
		this.scheduler = new Scheduler(this);
		this.storage = new ResourcePackStorage(this);

		TranslationHolder.extractLocales(this);
		ResourcePackHoster.hostResourcePack(this);

		TabRenderer.init(this);
	}

	@Override
	public void onEnable() {
		registerCommands();

		new CustomNameModifier(resourceLibKey("custom_name")).register();
		new CustomLoreModifier(resourceLibKey("custom_lore")).register();

		EventUtil.registerListeners(this,
			// misc
			LocalizedDeathMessages.class,
			// player
			DisplayCustomPotionEffectsOnHud.class,
			LoadSaveHudMessengerOnJoinLeave.class,
			LoadSaveTabRendererOnJoinLeave.class
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
	public static @NotNull ResourceLib instance() {
		return ResourceLib.instance;
	}

	/**
	 * Gets the plugin's task scheduler
	 *
	 * @return the plugin's task scheduler
	 */
	public static @NotNull Scheduler scheduler() {
		return instance().scheduler;
	}

	/**
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static @NotNull ComponentLogger logger() {
		return instance().getComponentLogger();
	}

	/**
	 * Gets the resource pack storage
	 *
	 * @return the resource pack storage
	 */
	public static @NotNull ResourcePackStorage storage() {
		return instance().storage;
	}

	/**
	 * Makes a namespaced key with this plugin's namespace
	 *
	 * @param value value
	 * @return namespaced key
	 */
	public static @NotNull NamespacedKey resourceLibKey(@NotNull String value) {
		return new NamespacedKey("resourcelib", value);
	}

	/**
	 * Loads default plugin resources under /resources/dataset.
	 * <br />
	 * Should be called only during {@link Plugin#onLoad()}.
	 *
	 * @param plugin plugin instance
	 */
	public static void loadDefaultResources(@NotNull Plugin plugin) {
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
	
	private static void loadItemMappings(@NotNull Plugin plugin) {
		var datasetsDir = new File(plugin.getDataFolder(), "datasets");
		for (File datasetDir : requireNonNull(datasetsDir.listFiles())) {
			if (!datasetDir.isDirectory()) continue;

			var itemsDir = new File(datasetDir, "item");
			if (!itemsDir.exists()) continue;
			if (!itemsDir.isDirectory()) continue;

			loadItemMappings(itemsDir, datasetDir.getName());
		}
	}

	private static void loadItemMappings(@NotNull File itemsDir, @NotNull String namespace) {
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
	public static @NotNull FontData requireFontData(@NotNull NamespacedKey key) {
		FontData fontData = storage().getFontData(key);
		return requireNonNull(fontData, "Couldn't find font mapping: %s".formatted(key));
	}

}
