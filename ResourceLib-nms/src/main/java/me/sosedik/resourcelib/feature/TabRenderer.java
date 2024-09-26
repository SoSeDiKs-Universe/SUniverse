package me.sosedik.resourcelib.feature;

import me.sosedik.resourcelib.ResourceLib;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

public class TabRenderer extends BukkitRunnable {

	private static final NamespacedKey EMPTY_LINE_KEY = ResourceLib.resourceLibKey("empty_line");

	private static final Map<UUID, TabRenderer> STORED_HUDS = new HashMap<>();
	private static final List<NamespacedKey> HEADER_PRIORITIES = new ArrayList<>();
	private static final List<NamespacedKey> FOOTER_PRIORITIES = new ArrayList<>();

	private final Map<NamespacedKey, HudProvider> hudProviders = new HashMap<>();
	private final Map<NamespacedKey, HudProvider> headerProviders = new HashMap<>();
	private final Map<NamespacedKey, HudProvider> footerProviders = new HashMap<>();
	private final List<Component> components = new ArrayList<>();
	private final Player player;

	private TabRenderer(@NotNull Player player) {
		this.player = player;

		addHeaderElement(EMPTY_LINE_KEY, () -> List.of(Component.empty()));
		addFooterElement(EMPTY_LINE_KEY, () -> List.of(Component.empty()));

		ResourceLib.scheduler().async(this, 0L, 20L);
	}

	/**
	 * Adds tab hud element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addHudElement(@NotNull NamespacedKey hudElementId, @NotNull HudProvider hudElement) {
		this.hudProviders.put(hudElementId, hudElement);
	}

	/**
	 * Adds tab header element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addHeaderElement(@NotNull NamespacedKey hudElementId, @NotNull HudProvider hudElement) {
		this.headerProviders.put(hudElementId, hudElement);
	}

	/**
	 * Adds tab footer element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addFooterElement(@NotNull NamespacedKey hudElementId, @NotNull HudProvider hudElement) {
		this.footerProviders.put(hudElementId, hudElement);
	}

	@Override
	public void run() {
		components.clear();
		for (HudProvider hudProvider : this.hudProviders.values()) {
			List<Component> hudElement = hudProvider.getHud();
			if (hudElement != null)
				components.addAll(hudElement);
		}
		Component hud = combined(components);
		components.clear();
		components.add(hud);
		HEADER_PRIORITIES.forEach(key -> {
			HudProvider hudProvider = headerProviders.get(key);
			if (hudProvider == null) return;

			List<Component> hudElement = hudProvider.getHud();
			if (hudElement != null)
				components.addAll(hudElement);
		});
		Component header = combine(Component.newline(), components);
		components.clear();
		FOOTER_PRIORITIES.forEach(key -> {
			HudProvider hudProvider = footerProviders.get(key);
			if (hudProvider == null) return;

			List<Component> hudElement = hudProvider.getHud();
			if (hudElement != null)
				components.addAll(hudElement);
		});
		Component footer = combine(Component.newline(), components);
		this.player.sendPlayerListHeaderAndFooter(header, footer);
	}

	public static @NotNull TabRenderer of(@NotNull Player player) {
		return STORED_HUDS.computeIfAbsent(player.getUniqueId(), k -> new TabRenderer(player));
	}

	public static void removePlayer(@NotNull Player player) {
		TabRenderer hudMessenger = STORED_HUDS.remove(player.getUniqueId());
		if (hudMessenger != null)
			hudMessenger.cancel();
	}

	@FunctionalInterface
	public interface HudProvider {

		@Nullable List<Component> getHud();

	}

	/**
	 * Inits tab renderer's options
	 *
	 * @param plugin plugin instance
	 */
	public static void init(@NotNull ResourceLib plugin) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("tab.header-priorities") || !config.contains("tab.footer-priorities")) {
			config.set("tab.header-priorities", List.of());
			config.set("tab.footer-priorities", List.of());
			return;
		}

		List<String> headerPriorities = config.getStringList("tab.header-priorities");
		List<String> footerPriorities = config.getStringList("tab.footer-priorities");
		headerPriorities.forEach(key -> HEADER_PRIORITIES.add(NamespacedKey.fromString(key)));
		footerPriorities.forEach(key -> FOOTER_PRIORITIES.add(NamespacedKey.fromString(key)));
	}

}
