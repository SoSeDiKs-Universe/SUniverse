package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.trappednewbie.listener.advancement.dedicated.KillAMobWearingItsHeadAdvancement;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

// MCCheck: 1.21.10, new mob heads
@NullMarked
public class KillAMobThatIsWearingItsHeadAdvancement extends BaseAdvancement {

	public KillAMobThatIsWearingItsHeadAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<String> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		KillAMobWearingItsHeadAdvancement.getHeads().forEach((entityType, headType) -> {
			requirements.add(entityType.key().value());
			triggerDatas.add(triggerData(entityType, headType));
		});
		return RequiredAdvancementProgress.vanilla(List.of(requirements), triggerDatas);
	}

	private static PlayerKilledEntityTriggerData triggerData(EntityType entityType, Material headType) {
		return playerKilledEntity(entityType.key().value())
				.withEntity(entity -> entity
					.withEntityType(entityType)
					.withEquipment(equipment -> equipment
						.withHelmet(ItemTriggerCondition.of(headType))
					)
				);
	}

}
