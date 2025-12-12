package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.EntityHurtPlayerTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

// MCCheck: 1.21.11, new hostile mobs that can't have attack damage deflected
@NullMarked
public class MasterShieldsmanAdvancement extends BaseAdvancement {

	public MasterShieldsmanAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>();
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
			|| entityType == EntityType.WITCH
			|| entityType == EntityType.ENDERMITE
			|| entityType == EntityType.EVOKER
			|| entityType == EntityType.GUARDIAN
			|| entityType == EntityType.ELDER_GUARDIAN
			|| entityType == EntityType.ZOMBIE_HORSE
			|| entityType == EntityType.SKELETON_HORSE
			|| entityType == EntityType.CAMEL_HUSK;
	}

	private static EntityHurtPlayerTriggerData hurt(EntityType entityType) {
		return VanillaTriggerData.entityHurtPlayer(entityType.key().value())
			.withDamage(damage -> damage.blocked().withSourceEntity(entity -> entity.withEntityType(entityType)));
	}

}
