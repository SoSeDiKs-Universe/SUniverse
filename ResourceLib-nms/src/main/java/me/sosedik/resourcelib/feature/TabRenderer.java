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

public class TabRenderer extends BukkitRunnable {

	private static final Map<UUID, TabRenderer> STORED_HUDS = new HashMap<>();

	private final Map<NamespacedKey, HudProvider> hudProviders = new HashMap<>();
	private final Player player;

	private TabRenderer(@NotNull Player player) {
		this.player = player;
		ResourceLib.scheduler().async(this, 0L, 20L);
	}

	/**
	 * Adds hud element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addTopElement(@NotNull NamespacedKey hudElementId, @NotNull Supplier<List<Component>> hudElement) {
		this.hudProviders.put(hudElementId, new HudProvider(hudElementId, hudElement));
	}

	@Override
	public void run() {
		List<Component> huds = new ArrayList<>();
		for (HudProvider hudProvider : this.hudProviders.values()) {
			List<Component> hudElement = hudProvider.getHud();
			if (hudElement != null)
				huds.addAll(hudElement);
		}
		huds.add(Component.empty()); // TODO
		Component hud = combine(Component.newline(), huds);
		this.player.sendPlayerListHeader(hud);
	}

	public static @NotNull TabRenderer of(@NotNull Player player) {
		return STORED_HUDS.computeIfAbsent(player.getUniqueId(), k -> new TabRenderer(player));
	}

	public static void removePlayer(@NotNull Player player) {
		TabRenderer hudMessenger = STORED_HUDS.remove(player.getUniqueId());
		if (hudMessenger != null)
			hudMessenger.cancel();
	}

	private record HudProvider(@NotNull NamespacedKey providerId, @NotNull Supplier<List<Component>> hudProvider) {

		public @Nullable List<Component> getHud() {
			return hudProvider().get();
		}

	}

}
