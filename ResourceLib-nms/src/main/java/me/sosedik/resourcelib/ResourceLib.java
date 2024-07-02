package me.sosedik.resourcelib;

import me.sosedik.resourcelib.dataset.ResourcePackStorage;
import me.sosedik.resourcelib.impl.item.modifier.CustomLoreModifier;
import me.sosedik.resourcelib.impl.item.modifier.CustomNameModifier;
import me.sosedik.resourcelib.listener.LocalizedResourcePackMessage;
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.FileUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceLib extends JavaPlugin {

	private static ResourceLib instance;
	private ResourcePackStorage storage;
	private ResourcePackGenerator generator;

	@Override
	public void onLoad() {
		ResourceLib.instance = this;
		this.storage = new ResourcePackStorage();
		this.generator = new ResourcePackGenerator(this);
		this.generator.init();
		loadUserResources();

		TranslationHolder.extractLocales(this);
	}

	private void loadUserResources() {
		var datasetsDir = new File(instance().getDataFolder(), "datasets");
		if (!datasetsDir.exists()) {
			FileUtil.createFolder(datasetsDir);
			return;
		}

		this.generator.parseResources(datasetsDir);
	}

	@Override
	public void onEnable() {
		this.generator.generate();
		this.generator = null;

		new CustomNameModifier(this).register();
		new CustomLoreModifier(this).register();

		if (getServer().getPluginManager().isPluginEnabled("FancyMotd")) {
			EventUtil.registerListeners(this, LocalizedResourcePackMessage.class);
		}
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
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static @NotNull ComponentLogger logger() {
		return instance().getComponentLogger();
	}

	public static @NotNull ResourcePackStorage storage() {
		return instance().storage;
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

		instance().generator.parseResources(datasetsDir);
	}

}
