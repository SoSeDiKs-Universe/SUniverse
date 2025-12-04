package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;

@NullMarked
public class WearARealNetheriteArmorAdvancement extends BaseAdvancement {

	public WearARealNetheriteArmorAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		String rawComponent = """
			{
				"predicates": {
					"minecraft:trim": {
						"material": "%s"
					}
				}
			}
			""".formatted(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKeyOrThrow(TrimMaterial.NETHERITE));

		return RequiredAdvancementProgress.vanilla(inventoryChanged()
			.withPlayer(player -> player
				.withEquipment(equipment -> equipment
					.withHelmet(ItemTriggerCondition.of(Material.NETHERITE_HELMET)
						.withRawComponents(rawComponent)
					)
					.withChestplate(ItemTriggerCondition.of(Material.NETHERITE_CHESTPLATE)
						.withRawComponents(rawComponent)
					)
					.withLeggings(ItemTriggerCondition.of(Material.NETHERITE_LEGGINGS)
						.withRawComponents(rawComponent)
					)
					.withBoots(ItemTriggerCondition.of(Material.NETHERITE_BOOTS)
						.withRawComponents(rawComponent)
					)
				)
			));
	}

}
