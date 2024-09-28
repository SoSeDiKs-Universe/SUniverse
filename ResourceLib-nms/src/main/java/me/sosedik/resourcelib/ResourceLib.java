package me.sosedik.resourcelib;

import me.sosedik.resourcelib.api.font.FontData;
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
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceLib extends JavaPlugin {

	private static ResourceLib instance;

	private Scheduler scheduler;
	private ResourcePackStorage storage;
	private ResourcePackGenerator generator;

	@Override
	public void onLoad() {
		ResourceLib.instance = this;
		this.scheduler = new Scheduler(this);
		this.storage = new ResourcePackStorage();
		this.generator = new ResourcePackGenerator(this);
		this.generator.init();
		loadUserResources();

		TranslationHolder.extractLocales(this);

		TabRenderer.init(this);
	}

	private void loadUserResources() {
		var datasetsDir = new File(instance().getDataFolder(), "datasets");
		if (!datasetsDir.exists()) {
			FileUtil.createFolder(datasetsDir);
			return;
		}

		this.generator.parseResources(datasetsDir, true);
	}

	@Override
	public void onEnable() {
		this.generator.generate();
		// Give the last chance for plugins to access the data before invalidating
		getServer().getScheduler().runTaskLater(this, () -> this.generator = null, 1L);

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
	 * Gets the resource pack generator.
	 * <p>
	 * Available only during load, becomes invalid after the plugin has enabled.
	 *
	 * @return the resource pack generator
	 */
	public static @Nullable ResourcePackGenerator generator() {
		return instance().generator;
	}

	/**
	 * Makes a namespaced key with this plugin's namespace
	 *
	 * @param value value
	 * @return namespaced key
	 */
	public static @NotNull NamespacedKey resourceLibKey(@NotNull String value) {
		return new NamespacedKey(instance(), value);
	}

	/**
	 * Loads default plugin resources under /resources/dataset.
	 * Should be called only during {@link Plugin#onLoad()}.
	 *
	 * @param plugin plugin instance
	 */
	public static void loadDefaultResources(@NotNull Plugin plugin) {
		ResourcePackGenerator packGenerator = instance().generator;
		if (packGenerator == null) {
			logger().warn("Plugin {} tried to load resources too early or too late!", plugin.getName());
			return;
		}

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

		instance().generator.parseResources(datasetsDir, false);
	}

	/**
	 * Gets the font data by key
	 *
	 * @param key font key
	 * @return font data
	 */
	public static @NotNull FontData requireFontData(@NotNull NamespacedKey key) {
		FontData fontData = ResourceLib.storage().getFontData(key.asString());
		return Objects.requireNonNull(fontData);
	}

}
