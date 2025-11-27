package me.sosedik.trappednewbie.listener.advancement.dedicated;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.sosedik.resourcelib.feature.ScoreboardRenderer;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.listener.player.TrappedNewbiePlayerOptions;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.dataset.UtilizerTags;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class TickerAdvancements implements Listener {

	private static final NamespacedKey WARDEN_HUG_HELPER = TrappedNewbie.trappedNewbieKey("warden_hug_timer");
	private static final Map<UUID, Integer> WARDEN_HUGGERS = new HashMap<>();

	public TickerAdvancements() {
		TrappedNewbie.scheduler().sync(() -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.isDead()) continue;

				checkWardenAtWorldHeightAdvancement(player);
				checkWardensAdvancement(player);
				checkWardenHuggersAdvancement(player);
				checkRavagersAdvancement(player);
				checkFamilyReunionAdvancement(player);
				checkBoneToPartyAdvancement(player);
			}
		}, 20L, 20L);
	}

	@EventHandler
	public void onJoin(PlayerClientLoadedWorldEvent event) {
		Player player = event.getPlayer();
		String localeKey = "adv." + TrappedNewbieAdvancements.HUG_A_WARDEN_FOR_A_MINUTE.key().value().replace('/', '.') + ".title";
		ScoreboardRenderer.of(player).addProvider(WARDEN_HUG_HELPER, () -> {
			Integer count = WARDEN_HUGGERS.get(player.getUniqueId());
			if (count == null) return null;
			if (count < 10) return null;
			if (!TrappedNewbiePlayerOptions.showAdvancementHelper(player)) return null;

			Component title = Messenger.messenger(player).getMessage(localeKey);
			return List.of(title.append(Component.text(": " + count + "/60")));
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getPlayer();
		WARDEN_HUGGERS.remove(player.getUniqueId());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		WARDEN_HUGGERS.remove(player.getUniqueId());
	}

	private void checkWardenAtWorldHeightAdvancement(Player player) {
		if (TrappedNewbieAdvancements.GET_A_WARDEN_TO_THE_HEIGHT_LIMIT.isDone(player)) return;

		Collection<LivingEntity> nearbyLivingEntities = player.getLocation().getNearbyLivingEntities(8,
			entity -> entity.getType() == EntityType.WARDEN && entity.getLocation().y() >= entity.getWorld().getMaxHeight() - 1D);
		if (nearbyLivingEntities.isEmpty()) return;

		TrappedNewbieAdvancements.GET_A_WARDEN_TO_THE_HEIGHT_LIMIT.awardAllCriteria(player);
	}

	private void checkWardensAdvancement(Player player) {
		if (TrappedNewbieAdvancements.BE_NEAR_FIVE_WARDENS.isDone(player)) return;

		Collection<LivingEntity> nearbyLivingEntities = player.getLocation().getNearbyLivingEntities(16, entity -> entity.getType() == EntityType.WARDEN);
		if (nearbyLivingEntities.size() < 5) return;

		TrappedNewbieAdvancements.BE_NEAR_FIVE_WARDENS.awardAllCriteria(player);
	}

	private void checkWardenHuggersAdvancement(Player player) {
		if (TrappedNewbieAdvancements.HUG_A_WARDEN_FOR_A_MINUTE.isDone(player)) return;

		Collection<LivingEntity> nearbyLivingEntities = player.getLocation().getNearbyLivingEntities(5, entity -> entity.getType() == EntityType.WARDEN);
		if (nearbyLivingEntities.isEmpty()) {
			WARDEN_HUGGERS.remove(player.getUniqueId());
			return;
		}

		int seconds = WARDEN_HUGGERS.compute(player.getUniqueId(), (k, v) -> v == null ? 1 : v + 1);
		if (seconds != 60) return;

		// Leave a second for the advancement helper
		TrappedNewbie.scheduler().sync(() -> WARDEN_HUGGERS.remove(player.getUniqueId()), 20L);
		TrappedNewbieAdvancements.HUG_A_WARDEN_FOR_A_MINUTE.awardAllCriteria(player);
	}

	private void checkRavagersAdvancement(Player player) {
		if (TrappedNewbieAdvancements.BE_NEAR_20_RAVAGERS.isDone(player)) return;

		Collection<LivingEntity> nearbyLivingEntities = player.getLocation().getNearbyLivingEntities(32, entity -> entity.getType() == EntityType.RAVAGER);
		if (nearbyLivingEntities.size() < 20) return;

		TrappedNewbieAdvancements.BE_NEAR_20_RAVAGERS.awardAllCriteria(player);
	}

	private void checkFamilyReunionAdvancement(Player player) {
		if (TrappedNewbieAdvancements.ZOMBIE_FAMILY_REUNION.isDone(player)) return;

		Collection<LivingEntity> nearbyLivingEntities = player.getLocation().getNearbyLivingEntities(10);
		for (EntityType entityType : UtilizerTags.HUMAN_LIKE_ZOMBIES.getValues()) {
			if (entityType.getEntityClass() == null) continue;
			if (!Ageable.class.isAssignableFrom(entityType.getEntityClass())) continue;
			if (!nearbyLivingEntities.removeIf(entity -> entity.getType() == entityType && entity instanceof Ageable ageable && ageable.isAdult())) return;
			if (!nearbyLivingEntities.removeIf(entity -> entity.getType() == entityType && entity instanceof Ageable ageable && !ageable.isAdult())) return;
		}

		TrappedNewbieAdvancements.ZOMBIE_FAMILY_REUNION.awardAllCriteria(player);
	}

	private void checkBoneToPartyAdvancement(Player player) {
		if (TrappedNewbieAdvancements.BONE_TO_PARTY.isDone(player)) return;

		Collection<LivingEntity> nearbyLivingEntities = player.getLocation().getNearbyLivingEntities(10);
		if (!nearbyLivingEntities.removeIf(entity -> entity.getType() == EntityType.WITHER)) return;
		for (EntityType entityType : Tag.ENTITY_TYPES_SKELETONS.getValues()) {
			if (!nearbyLivingEntities.removeIf(entity -> entity.getType() == entityType)) return;
		}

		TrappedNewbieAdvancements.BONE_TO_PARTY.awardAllCriteria(player);
	}

}
