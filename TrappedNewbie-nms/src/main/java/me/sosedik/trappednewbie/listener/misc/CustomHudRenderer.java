package me.sosedik.trappednewbie.listener.misc;

import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.hud.HudRenderer;
import me.sosedik.trappednewbie.api.task.BossBarTask;
import me.sosedik.trappednewbie.impl.hud.AbsorptionRenderer;
import me.sosedik.trappednewbie.impl.hud.AirRenderer;
import me.sosedik.trappednewbie.impl.hud.ArmorRenderer;
import me.sosedik.trappednewbie.impl.hud.HealthRenderer;
import me.sosedik.trappednewbie.impl.hud.HungerRenderer;
import me.sosedik.trappednewbie.listener.player.TaskManagement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Renders custom hud elements
 */
public class CustomHudRenderer implements Listener {

	private static final Map<UUID, Map<Class<? extends HudRenderer>, HudRenderer>> RENDERERS = new HashMap<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		var hudMessenger = HudMessenger.of(player);
		Map<Class<? extends HudRenderer>, HudRenderer> renderers = getRenderers(player);

		List<HudRenderer> huds = List.of(
			new AbsorptionRenderer(player),
			new AirRenderer(player),
			new ArmorRenderer(player),
			new HealthRenderer(player),
			new HungerRenderer(player)
//			new ThirstRenderer(player), // TODO thirst
//			new TemperatureRenderer(player) // TODO temperature
		);
		for (HudRenderer hud : huds) {
			renderers.put(hud.getClass(), hud);
			hudMessenger.addHudElement(TrappedNewbie.trappedNewbieKey(hud.getId()), hud::render);
		}

		huds = List.of(
//			new ClockRenderer(player),
//			new CompassRenderer(player),
//			new DepthMeterRenderer(player),
//			new ThermometerRenderer(player)
		);
		BossBarTask bossBarTask = TaskManagement.bossBar(player);
		for (HudRenderer hud : huds) {
			renderers.put(hud.getClass(), hud);
			bossBarTask.addIconProvider(hud.getId(), 0, p -> hud.render());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Map<Class<? extends HudRenderer>, HudRenderer> renderers = RENDERERS.remove(player.getUniqueId());
		if (renderers == null) return;

		renderers.values().forEach(HudRenderer::onQuit);
	}

	/**
	 * Gets registered renderer
	 *
	 * @param player player
	 * @param id renderer id
	 * @throws RuntimeException if requested unknown renderer
	 * @return renderer
	 */
	@SuppressWarnings("unchecked")
	public static <T extends HudRenderer> T getRenderer(Player player, Class<T> id) {
		Map<Class<? extends HudRenderer>, HudRenderer> renderers = getRenderers(player);
		HudRenderer renderer = renderers.get(id);
		if (renderer == null) throw new RuntimeException("Requested unknown renderer: " + id);
		return (T) renderer;
	}

	private static Map<Class<? extends HudRenderer>, HudRenderer> getRenderers(Player player) {
		return RENDERERS.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
	}

}
