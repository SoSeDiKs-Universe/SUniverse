package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;

@NullMarked
public class WearSilentArmorAdvancement extends BaseAdvancement {

	public WearSilentArmorAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		String rawComponent = """
			{
				"predicates": {
					"minecraft:trim": {
						"pattern": "%s"
					}
				}
			}
			""".formatted(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).getKeyOrThrow(TrimPattern.SILENCE));

		return RequiredAdvancementProgress.vanilla(inventoryChanged()
			.withPlayer(player -> player
				.withEquipment(equipment -> equipment
					.withHelmet(ItemTriggerCondition.builder()
						.withRawComponents(rawComponent)
					)
					.withChestplate(ItemTriggerCondition.builder()
						.withRawComponents(rawComponent)
					)
					.withLeggings(ItemTriggerCondition.builder()
						.withRawComponents(rawComponent)
					)
					.withBoots(ItemTriggerCondition.builder()
						.withRawComponents(rawComponent)
					)
				)
			)
		);
	}

}
