package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

// MCCheck: 1.21.10, new hostile mobs that have tnt immunity
@NullMarked
public class BlowUpAllMonstersWithTNTAdvancement extends BaseAdvancement {

	public BlowUpAllMonstersWithTNTAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(UtilizerTags.HOSTILE_MONSTERS.getValues().size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (EntityType entityType : UtilizerTags.HOSTILE_MONSTERS.getValues()) {
			if (shouldSkip(entityType)) continue;

			requirements.add(List.of(entityType.key().value()));
			triggerDatas.add(hurt(entityType));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static boolean shouldSkip(EntityType entityType) {
		return entityType == EntityType.WITHER
			|| entityType == EntityType.ENDER_DRAGON;
	}

	private static PlayerKilledEntityTriggerData hurt(EntityType entityType) {
		return VanillaTriggerData.playerKilledEntity(entityType.key().value())
			.withEntity(entity -> entity.withEntityType(entityType))
			.withDamage(source -> source
				.withTag(DamageTypeTagKeys.IS_EXPLOSION, true)
				.withDirectEntity(entity -> entity.withEntityType(EntityType.TNT))
			);
	}

}
