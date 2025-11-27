package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.EntityStateTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

@NullMarked
public class KillAllBabyZombiesAdvancement extends BaseAdvancement {

	public KillAllBabyZombiesAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(UtilizerTags.HUMAN_LIKE_ZOMBIES.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (EntityType type : UtilizerTags.HUMAN_LIKE_ZOMBIES.getValues()) {
			if (type.getEntityClass() == null) continue;
			if (!Ageable.class.isAssignableFrom(type.getEntityClass())) continue;

			requirements.add(List.of(type.key().value()));
			triggerDatas.add(triggerData(type));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static PlayerKilledEntityTriggerData triggerData(EntityType type) {
		return playerKilledEntity(type.key().value())
				.withEntity(entity -> entity
					.withEntityType(type)
					.withState(EntityStateTriggerCondition::baby)
				);
	}

}
