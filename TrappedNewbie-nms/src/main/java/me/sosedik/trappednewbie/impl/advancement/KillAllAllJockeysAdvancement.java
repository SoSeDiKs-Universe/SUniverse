package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.Registry;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

// MCCheck: 1.21.10, new jockeys, entity nbt tags
@NullMarked
public class KillAllAllJockeysAdvancement extends BaseAdvancement {

	public KillAllAllJockeysAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();

		RegistryAccess.registryAccess().getRegistry(RegistryKey.CHICKEN_VARIANT).forEach(chickenVariant -> {
			Registry.VILLAGER_TYPE.iterator().forEachRemaining(villagerType -> {
				villagerRider(requirements, triggerDatas, villagerType, null, chickenVariant);
				Registry.VILLAGER_PROFESSION.iterator().forEachRemaining(profession -> {
					villagerRider(requirements, triggerDatas, villagerType, profession, chickenVariant);
				});
			});
		});

		RegistryAccess.registryAccess().getRegistry(RegistryKey.CHICKEN_VARIANT).forEach(chickenVariant -> {
			chickenRider(requirements, triggerDatas, EntityType.ZOMBIE, chickenVariant, true, false);
			chickenRider(requirements, triggerDatas, EntityType.ZOMBIE, chickenVariant, true, true);
			chickenRider(requirements, triggerDatas, EntityType.HUSK, chickenVariant, true, false);
			chickenRider(requirements, triggerDatas, EntityType.HUSK, chickenVariant, true, true);
			chickenRider(requirements, triggerDatas, EntityType.DROWNED, chickenVariant, null, true);
			chickenRider(requirements, triggerDatas, EntityType.DROWNED, chickenVariant, null, false);
			chickenRider(requirements, triggerDatas, EntityType.ZOMBIFIED_PIGLIN, chickenVariant, null, true);
			chickenRider(requirements, triggerDatas, EntityType.ZOMBIFIED_PIGLIN, chickenVariant, null, false);
			chickenRider(requirements, triggerDatas, EntityType.WITCH, chickenVariant, null, true);
			chickenRider(requirements, triggerDatas, EntityType.WITCH, chickenVariant, null, false);
		});

		simpleRider(requirements, triggerDatas, EntityType.ZOMBIFIED_PIGLIN, EntityType.STRIDER, true, null);
		simpleRider(requirements, triggerDatas, EntityType.ZOMBIFIED_PIGLIN, EntityType.STRIDER, false, null);
		simpleRider(requirements, triggerDatas, EntityType.STRIDER, EntityType.STRIDER, true, null);
		simpleRider(requirements, triggerDatas, EntityType.STRIDER, EntityType.STRIDER, false, null);

		simpleRider(requirements, triggerDatas, EntityType.PIGLIN, EntityType.HOGLIN);
		simpleRider(requirements, triggerDatas, EntityType.PIGLIN, EntityType.PIGLIN);

		simpleRider(requirements, triggerDatas, EntityType.SKELETON, EntityType.SPIDER);
		simpleRider(requirements, triggerDatas, EntityType.STRAY, EntityType.SPIDER);

		simpleRider(requirements, triggerDatas, EntityType.SKELETON, EntityType.SKELETON_HORSE);
		simpleRider(requirements, triggerDatas, EntityType.STRAY, EntityType.SKELETON_HORSE);

		simpleRider(requirements, triggerDatas, EntityType.EVOKER, EntityType.RAVAGER);
		simpleRider(requirements, triggerDatas, EntityType.PILLAGER, EntityType.RAVAGER);
		simpleRider(requirements, triggerDatas, EntityType.VINDICATOR, EntityType.RAVAGER);

		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static void villagerRider(List<List<String>> requirements, List<VanillaTriggerData<?>> triggerDatas, Villager.Type villagerType, Villager.@Nullable Profession profession, Chicken.Variant chickenVariant) {
		EntityType[] entityTypes = {EntityType.VILLAGER, EntityType.ZOMBIE_VILLAGER};
		for (EntityType entityType : entityTypes) {
			if (profession == null) {
				villagerRider(requirements, triggerDatas, entityType, villagerType, null, chickenVariant, true, false);
				villagerRider(requirements, triggerDatas, entityType, villagerType, null, chickenVariant, true, true);
			} else {
				villagerRider(requirements, triggerDatas, entityType, villagerType, profession, chickenVariant, false, false);
			}
		}
	}

	private static void villagerRider(List<List<String>> requirements, List<VanillaTriggerData<?>> triggerDatas, EntityType riderType, Villager.Type villagerType, Villager.@Nullable Profession profession, Chicken.Variant chickenVariant, @Nullable Boolean babyRider, @Nullable Boolean babyVehicle) {
		String ridingKey = (Boolean.TRUE.equals(babyRider) ? "baby_" : "") + riderType.key().value() + "_" + villagerType.key().value() + (profession == null ? "" : "_" + profession.key().value()) + "_riding_" + (Boolean.TRUE.equals(babyRider) ? "baby_" : "") + chickenVariant.key().value() + "_chicken";
		String bearingKey = (Boolean.TRUE.equals(babyVehicle) ? "baby_" : "") + chickenVariant.key().value() + "_chicken_bearing_" + (Boolean.TRUE.equals(babyRider) ? "baby_" : "") + riderType.key().value() + "_" + villagerType.key().value() + (profession == null ? "" : "_" + profession.key().value());
		String riderNbt = profession == null ? "{VillagerData:{type:\"%s\"}}".formatted(villagerType.key()) : "{VillagerData:{profession:\"%s\",type:\"%s\"}}".formatted(profession.key(), villagerType.key());
		String vehicleNbt = "{variant:\"%s\"}".formatted(chickenVariant.key());
		requirements.add(List.of(ridingKey, bearingKey));
		triggerDatas.add(vehicleTriggerData(ridingKey, riderType, EntityType.CHICKEN, babyRider, babyVehicle, riderNbt, vehicleNbt));
		triggerDatas.add(riderTriggerData(ridingKey, riderType, EntityType.CHICKEN, babyRider, babyVehicle, riderNbt, vehicleNbt));
	}

	private static void chickenRider(List<List<String>> requirements, List<VanillaTriggerData<?>> triggerDatas, EntityType riderType, Chicken.Variant chickenVariant, @Nullable Boolean babyRider, @Nullable Boolean babyVehicle) {
		String ridingKey = (Boolean.TRUE.equals(babyRider) ? "baby_" : "") + riderType.key().value() + "_riding_" + (Boolean.TRUE.equals(babyVehicle) ? "baby_" : "") + chickenVariant.key().value() + "_chicken";
		String bearingKey = (Boolean.TRUE.equals(babyVehicle) ? "baby_" : "") + chickenVariant.key().value() + "_chicken" + "_bearing_" + (Boolean.TRUE.equals(babyRider) ? "baby_" : "") + riderType.key().value();
		String vehicleNbt = "{variant:\"%s\"}".formatted(chickenVariant.key());
		requirements.add(List.of(ridingKey, bearingKey));
		triggerDatas.add(vehicleTriggerData(ridingKey, riderType, EntityType.CHICKEN, babyRider, babyVehicle, null, vehicleNbt));
		triggerDatas.add(riderTriggerData(bearingKey, riderType, EntityType.CHICKEN, babyRider, babyVehicle, null, vehicleNbt));
	}

	private static void simpleRider(List<List<String>> requirements, List<VanillaTriggerData<?>> triggerDatas, EntityType riderType, EntityType vehicleType) {
		simpleRider(requirements, triggerDatas, riderType, vehicleType, null, null);
	}

	private static void simpleRider(List<List<String>> requirements, List<VanillaTriggerData<?>> triggerDatas, EntityType riderType, EntityType vehicleType, @Nullable Boolean babyRider, @Nullable Boolean babyVehicle) {
		String ridingKey = (Boolean.TRUE.equals(babyRider) ? "baby_" : "") + riderType.key().value() + "_riding_" + (Boolean.TRUE.equals(babyVehicle) ? "baby_" : "") + vehicleType.key().value();
		String bearingKey = (Boolean.TRUE.equals(babyVehicle) ? "baby_" : "") + vehicleType.key().value() + "_bearing_" + (Boolean.TRUE.equals(babyRider) ? "baby_" : "") + riderType.key().value();
		requirements.add(List.of(ridingKey, bearingKey));
		triggerDatas.add(vehicleTriggerData(ridingKey, riderType, vehicleType, babyRider, babyVehicle, null, null));
		triggerDatas.add(riderTriggerData(bearingKey, riderType, vehicleType, babyRider, babyVehicle, null, null));
	}

	private static PlayerKilledEntityTriggerData vehicleTriggerData(String key, EntityType riderType, EntityType vehicleType, @Nullable Boolean babyRider, @Nullable Boolean babyVehicle, @Nullable String riderNbt, @Nullable String vehicleNbt) {
		return playerKilledEntity(key)
				.withEntity(entity -> entity
					.withEntityType(riderType)
					.withState(state -> state.baby(babyRider))
					.withNbt(riderNbt)
					.withVehicle(vehicle -> vehicle
						.withEntityType(vehicleType)
						.withState(state -> state.baby(babyVehicle))
						.withNbt(vehicleNbt)
					)
				);
	}

	private static PlayerKilledEntityTriggerData riderTriggerData(String key, EntityType riderType, EntityType vehicleType, @Nullable Boolean babyRider, @Nullable Boolean babyVehicle, @Nullable String riderNbt, @Nullable String vehicleNbt) {
		return playerKilledEntity(key)
				.withEntity(entity -> entity
					.withEntityType(vehicleType)
					.withState(state -> state.baby(babyVehicle))
					.withNbt(vehicleNbt)
					.withPassenger(passenger -> passenger
						.withEntityType(riderType)
						.withState(state -> state.baby(babyRider))
						.withNbt(riderNbt)
					)
				);
	}

}
