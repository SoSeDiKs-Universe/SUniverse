package me.sosedik.resourcelib.feature;

import com.google.common.base.Preconditions;
import me.sosedik.resourcelib.ResourceLib;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

@NullMarked
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

	private TabRenderer(Player player) {
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
	public void addHudElement(NamespacedKey hudElementId, HudProvider hudElement) {
		this.hudProviders.put(hudElementId, hudElement);
	}

	/**
	 * Adds tab header element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addHeaderElement(NamespacedKey hudElementId, HudProvider hudElement) {
		this.headerProviders.put(hudElementId, hudElement);
	}

	/**
	 * Adds tab footer element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addFooterElement(NamespacedKey hudElementId, HudProvider hudElement) {
		this.footerProviders.put(hudElementId, hudElement);
	}

	@Override
	public void run() {
		this.components.clear();
		for (HudProvider hudProvider : this.hudProviders.values()) {
			List<Component> hudElement = hudProvider.getHud();
			if (hudElement != null)
				this.components.addAll(hudElement);
		}
		Component hud = combined(this.components);
		this.components.clear();
		this.components.add(hud);
		HEADER_PRIORITIES.forEach(key -> {
			HudProvider hudProvider = this.headerProviders.get(key);
			if (hudProvider == null) return;

			List<Component> hudElement = hudProvider.getHud();
			if (hudElement != null)
				this.components.addAll(hudElement);
		});
		Component header = combine(Component.newline(), this.components);
		this.components.clear();
		FOOTER_PRIORITIES.forEach(key -> {
			HudProvider hudProvider = this.footerProviders.get(key);
			if (hudProvider == null) return;

			List<Component> hudElement = hudProvider.getHud();
			if (hudElement != null)
				this.components.addAll(hudElement);
		});
		Component footer = combine(Component.newline(), this.components);
		this.player.sendPlayerListHeaderAndFooter(header, footer);
	}

	public static TabRenderer of(Player player) {
		Preconditions.checkArgument(player.isOnline(), "Player must be online");
		return STORED_HUDS.computeIfAbsent(player.getUniqueId(), k -> new TabRenderer(player));
	}

	public static void removePlayer(Player player) {
		TabRenderer hudMessenger = STORED_HUDS.remove(player.getUniqueId());
		if (hudMessenger != null)
			hudMessenger.cancel();
	}

	@FunctionalInterface
	public interface HudProvider {

		@Nullable List<Component> getHud();

	}

	/**
	 * Initializes tab renderer's options
	 *
	 * @param plugin plugin instance
	 */
	public static void init(ResourceLib plugin) {
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
