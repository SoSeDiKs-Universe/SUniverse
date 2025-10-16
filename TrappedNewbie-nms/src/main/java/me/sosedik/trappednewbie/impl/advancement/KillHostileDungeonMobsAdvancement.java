package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.EntityTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.RaiderTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.context.ContextAwareCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

@NullMarked
public class KillHostileDungeonMobsAdvancement extends BaseAdvancement {

	public KillHostileDungeonMobsAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(UtilizerTags.DUNGEON_HOSTILE_MOBS.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (EntityType type : UtilizerTags.DUNGEON_HOSTILE_MOBS.getValues()) {
			requirements.add(List.of(type.key().value()));
			triggerDatas.add(isRaidMob(type) ? triggerDataNonRaider(type) : triggerData(type));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static boolean isRaidMob(EntityType type) {
		return Tag.ENTITY_TYPES_RAIDERS.isTagged(type);
	}

	private static PlayerKilledEntityTriggerData triggerData(EntityType type) {
		return playerKilledEntity(type.key().value())
				.withEntity(entity -> entity.withEntityType(type));
	}

	private static PlayerKilledEntityTriggerData triggerDataNonRaider(EntityType type) {
		PlayerKilledEntityTriggerData triggerData = triggerData(type);
		ContextAwareCondition first = Objects.requireNonNull(triggerData.getEntityTriggerConditions()).getFirst();
		((EntityTriggerCondition) first).withSubPredicate(new RaiderTriggerCondition(false, false));
		return triggerData;
	}

}
