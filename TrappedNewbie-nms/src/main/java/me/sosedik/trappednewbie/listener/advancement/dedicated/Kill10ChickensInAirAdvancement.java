package me.sosedik.trappednewbie.listener.advancement.dedicated;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.sosedik.resourcelib.feature.ScoreboardRenderer;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.listener.player.TrappedNewbiePlayerOptions;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.EntityUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class Kill10ChickensInAirAdvancement implements Listener {

	private static final NamespacedKey AIR_BATTLE_HELPER = TrappedNewbie.trappedNewbieKey("air_battle_score");
	private static final Map<UUID, Integer> COUNT_TRACKER = new HashMap<>();

	@EventHandler
	public void onJoin(PlayerClientLoadedWorldEvent event) {
		Player player = event.getPlayer();
		String localeKey = "adv." + TrappedNewbieAdvancements.KILL_10_CHICKEN_IN_AIR.key().value().replace('/', '.') + ".title";
		ScoreboardRenderer.of(player).addProvider(AIR_BATTLE_HELPER, () -> {
			Integer count = COUNT_TRACKER.get(player.getUniqueId());
			if (count == null) return null;
			if (!TrappedNewbiePlayerOptions.showAdvancementHelper(player)) return null;

			Component title = Messenger.messenger(player).getMessage(localeKey);
			return List.of(title.append(Component.text(": " + count + "/10")));
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKill(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Chicken chicken)) return;
		if (!(event.getDamageSource().getDirectEntity() instanceof AbstractArrow arrow)) return;
		if (!Tag.ENTITY_TYPES_ARROWS.isTagged(arrow.getType())) return;
		if (!EntityUtil.isInAirLazy(chicken)) return;

		Player killer = chicken.getKiller();
		if (killer == null) return;
		if (isDisqualified(killer)) return;

		int count = COUNT_TRACKER.getOrDefault(killer.getUniqueId(), 0) + 1;
		if (count == 10) {
			TrappedNewbieAdvancements.KILL_10_CHICKEN_IN_AIR.awardAllCriteria(killer);
			COUNT_TRACKER.remove(killer.getUniqueId());
			return;
		} else if (count == 1) {
			TrappedNewbie.scheduler().sync(task -> {
				if (!isDisqualified(killer)) return false;
				COUNT_TRACKER.remove(killer.getUniqueId());
				return true;
			}, 1L, 1L);
		}
		COUNT_TRACKER.put(killer.getUniqueId(), count);
	}

	private boolean isDisqualified(Player player) {
		if (TrappedNewbieAdvancements.KILL_10_CHICKEN_IN_AIR.isDone(player)) return true;
		if (!player.isValid()) return true;
		return !EntityUtil.isInAirLazy(player);
	}

}
