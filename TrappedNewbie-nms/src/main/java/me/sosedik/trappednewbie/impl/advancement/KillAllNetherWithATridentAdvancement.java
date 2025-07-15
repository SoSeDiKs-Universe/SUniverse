package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
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
public class KillAllNetherWithATridentAdvancement extends BaseAdvancement {

	public KillAllNetherWithATridentAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(UtilizerTags.LAND_ANIMALS.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (EntityType type : UtilizerTags.NATIVE_NETHER_MOBS.getValues()) {
			requirements.add(List.of(type.key().value()));
			triggerDatas.add(triggerData(type));
		}
		for (EntityType type : UtilizerTags.NON_NATIVE_NETHER_MOBS.getValues()) {
			if (type == EntityType.ENDERMAN) continue;

			requirements.add(List.of(type.key().value()));
			triggerDatas.add(triggerDataWithDimension(type));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static PlayerKilledEntityTriggerData triggerData(EntityType type) {
		return playerKilledEntity(type.key().value())
				.withEntity(entity -> entity.withEntityType(type))
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(entity -> entity
						.withEntityType(EntityType.TRIDENT)
					)
				);
	}

	private static PlayerKilledEntityTriggerData triggerDataWithDimension(EntityType type) {
		PlayerKilledEntityTriggerData triggerData = triggerData(type);
		Objects.requireNonNull(triggerData.getEntityTriggerConditions()).getFirst()
				.withLocation(loc -> loc.withDimension(World.Environment.NETHER));
		return triggerData;
	}

}
