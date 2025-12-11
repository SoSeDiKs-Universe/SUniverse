package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.util.MiscUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

@NullMarked
public class KillAllZombieVillagersAdvancement extends BaseAdvancement {

	public KillAllZombieVillagersAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (World.Environment environment : World.Environment.values()) {
			if (environment == World.Environment.CUSTOM) continue;

			Registry.VILLAGER_TYPE.stream()
				.filter(type -> NamespacedKey.MINECRAFT.equals(type.key().namespace()))
				.forEach(type -> {
					Registry.VILLAGER_PROFESSION.stream()
						.filter(profession -> NamespacedKey.MINECRAFT.equals(profession.key().namespace()))
						.forEach(profession -> {
							String key = MiscUtil.getDimensionKey(environment) + "_" + type.key().value() + "_" + profession.key().value();
							requirements.add(List.of(key));
							triggerDatas.add(triggerData(key, environment, type, profession, false));
						});
					String key = MiscUtil.getDimensionKey(environment) + "_" + type.key().value() + "_baby";
					requirements.add(List.of(key));
					triggerDatas.add(triggerData(key, environment, type, null, true));
				});
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static PlayerKilledEntityTriggerData triggerData(String key, World.Environment environment, Villager.Type type, Villager.@Nullable Profession profession, boolean baby) {
		return playerKilledEntity(key)
				.withEntity(entity -> entity
					.withEntityType(EntityType.ZOMBIE_VILLAGER)
					.withState(state -> state.baby(baby))
					.withLocation(loc -> loc.withDimension(environment))
					.withNbt(profession == null ? "{VillagerData:{type:\"%s\"}}".formatted(type.key()) : "{VillagerData:{profession:\"%s\",type:\"%s\"}}".formatted(profession.key(), type.key()))
				);
	}

}
