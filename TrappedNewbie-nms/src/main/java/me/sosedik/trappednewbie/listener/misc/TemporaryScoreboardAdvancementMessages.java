package me.sosedik.trappednewbie.listener.misc;

import com.google.common.base.Preconditions;
import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.sosedik.resourcelib.feature.ScoreboardRenderer;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.listener.player.TrappedNewbiePlayerOptions;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Shows advancement progress messages in scoreboard
 */
@NullMarked
public class TemporaryScoreboardAdvancementMessages implements Listener {

	private static final NamespacedKey TEMP_ADV_MESSAGES = TrappedNewbie.trappedNewbieKey("temp_adv_messages");
	private static final Map<UUID, Map<String, Map.Entry<Supplier<Component>, Long>>> MESSAGES = new HashMap<>();

	@EventHandler
	public void onJoin(PlayerClientLoadedWorldEvent event) {
		Player player = event.getPlayer();
		ScoreboardRenderer.of(player).addProvider(TEMP_ADV_MESSAGES, () -> {
			if (!TrappedNewbiePlayerOptions.showAdvancementHelper(player)) return null;

			var suppliers = MESSAGES.get(player.getUniqueId());
			if (suppliers == null) return null;
			if (suppliers.isEmpty()) return null;

			return suppliers.values().stream().map(v -> v.getKey().get()).toList();
		});
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		MESSAGES.remove(event.getPlayer().getUniqueId());
	}

	public static void addMessage(Player player, String id, Supplier<Component> message) {
		Preconditions.checkArgument(player.isOnline(), "Player must be online");
		var map = MESSAGES.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
		Map.Entry<Supplier<Component>, Long> entry = Map.entry(message, System.currentTimeMillis());
		map.put(id, entry);
		TrappedNewbie.scheduler().sync(() -> map.remove(id, entry),  6 * 20L);
	}

}
