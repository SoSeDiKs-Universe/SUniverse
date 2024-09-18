package me.sosedik.resourcelib.feature;

import me.sosedik.resourcelib.ResourceLib;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

public class TabRenderer extends BukkitRunnable {

	private static final Map<UUID, TabRenderer> STORED_HUDS = new HashMap<>();

	private final Map<NamespacedKey, HudProvider> hudProviders = new HashMap<>();
	private final Map<NamespacedKey, HudProvider> headerProviders = new HashMap<>();
	private final Map<NamespacedKey, HudProvider> footerProviders = new HashMap<>();
	private final List<Component> components = new ArrayList<>();
	private final Player player;

	private TabRenderer(@NotNull Player player) {
		this.player = player;
		ResourceLib.scheduler().async(this, 0L, 20L);
	}

	/**
	 * Adds tab header element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addHudElement(@NotNull NamespacedKey hudElementId, @NotNull Supplier<@Nullable List<Component>> hudElement) {
		this.hudProviders.put(hudElementId, new HudProvider(hudElementId, hudElement));
	}

	/**
	 * Adds tab header element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addHeaderElement(@NotNull NamespacedKey hudElementId, @NotNull Supplier<@Nullable List<Component>> hudElement) {
		this.headerProviders.put(hudElementId, new HudProvider(hudElementId, hudElement));
	}

	/**
	 * Adds tab footer element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addFooterElement(@NotNull NamespacedKey hudElementId, @NotNull Supplier<@Nullable List<Component>> hudElement) {
		this.footerProviders.put(hudElementId, new HudProvider(hudElementId, hudElement));
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
		for (HudProvider hudProvider : this.headerProviders.values()) {
			List<Component> hudElement = hudProvider.getHud();
			if (hudElement != null)
				components.addAll(hudElement);
		}
		Component header = combine(Component.newline(), components);
		components.clear();
		for (HudProvider hudProvider : this.footerProviders.values()) {
			List<Component> hudElement = hudProvider.getHud();
			if (hudElement != null)
				components.addAll(hudElement);
		}
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

	private record HudProvider(@NotNull NamespacedKey providerId, @NotNull Supplier<@Nullable List<Component>> hudProvider) {

		public @Nullable List<Component> getHud() {
			return hudProvider().get();
		}

	}

}
