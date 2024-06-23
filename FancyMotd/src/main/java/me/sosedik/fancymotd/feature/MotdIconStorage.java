package me.sosedik.fancymotd.feature;

import me.sosedik.fancymotd.FancyMotd;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MotdIconStorage {

	private static final List<CachedServerIcon> SERVER_ICONS = new ArrayList<>();
	private static final Random RANDOM = new Random();

	/**
	 * Gets random cached icon
	 *
	 * @return random cached icon
	 */
	public static @Nullable CachedServerIcon getRandomIcon() {
		if (SERVER_ICONS.isEmpty()) return null;
		return SERVER_ICONS.get(RANDOM.nextInt(SERVER_ICONS.size()));
	}

	/**
	 * Refreshes plugin icons from storage
	 *
	 * @param plugin plugin instance
	 */
	public static void refreshIcons(@NotNull FancyMotd plugin) {
		SERVER_ICONS.clear();

		var storage = new File(plugin.getDataFolder(), "icons");
		extractIcons(plugin, storage);
		loadIcons(storage);
	}

	private static void extractIcons(@NotNull Plugin plugin, @NotNull File storage) {
		if (storage.exists()) return;

		if (!storage.mkdirs())
			FancyMotd.logger().warn("Could not create icons folder!");
		var jarFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		if (!jarFile.isFile()) return;

		try (var jar = new JarFile(jarFile)) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.startsWith("icons") && name.endsWith(".png"))
					plugin.saveResource(name, false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadIcons(@NotNull File storage) {
		for (File icon : Objects.requireNonNull(storage.listFiles())) {
			if (!icon.getName().endsWith(".png")) continue;
			try {
				SERVER_ICONS.add(Bukkit.getServer().loadServerIcon(icon));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
