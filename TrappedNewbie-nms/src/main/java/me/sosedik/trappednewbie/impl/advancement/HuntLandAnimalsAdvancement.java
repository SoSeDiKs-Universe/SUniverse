package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

@NullMarked
public class HuntLandAnimalsAdvancement extends BaseAdvancement {

	public HuntLandAnimalsAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(UtilizerTags.LAND_ANIMALS.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (EntityType type : UtilizerTags.LAND_ANIMALS.getValues()) {
			requirements.add(List.of(type.key().value()));
			triggerDatas.add(triggerData(type));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static VanillaTriggerData<?> triggerData(EntityType type) {
		return playerKilledEntity(type.key().value())
				.withEntity(entity -> entity.withEntityType(type))
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
					.withDirectEntity(direct -> direct
						.withEntityType(Tag.ENTITY_TYPES_ARROWS)
						.withNbt("{weapon:{id:\"minecraft:bow\"}}")
					)
				);
	}

}
