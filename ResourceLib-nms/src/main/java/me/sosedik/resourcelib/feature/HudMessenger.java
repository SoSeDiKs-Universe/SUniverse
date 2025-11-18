package me.sosedik.resourcelib.feature;

import com.google.common.base.Preconditions;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static me.sosedik.utilizer.api.message.Mini.combined;

@NullMarked
public class HudMessenger extends BukkitRunnable {

	private static final int ACTION_BAR_MESSAGE_DURATION = 40;
	private static final Map<UUID, HudMessenger> STORED_HUDS = new HashMap<>();

	private final Map<NamespacedKey, HudProvider> hudProviders = new HashMap<>();
	private final List<Component> huds = new ArrayList<>();
	private final Player player;
	private @Nullable Component actionBarMessage = null;
	private int messageTicks = 0;

	private HudMessenger(Player player) {
		this.player = player;
		ResourceLib.scheduler().sync(this, 0L, 1L);
	}

	/**
	 * Sets action bar message displayed to the player.
	 * It might be overwritten at any time.
	 *
	 * @param message action bar message
	 */
	public void displayMessage(Component message) {
		displayMessage(message, ACTION_BAR_MESSAGE_DURATION);
	}

	/**
	 * Sets action bar message displayed to the player.
	 * It might be overwritten at any time.
	 *
	 * @param message action bar message
	 */
	public void displayMessage(Component message, int messageTicks) {
		int length = SpacingUtil.getWidth(message);
		message = SpacingUtil.getOffset((int) Math.ceil(length / -2D), length, message);
		this.actionBarMessage = message;
		this.messageTicks = messageTicks;
	}

	/**
	 * Adds hud element for displaying until manually removed
	 *
	 * @param hudElementId hud element id
	 * @param hudElement   hud element
	 */
	public void addHudElement(NamespacedKey hudElementId, Supplier<@Nullable Component> hudElement) {
		this.hudProviders.put(hudElementId, new HudProvider(hudElementId, hudElement));
	}

	@Override
	public void run() {
		this.player.sendActionBar(getHudMessage());
	}

	public synchronized Component getHudMessage() {
		this.huds.clear();
		this.huds.add(SpacingUtil.ZERO_SPACE); // Used to identify custom actionbar messages
		for (HudProvider hudProvider : this.hudProviders.values()) {
			Component hudElement = hudProvider.getHud();
			if (hudElement != null)
				this.huds.add(hudElement);
		}
		if (this.actionBarMessage != null) {
			this.huds.add(this.actionBarMessage);
			if (--this.messageTicks <= 0)
				this.actionBarMessage = null;
		}
		return combined(this.huds);
	}

	public static HudMessenger of(Player player) {
		Preconditions.checkArgument(player.isOnline(), "Player must be online");
		return STORED_HUDS.computeIfAbsent(player.getUniqueId(), k -> new HudMessenger(player));
	}

	public static void removePlayer(Player player) {
		HudMessenger hudMessenger = STORED_HUDS.remove(player.getUniqueId());
		if (hudMessenger != null)
			hudMessenger.cancel();
	}

	private record HudProvider(NamespacedKey providerId, Supplier<@Nullable Component> hudProvider) {

		public @Nullable Component getHud() {
			return hudProvider().get();
		}

	}

}
