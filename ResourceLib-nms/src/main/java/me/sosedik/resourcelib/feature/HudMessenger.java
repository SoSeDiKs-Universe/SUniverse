package me.sosedik.resourcelib.feature;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
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

import static me.sosedik.utilizer.api.message.Mini.combined;

public class HudMessenger extends BukkitRunnable {

	private static final long ACTION_BAR_MESSAGE_DURATION = 50L;
	private static final Map<UUID, HudMessenger> STORED_HUDS = new HashMap<>();

	private final Map<NamespacedKey, HudProvider> hudProviders = new HashMap<>();
	private final Player player;
	private Component actionBarMessage = null;

	private HudMessenger(@NotNull Player player) {
		this.player = player;
		ResourceLib.scheduler().async(this, 0L, 1L);
	}

	/**
	 * Sets action bar message displayed to the player.
	 * It might be overwritten at any time.
	 *
	 * @param message action bar message
	 */
	public void displayMessage(@NotNull Component message) {
		int length = SpacingUtil.getWidth(message);
		message = SpacingUtil.getOffset((int) Math.ceil(length / -2D), length, message);
		this.actionBarMessage = message;
		Component finalMessage = message;
		ResourceLib.scheduler().async(() -> {
			if (this.actionBarMessage == finalMessage)
				this.actionBarMessage = null;
		}, ACTION_BAR_MESSAGE_DURATION);
	}

	/**
	 * Adds hud element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addHudElement(@NotNull NamespacedKey hudElementId, @NotNull Supplier<Component> hudElement) {
		this.hudProviders.put(hudElementId, new HudProvider(hudElementId, hudElement));
	}

	@Override
	public void run() {
		List<Component> huds = new ArrayList<>();
		for (HudProvider hudProvider : this.hudProviders.values()) {
			Component hudElement = hudProvider.getHud();
			if (hudElement != null)
				huds.add(hudElement);
		}
		if (this.actionBarMessage != null)
			huds.add(this.actionBarMessage);
		Component hud = combined(huds);
		this.player.sendActionBar(hud);
	}

	public static @NotNull HudMessenger of(@NotNull Player player) {
		return STORED_HUDS.computeIfAbsent(player.getUniqueId(), k -> new HudMessenger(player));
	}

	public static void removePlayer(@NotNull Player player) {
		HudMessenger hudMessenger = STORED_HUDS.remove(player.getUniqueId());
		if (hudMessenger != null)
			hudMessenger.cancel();
	}

	private record HudProvider(@NotNull NamespacedKey providerId, @NotNull Supplier<Component> hudProvider) {

		public @Nullable Component getHud() {
			return hudProvider().get();
		}

	}

}