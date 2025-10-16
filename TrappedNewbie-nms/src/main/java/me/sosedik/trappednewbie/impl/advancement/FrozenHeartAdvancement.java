package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.PlayerTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.context.AnyOfTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.context.WeatherTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.util.BiomeTags;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

@NullMarked
public class FrozenHeartAdvancement extends BaseAdvancement {

	public FrozenHeartAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<String> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();
		for (PotionType potionType : new PotionType[] {PotionType.SLOWNESS, PotionType.LONG_SLOWNESS, PotionType.STRONG_SLOWNESS}) {
			requirements.add(potionType.key().value());
			triggerDatas.add(triggerData(potionType));
		}
		return RequiredAdvancementProgress.vanilla(List.of(requirements), triggerDatas);
	}

	private static PlayerKilledEntityTriggerData triggerData(PotionType potionType) {
		return playerKilledEntity(potionType.key().value())
				.withEntity(entity -> entity.withEntityType(EntityType.STRAY))
				.withDamage(damage -> damage
					.withDirectEntity(entity -> entity
						.withEntityType(EntityType.ARROW)
						.withNbt("{item:{components:{\"%s\":{potion:\"%s\"}}}}".formatted(DataComponentTypes.POTION_CONTENTS.key(), potionType.key()))
					)
				)
				.withPlayer(
					new AnyOfTriggerCondition(
						new WeatherTriggerCondition(null, true),
						new WeatherTriggerCondition(true, null)
					),
					PlayerTriggerCondition.builder()
						.withNbt("{TicksFrozen:140}")
						.withLocation(loc -> loc
							.withBiome(List.of(BiomeTags.SNOWY.toArray(Biome[]::new)))
						)
						.withEffects(PotionEffectType.SLOWNESS, PotionEffectType.HUNGER)
				);
	}

}
