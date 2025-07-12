package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerHurtEntity;

@NullMarked
public class AttackSquidInTheAirWithASnowballAdvancement extends BaseAdvancement {

	public AttackSquidInTheAirWithASnowballAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		return RequiredAdvancementProgress.vanilla(
			playerHurtEntity()
				.withEntity(entity -> entity
					.withEntityType(EntityType.GLOW_SQUID),
			entity -> entity
					.inverted()
					.withLocation(location -> location
						.withFluid(fluid -> fluid.withFluids(Tag.FLUIDS_WATER))
					)
				)
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(direct -> direct
							.withEntityType(EntityType.SNOWBALL)
							.withNbt("{Item:{id:\"%s\"}}".formatted(Material.SNOWBALL.key()))
						)
					)
				)
		);
	}

}
