package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.EntityTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.context.ContextAwareCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

@NullMarked
public class KillHostileEndMobsAdvancement extends BaseAdvancement {

	public KillHostileEndMobsAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(UtilizerTags.END_HOSTILE_MOBS.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (EntityType type : UtilizerTags.END_HOSTILE_MOBS.getValues()) {
			requirements.add(List.of(type.key().value()));
			triggerDatas.add(shouldRequireDimension(type) ? triggerDataWithDimension(type) : triggerData(type));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static boolean shouldRequireDimension(EntityType type) {
		return type == EntityType.ENDERMAN;
	}

	private static PlayerKilledEntityTriggerData triggerData(EntityType type) {
		return playerKilledEntity(type.key().value())
				.withEntity(entity -> entity.withEntityType(type));
	}

	private static PlayerKilledEntityTriggerData triggerDataWithDimension(EntityType type) {
		PlayerKilledEntityTriggerData triggerData = triggerData(type);
		ContextAwareCondition first = Objects.requireNonNull(triggerData.getEntityTriggerConditions()).getFirst();
		((EntityTriggerCondition) first).withLocation(loc -> loc.withDimension(World.Environment.THE_END));
		return triggerData;
	}

}
