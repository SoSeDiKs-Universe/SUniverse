package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Statistics advancements
 */
@NullMarked
public class StatisticAdvancements implements Listener {

	public StatisticAdvancements() {
		TrappedNewbie.scheduler().async(() -> Bukkit.getOnlinePlayers().forEach(this::trackMovementStats), 3 * 20L, 3 * 20L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onStatIncrement(PlayerStatisticIncrementEvent event) {
		Player player = event.getPlayer();
		int statValue = event.getNewValue();
		switch (event.getStatistic()) {
			case USE_ITEM -> {
				switch (event.getMaterial()) {
					case TOTEM_OF_UNDYING -> {
						if (statValue >= 5) TrappedNewbieAdvancements.TOTEM_5.awardAllCriteria(player);
						if (statValue >= 10) TrappedNewbieAdvancements.TOTEM_10.awardAllCriteria(player);
						if (statValue >= 25) TrappedNewbieAdvancements.TOTEM_25.awardAllCriteria(player);
						if (statValue >= 50) TrappedNewbieAdvancements.TOTEM_50.awardAllCriteria(player);
						if (statValue >= 100) TrappedNewbieAdvancements.TOTEM_100.awardAllCriteria(player);
						if (statValue >= 250) TrappedNewbieAdvancements.TOTEM_250.awardAllCriteria(player);
						if (statValue >= 500) TrappedNewbieAdvancements.TOTEM_500.awardAllCriteria(player);
						if (statValue >= 1000) TrappedNewbieAdvancements.TOTEM_1000.awardAllCriteria(player);
						if (statValue >= 2500) TrappedNewbieAdvancements.TOTEM_2500.awardAllCriteria(player);
						if (statValue >= 5000) TrappedNewbieAdvancements.TOTEM_5000.awardAllCriteria(player);
					}
					case IRON_PICKAXE -> {
						if (statValue >= 100) TrappedNewbieAdvancements.BREAK_100_IRON.awardAllCriteria(player);
					}
					case DIAMOND_PICKAXE -> {
						if (statValue >= 2500) TrappedNewbieAdvancements.BREAK_2500_DIAMOND.awardAllCriteria(player);
					}
					case NETHERITE_PICKAXE -> {
						if (statValue >= 10_000) TrappedNewbieAdvancements.BREAK_10K_NETHERITE.awardAllCriteria(player);
						if (statValue >= 100_000) TrappedNewbieAdvancements.BREAK_100K_NETHERITE.awardAllCriteria(player);
					}
					case null, default -> {}
				}
			}
			case CHEST_OPENED -> {
				if (statValue >= 100) TrappedNewbieAdvancements.OPEN_CHEST_100.awardAllCriteria(player);
				if (statValue >= 1000) TrappedNewbieAdvancements.OPEN_CHEST_1K.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.OPEN_CHEST_10K.awardAllCriteria(player);
				if (statValue >= 25_000) TrappedNewbieAdvancements.OPEN_CHEST_25K.awardAllCriteria(player);
			}
			case SHULKER_BOX_OPENED -> {
				if (statValue >= 100) TrappedNewbieAdvancements.OPEN_SHULKER_100.awardAllCriteria(player);
				if (statValue >= 1000) TrappedNewbieAdvancements.OPEN_SHULKER_1K.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.OPEN_SHULKER_10K.awardAllCriteria(player);
				if (statValue >= 100_000) TrappedNewbieAdvancements.OPEN_SHULKER_100K.awardAllCriteria(player);
			}
			case CRAFTING_TABLE_INTERACTION -> {
				if (statValue >= 15) TrappedNewbieAdvancements.OPEN_CRAFTING_TABLE_15.awardAllCriteria(player);
				if (statValue >= 100) TrappedNewbieAdvancements.OPEN_CRAFTING_TABLE_100.awardAllCriteria(player);
				if (statValue >= 500) TrappedNewbieAdvancements.OPEN_CRAFTING_TABLE_500.awardAllCriteria(player);
				if (statValue >= 2500) TrappedNewbieAdvancements.OPEN_CRAFTING_TABLE_2500.awardAllCriteria(player);
			}
			case DEATHS -> {
				TrappedNewbieAdvancements.DEATHS_1.awardAllCriteria(player);
				if (statValue >= 50) TrappedNewbieAdvancements.DEATHS_50.awardAllCriteria(player);
				if (statValue >= 250) TrappedNewbieAdvancements.DEATHS_250.awardAllCriteria(player);
				if (statValue >= 1000) TrappedNewbieAdvancements.DEATHS_1000.awardAllCriteria(player);
			}
			case JUMP -> {
				if (statValue >= 1000) TrappedNewbieAdvancements.JUMP_1K.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.JUMP_10K.awardAllCriteria(player);
				if (statValue >= 50_000) TrappedNewbieAdvancements.JUMP_50K.awardAllCriteria(player);
				if (statValue >= 100_000) TrappedNewbieAdvancements.JUMP_100K.awardAllCriteria(player);
				if (statValue >= 250_000) TrappedNewbieAdvancements.JUMP_250K.awardAllCriteria(player);
				if (statValue >= 500_000) TrappedNewbieAdvancements.JUMP_500K.awardAllCriteria(player);
				if (statValue >= 1_000_000) TrappedNewbieAdvancements.JUMP_1000K.awardAllCriteria(player);
			}
			case ITEM_ENCHANTED -> {
				if (statValue >= 10) TrappedNewbieAdvancements.ENCHANT_10.awardAllCriteria(player);
				if (statValue >= 50) TrappedNewbieAdvancements.ENCHANT_50.awardAllCriteria(player);
				if (statValue >= 250) TrappedNewbieAdvancements.ENCHANT_250.awardAllCriteria(player);
				if (statValue >= 1000) TrappedNewbieAdvancements.ENCHANT_1000.awardAllCriteria(player);
				if (statValue >= 2500) TrappedNewbieAdvancements.ENCHANT_2500.awardAllCriteria(player);
				if (statValue >= 5000) TrappedNewbieAdvancements.ENCHANT_5K.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.ENCHANT_10K.awardAllCriteria(player);
			}
			case ANIMALS_BRED -> {
				if (statValue >= 100) TrappedNewbieAdvancements.BREED_100.awardAllCriteria(player);
				if (statValue >= 500) TrappedNewbieAdvancements.BREED_500.awardAllCriteria(player);
				if (statValue >= 2500) TrappedNewbieAdvancements.BREED_2500.awardAllCriteria(player);
				if (statValue >= 5000) TrappedNewbieAdvancements.BREED_5000.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.BREED_10000.awardAllCriteria(player);
				if (statValue >= 15_000) TrappedNewbieAdvancements.BREED_15000.awardAllCriteria(player);
				if (statValue >= 25_000) TrappedNewbieAdvancements.BREED_25000.awardAllCriteria(player);
			}
			case MOB_KILLS -> {
				if (statValue >= 250) TrappedNewbieAdvancements.KILL_250.awardAllCriteria(player);
				if (statValue >= 2500) TrappedNewbieAdvancements.KILL_2500.awardAllCriteria(player);
				if (statValue >= 25_000) TrappedNewbieAdvancements.KILL_25K.awardAllCriteria(player);
				if (statValue >= 50_000) TrappedNewbieAdvancements.KILL_50K.awardAllCriteria(player);
				if (statValue >= 100_000) TrappedNewbieAdvancements.KILL_100K.awardAllCriteria(player);
				if (statValue >= 250_000) TrappedNewbieAdvancements.KILL_250K.awardAllCriteria(player);
				if (statValue >= 500_000) TrappedNewbieAdvancements.KILL_500K.awardAllCriteria(player);
			}
			case TRADED_WITH_VILLAGER -> {
				if (statValue >= 100) TrappedNewbieAdvancements.TRADE_100.awardAllCriteria(player);
				if (statValue >= 500) TrappedNewbieAdvancements.TRADE_500.awardAllCriteria(player);
				if (statValue >= 2500) TrappedNewbieAdvancements.TRADE_2500.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.TRADE_10K.awardAllCriteria(player);
				if (statValue >= 25_000) TrappedNewbieAdvancements.TRADE_25K.awardAllCriteria(player);
				if (statValue >= 50_000) TrappedNewbieAdvancements.TRADE_50K.awardAllCriteria(player);
				if (statValue >= 250_000) TrappedNewbieAdvancements.TRADE_250K.awardAllCriteria(player);
			}
			case RAID_WIN -> {
				if (statValue >= 5) TrappedNewbieAdvancements.RAID_5.awardAllCriteria(player);
				if (statValue >= 20) TrappedNewbieAdvancements.RAID_20.awardAllCriteria(player);
				if (statValue >= 100) TrappedNewbieAdvancements.RAID_100.awardAllCriteria(player);
				if (statValue >= 2000) TrappedNewbieAdvancements.RAID_2000.awardAllCriteria(player);
			}
			case BELL_RING -> {
				if (statValue >= 100) TrappedNewbieAdvancements.BELL_100.awardAllCriteria(player);
				if (statValue >= 1000) TrappedNewbieAdvancements.BELL_1K.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.BELL_10K.awardAllCriteria(player);
				if (statValue >= 100_000) TrappedNewbieAdvancements.BELL_100K.awardAllCriteria(player);
				if (statValue >= 1_000_000) TrappedNewbieAdvancements.BELL_1000K.awardAllCriteria(player);
				if (statValue >= 10_000_000) TrappedNewbieAdvancements.BELL_10000K.awardAllCriteria(player);
			}
			case FISH_CAUGHT -> {
				if (statValue >= 250) TrappedNewbieAdvancements.FISH_250.awardAllCriteria(player);
				if (statValue >= 1000) TrappedNewbieAdvancements.FISH_1K.awardAllCriteria(player);
				if (statValue >= 2500) TrappedNewbieAdvancements.FISH_2500.awardAllCriteria(player);
				if (statValue >= 5000) TrappedNewbieAdvancements.FISH_5K.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.FISH_10K.awardAllCriteria(player);
				if (statValue >= 50_000) TrappedNewbieAdvancements.FISH_50K.awardAllCriteria(player);
			}
		}
	}

	private void trackMovementStats(Player player) {
		trackMovementStat(player, Statistic.WALK_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.WALK_10KM);
		trackMovementStat(player, Statistic.WALK_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.WALK_50KM);
		trackMovementStat(player, Statistic.WALK_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.WALK_250KM);
		trackMovementStat(player, Statistic.WALK_ONE_CM, 1000 * 1000 * 100, TrappedNewbieAdvancements.WALK_1000KM);
		trackMovementStat(player, Statistic.WALK_ONE_CM, 5000 * 1000 * 100, TrappedNewbieAdvancements.WALK_5000KM);
		trackMovementStat(player, Statistic.WALK_ONE_CM, 10_000 * 1000 * 100, TrappedNewbieAdvancements.WALK_10000KM);
		trackMovementStat(player, Statistic.SPRINT_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.SPRINT_10KM);
		trackMovementStat(player, Statistic.SPRINT_ONE_CM, 42_195 * 100, TrappedNewbieAdvancements.SPRINT_MARATHON);
		trackMovementStat(player, Statistic.SPRINT_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.SPRINT_250KM);
		trackMovementStat(player, Statistic.SPRINT_ONE_CM, 1000 * 1000 * 100, TrappedNewbieAdvancements.SPRINT_1000KM);
		trackMovementStat(player, Statistic.SPRINT_ONE_CM, 2500 * 1000 * 100, TrappedNewbieAdvancements.SPRINT_2500KM);
		trackMovementStat(player, Statistic.SPRINT_ONE_CM, 5000 * 1000 * 100, TrappedNewbieAdvancements.SPRINT_5000KM);
		trackMovementStat(player, Statistic.SPRINT_ONE_CM, 10_000 * 1000 * 100, TrappedNewbieAdvancements.SPRINT_10000KM);
		trackMovementStat(player, Statistic.SWIM_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.SWIM_1KM);
		trackMovementStat(player, Statistic.SWIM_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.SWIM_10KM);
		trackMovementStat(player, Statistic.SWIM_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.SWIM_50KM);
		trackMovementStat(player, Statistic.SWIM_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.SWIM_100KM);
		trackMovementStat(player, Statistic.SWIM_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.SWIM_250KM);
		trackMovementStat(player, Statistic.SWIM_ONE_CM, 500 * 1000 * 100, TrappedNewbieAdvancements.SWIM_500KM);
		trackMovementStat(player, Statistic.SWIM_ONE_CM, 1000 * 1000 * 100, TrappedNewbieAdvancements.SWIM_1000KM);
		trackMovementStat(player, Statistic.BOAT_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.BOAT_1KM);
		trackMovementStat(player, Statistic.BOAT_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.BOAT_10KM);
		trackMovementStat(player, Statistic.BOAT_ONE_CM, 25 * 1000 * 100, TrappedNewbieAdvancements.BOAT_25KM);
		trackMovementStat(player, Statistic.BOAT_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.BOAT_50KM);
		trackMovementStat(player, Statistic.BOAT_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.BOAT_100KM);
		trackMovementStat(player, Statistic.BOAT_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.BOAT_250KM);
		trackMovementStat(player, Statistic.BOAT_ONE_CM, 500 * 1000 * 100, TrappedNewbieAdvancements.BOAT_500KM);
		trackMovementStat(player, Statistic.CLIMB_ONE_CM, 100 * 100, TrappedNewbieAdvancements.CLIMB_100M);
		trackMovementStat(player, Statistic.CLIMB_ONE_CM, 500 * 100, TrappedNewbieAdvancements.CLIMB_500M);
		trackMovementStat(player, Statistic.CLIMB_ONE_CM, 3 * 1000 * 100, TrappedNewbieAdvancements.CLIMB_3KM);
		trackMovementStat(player, Statistic.CLIMB_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.CLIMB_10KM);
		trackMovementStat(player, Statistic.MINECART_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.MINECART_1KM);
		trackMovementStat(player, Statistic.MINECART_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.MINECART_10KM);
		trackMovementStat(player, Statistic.MINECART_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.MINECART_50KM);
		trackMovementStat(player, Statistic.MINECART_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.MINECART_250KM);
		trackMovementStat(player, Statistic.AVIATE_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.ELYTRA_10KM);
		trackMovementStat(player, Statistic.AVIATE_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.ELYTRA_100KM);
		trackMovementStat(player, Statistic.AVIATE_ONE_CM, 1000 * 1000 * 100, TrappedNewbieAdvancements.ELYTRA_1000KM);
		trackMovementStat(player, Statistic.AVIATE_ONE_CM, 2500 * 1000 * 100, TrappedNewbieAdvancements.ELYTRA_2500KM);
		trackMovementStat(player, Statistic.AVIATE_ONE_CM, 5000 * 1000 * 100, TrappedNewbieAdvancements.ELYTRA_5000KM);
		trackMovementStat(player, Statistic.AVIATE_ONE_CM, 10_000 * 1000 * 100, TrappedNewbieAdvancements.ELYTRA_10000KM);
		trackMovementStat(player, Statistic.AVIATE_ONE_CM, 15_000 * 1000 * 100, TrappedNewbieAdvancements.ELYTRA_15000KM);
		trackMovementStat(player, Statistic.CROUCH_ONE_CM, 100 * 100, TrappedNewbieAdvancements.SNEAK_100M);
		trackMovementStat(player, Statistic.CROUCH_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.SNEAK_1KM);
		trackMovementStat(player, Statistic.CROUCH_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.SNEAK_10KM);
		trackMovementStat(player, Statistic.CROUCH_ONE_CM, 25 * 1000 * 100, TrappedNewbieAdvancements.SNEAK_25KM);
		trackMovementStat(player, Statistic.CROUCH_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.SNEAK_50KM);
		trackMovementStat(player, Statistic.CROUCH_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.SNEAK_100KM);
		trackMovementStat(player, Statistic.CROUCH_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.SNEAK_250KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 10_000, TrappedNewbieAdvancements.STRIDER_100M);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.STRIDER_1KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_10KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 25 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_25KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_50KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_100KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_250KM);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.HORSE_1K);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.HORSE_10K);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 25 * 1000 * 100, TrappedNewbieAdvancements.HORSE_25K);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.HORSE_50K);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.HORSE_100K);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.HORSE_250K);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 500 * 1000 * 100, TrappedNewbieAdvancements.HORSE_500K);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 10_000, TrappedNewbieAdvancements.PIG_100M);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.PIG_1KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.PIG_10KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 25 * 1000 * 100, TrappedNewbieAdvancements.PIG_25KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.PIG_50KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.PIG_100KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.PIG_250KM);
		trackMovementStat(player, Statistic.PLAY_ONE_MINUTE, 24 * 60 * 60 * 20, TrappedNewbieAdvancements.PLAY_1D);
		trackMovementStat(player, Statistic.PLAY_ONE_MINUTE, 100 * 24 * 60 * 60 * 20, TrappedNewbieAdvancements.PLAY_100D);
		trackMovementStat(player, Statistic.PLAY_ONE_MINUTE, 365 * 24 * 60 * 60 * 20, TrappedNewbieAdvancements.PLAY_365D);
		trackMovementStat(player, Statistic.PLAY_ONE_MINUTE, 1000 * 24 * 60 * 60 * 20, TrappedNewbieAdvancements.PLAY_1000D);
		trackMovementStat(player, Statistic.TIME_SINCE_DEATH, 60 * 60 * 20, TrappedNewbieAdvancements.SURVIVE_1H);
		trackMovementStat(player, Statistic.TIME_SINCE_DEATH, 10 * 60 * 60 * 20, TrappedNewbieAdvancements.SURVIVE_10H);
		trackMovementStat(player, Statistic.TIME_SINCE_DEATH, 50 * 60 * 60 * 20, TrappedNewbieAdvancements.SURVIVE_50H);
		trackMovementStat(player, Statistic.TIME_SINCE_DEATH, 200 * 60 * 60 * 20, TrappedNewbieAdvancements.SURVIVE_200H);
	}

	private void trackMovementStat(Player player, Statistic statistic, int required, IAdvancement advancement) {
		int stat = player.getStatistic(statistic);
		if (stat >= required)
			advancement.awardAllCriteria(player);
	}

}
