package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.EntityHurtPlayerTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// MCCheck: 1.21.7, new hostile mobs (that can have attack damage deflected)
@NullMarked
public class MasterShieldsmanAdvancement extends BaseAdvancement {

	private static final Set<EntityType> HOSTILE_MOBS = Set.of(
		EntityType.CAVE_SPIDER, EntityType.SPIDER, EntityType.ZOMBIFIED_PIGLIN,
		EntityType.ENDERMAN, EntityType.BLAZE, EntityType.CREEPER, EntityType.GHAST,
		EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.SHULKER, EntityType.SILVERFISH,
		EntityType.SKELETON, EntityType.SLIME, EntityType.STRAY, EntityType.VINDICATOR,
		EntityType.WITHER_SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER,
		EntityType.PHANTOM, EntityType.DROWNED, EntityType.PILLAGER, EntityType.RAVAGER,
		EntityType.ENDERMITE, EntityType.VEX, EntityType.PIGLIN, EntityType.HOGLIN,
		EntityType.ZOGLIN, EntityType.PIGLIN_BRUTE, EntityType.ENDER_DRAGON,
		EntityType.WARDEN, EntityType.BREEZE, EntityType.BOGGED, EntityType.CREAKING
	);

	public MasterShieldsmanAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>(HOSTILE_MOBS.size());
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (EntityType entityType : HOSTILE_MOBS) {
			requirements.add(List.of(entityType.key().value()));
			triggerDatas.add(hurt(entityType));
		}
		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static EntityHurtPlayerTriggerData hurt(EntityType entityType) {
		return VanillaTriggerData.entityHurtPlayer(entityType.key().value())
			.withDamage(damage -> damage.blocked().withSourceEntity(entity -> entity.withEntityType(entityType)));
	}

}
