package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

// MCCheck: 1.21.8, new jockeys
@NullMarked
public class KillAllJockeysAdvancement extends BaseAdvancement {

	public KillAllJockeysAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();

		simpleRider(requirements, triggerDatas, EntityType.ZOMBIE, EntityType.CHICKEN);
		simpleRider(requirements, triggerDatas, EntityType.HUSK, EntityType.CHICKEN);
		simpleRider(requirements, triggerDatas, EntityType.DROWNED, EntityType.CHICKEN);
		simpleRider(requirements, triggerDatas, EntityType.ZOMBIFIED_PIGLIN, EntityType.CHICKEN);
		simpleRider(requirements, triggerDatas, EntityType.ZOMBIE_VILLAGER, EntityType.CHICKEN);
		simpleRider(requirements, triggerDatas, EntityType.VILLAGER, EntityType.CHICKEN);
		simpleRider(requirements, triggerDatas, EntityType.WITCH, EntityType.CHICKEN);

		simpleRider(requirements, triggerDatas, EntityType.ZOMBIFIED_PIGLIN, EntityType.STRIDER);
		simpleRider(requirements, triggerDatas, EntityType.STRIDER, EntityType.STRIDER);

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

	private static void simpleRider(List<List<String>> requirements, List<VanillaTriggerData<?>> triggerDatas, EntityType riderType, EntityType vehicleType) {
		String ridingKey = riderType.key().value() + "_riding_" + vehicleType.key().value();
		String bearingKey = vehicleType.key().value() + "_bearing_" + riderType.key().value();
		requirements.add(List.of(ridingKey, bearingKey));
		triggerDatas.add(vehicleTriggerData(ridingKey, riderType, vehicleType));
		triggerDatas.add(riderTriggerData(bearingKey, riderType, vehicleType));
	}

	private static PlayerKilledEntityTriggerData vehicleTriggerData(String key, EntityType riderType, EntityType vehicleType) {
		return playerKilledEntity(key)
				.withEntity(entity -> entity
					.withEntityType(riderType)
					.withVehicle(vehicle -> vehicle
						.withEntityType(vehicleType)
					)
				);
	}

	private static PlayerKilledEntityTriggerData riderTriggerData(String key, EntityType riderType, EntityType vehicleType) {
		return playerKilledEntity(key)
				.withEntity(entity -> entity
					.withEntityType(vehicleType)
					.withPassenger(passenger -> passenger
						.withEntityType(riderType)
					)
				);
	}

}
