package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		int statValue = player.getStatistic(Statistic.LEAVE_GAME);
		if (statValue == 0) return;

		TrappedNewbieAdvancements.GAME_LEAVES_1.awardAllCriteria(player);
		TrappedNewbieAdvancements.GAME_LEAVES_10.modifySimpleProgress(player, p -> p.setGained(statValue));
		TrappedNewbieAdvancements.GAME_LEAVES_100.modifySimpleProgress(player, p -> p.setGained(statValue));
		TrappedNewbieAdvancements.GAME_LEAVES_1K.modifySimpleProgress(player, p -> p.setGained(statValue));
		TrappedNewbieAdvancements.GAME_LEAVES_5K.modifySimpleProgress(player, p -> p.setGained(statValue));
		TrappedNewbieAdvancements.GAME_LEAVES_10K.modifySimpleProgress(player, p -> p.setGained(statValue));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onStatIncrement(PlayerStatisticIncrementEvent event) {
		Player player = event.getPlayer();
		int statValue = event.getNewValue();
		switch (event.getStatistic()) {
			case USE_ITEM -> {
				switch (event.getMaterial()) {
					case TOTEM_OF_UNDYING -> {
						TrappedNewbieAdvancements.TOTEM_5.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.TOTEM_10.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.TOTEM_25.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.TOTEM_50.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.TOTEM_100.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.TOTEM_250.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.TOTEM_500.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.TOTEM_1000.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.TOTEM_2500.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.TOTEM_5000.modifySimpleProgress(player, p -> p.setGained(statValue));
					}
					case IRON_PICKAXE -> {
						TrappedNewbieAdvancements.BREAK_100_IRON.modifySimpleProgress(player, p -> p.setGained(statValue));
					}
					case DIAMOND_PICKAXE -> {
						TrappedNewbieAdvancements.BREAK_2500_DIAMOND.modifySimpleProgress(player, p -> p.setGained(statValue));
					}
					case NETHERITE_PICKAXE -> {
						TrappedNewbieAdvancements.BREAK_10K_NETHERITE.modifySimpleProgress(player, p -> p.setGained(statValue));
						TrappedNewbieAdvancements.BREAK_100K_NETHERITE.modifySimpleProgress(player, p -> p.setGained(statValue));
					}
					case ENDER_PEARL -> {
						TrappedNewbieAdvancements.USE_100_STACKS_OF_ENDER_PEARLS.modifySimpleProgress(player, p -> p.setGained(statValue));
					}
					case null, default -> {}
				}
			}
			case CHEST_OPENED -> {
				TrappedNewbieAdvancements.OPEN_CHEST_100.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.OPEN_CHEST_1K.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.OPEN_CHEST_10K.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.OPEN_CHEST_25K.modifySimpleProgress(player, p -> p.setGained(statValue));
			}
			case SHULKER_BOX_OPENED -> {
				TrappedNewbieAdvancements.OPEN_SHULKER_100.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.OPEN_SHULKER_1K.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.OPEN_SHULKER_10K.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.OPEN_SHULKER_100K.modifySimpleProgress(player, p -> p.setGained(statValue));
			}
			case CRAFTING_TABLE_INTERACTION -> {
				TrappedNewbieAdvancements.OPEN_CRAFTING_TABLE_15.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.OPEN_CRAFTING_TABLE_100.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.OPEN_CRAFTING_TABLE_500.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.OPEN_CRAFTING_TABLE_2500.modifySimpleProgress(player, p -> p.setGained(statValue));
			}
			case DEATHS -> {
				TrappedNewbieAdvancements.DEATHS_1.awardAllCriteria(player);
				TrappedNewbieAdvancements.DEATHS_50.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.DEATHS_250.modifySimpleProgress(player, p -> p.setGained(statValue));
				TrappedNewbieAdvancements.DEATHS_1000.modifySimpleProgress(player, p -> p.setGained(statValue));
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
				if (statValue >= 5000) TrappedNewbieAdvancements.BREED_5K.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.BREED_10K.awardAllCriteria(player);
				if (statValue >= 15_000) TrappedNewbieAdvancements.BREED_15K.awardAllCriteria(player);
				if (statValue >= 25_000) TrappedNewbieAdvancements.BREED_25K.awardAllCriteria(player);
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
			case KILL_ENTITY -> {
				EntityType entityType = event.getEntityType();
				if (entityType == EntityType.SKELETON_HORSE) TrappedNewbieAdvancements.KILL_400_SKELETON_HORSES.modifySimpleProgress(player, p -> p.setGained(statValue));
				else if (entityType == EntityType.ENDERMITE) TrappedNewbieAdvancements.KILL_10K_ENDERMITES.modifySimpleProgress(player, p -> p.setGained(statValue));
				else if (entityType == EntityType.SILVERFISH) TrappedNewbieAdvancements.KILL_10K_SILVERFISHES.modifySimpleProgress(player, p -> p.setGained(statValue));
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
				if (statValue >= 5) TrappedNewbieAdvancements.FISH_5.awardAllCriteria(player);
				if (statValue >= 25) TrappedNewbieAdvancements.FISH_25.awardAllCriteria(player);
				if (statValue >= 100) TrappedNewbieAdvancements.FISH_100.awardAllCriteria(player);
				if (statValue >= 250) TrappedNewbieAdvancements.FISH_250.awardAllCriteria(player);
				if (statValue >= 500) TrappedNewbieAdvancements.FISH_500.awardAllCriteria(player);
				if (statValue >= 1000) TrappedNewbieAdvancements.FISH_1K.awardAllCriteria(player);
				if (statValue >= 2500) TrappedNewbieAdvancements.FISH_2500.awardAllCriteria(player);
				if (statValue >= 5000) TrappedNewbieAdvancements.FISH_5K.awardAllCriteria(player);
				if (statValue >= 10_000) TrappedNewbieAdvancements.FISH_10K.awardAllCriteria(player);
				if (statValue >= 50_000) TrappedNewbieAdvancements.FISH_50K.awardAllCriteria(player);
			}
			case MINE_BLOCK -> {
				Material type = event.getMaterial();
				if (type == Material.SUSPICIOUS_SAND || type == Material.SUSPICIOUS_GRAVEL) {
					TrappedNewbieAdvancements.BREAK_A_SUSPICIOUS_BLOCK.awardAllCriteria(player);
					TrappedNewbieAdvancements.BREAK_A_STACK_OF_SUSPICIOUS_BLOCKS.modifySimpleProgress(player, p -> p.setGained(Math.max(statValue, p.getGained())));
				}
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
		trackMovementStat(player, Statistic.MINECART_ONE_CM, 500 * 1000 * 100, TrappedNewbieAdvancements.MINECART_500KM);
		trackMovementStat(player, Statistic.MINECART_ONE_CM, 5000 * 1000 * 100, TrappedNewbieAdvancements.MINECART_5000KM);
		trackMovementStat(player, Statistic.MINECART_ONE_CM, 15000 * 1000 * 100, TrappedNewbieAdvancements.MINECART_15000KM);
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
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 100 * 100, TrappedNewbieAdvancements.STRIDER_100M);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.STRIDER_1KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_10KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 25 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_25KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_50KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_100KM);
		trackMovementStat(player, Statistic.STRIDER_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.STRIDER_250KM);
		trackMovementStat(player, Statistic.HAPPY_GHAST_ONE_CM, 100 * 100, TrappedNewbieAdvancements.HAPPY_GHAST_100M);
		trackMovementStat(player, Statistic.HAPPY_GHAST_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.HAPPY_GHAST_1KM);
		trackMovementStat(player, Statistic.HAPPY_GHAST_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.HAPPY_GHAST_10KM);
		trackMovementStat(player, Statistic.HAPPY_GHAST_ONE_CM, 25 * 1000 * 100, TrappedNewbieAdvancements.HAPPY_GHAST_25KM);
		trackMovementStat(player, Statistic.HAPPY_GHAST_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.HAPPY_GHAST_50KM);
		trackMovementStat(player, Statistic.HAPPY_GHAST_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.HAPPY_GHAST_100KM);
		trackMovementStat(player, Statistic.HAPPY_GHAST_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.HAPPY_GHAST_250KM);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.HORSE_1KM);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.HORSE_10KM);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 25 * 1000 * 100, TrappedNewbieAdvancements.HORSE_25KM);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.HORSE_50KM);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.HORSE_100KM);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.HORSE_250KM);
		trackMovementStat(player, Statistic.HORSE_ONE_CM, 500 * 1000 * 100, TrappedNewbieAdvancements.HORSE_500KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 100 * 100, TrappedNewbieAdvancements.PIG_100M);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 1000 * 100, TrappedNewbieAdvancements.PIG_1KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 10 * 1000 * 100, TrappedNewbieAdvancements.PIG_10KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 25 * 1000 * 100, TrappedNewbieAdvancements.PIG_25KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 50 * 1000 * 100, TrappedNewbieAdvancements.PIG_50KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 100 * 1000 * 100, TrappedNewbieAdvancements.PIG_100KM);
		trackMovementStat(player, Statistic.PIG_ONE_CM, 250 * 1000 * 100, TrappedNewbieAdvancements.PIG_250KM);
		int dayTime = 20 * 60 * 60;
		trackMovementStat(player, Statistic.PLAY_ONE_MINUTE, dayTime, TrappedNewbieAdvancements.PLAY_1D);
		trackMovementStat(player, Statistic.PLAY_ONE_MINUTE, 100 * dayTime, TrappedNewbieAdvancements.PLAY_100D);
		trackMovementStat(player, Statistic.PLAY_ONE_MINUTE, 365 * dayTime, TrappedNewbieAdvancements.PLAY_365D);
		trackMovementStat(player, Statistic.PLAY_ONE_MINUTE, 1000 * dayTime, TrappedNewbieAdvancements.PLAY_1000D);
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
