package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;

@NullMarked
public class WearArmorWithSameTrimsAdvancement extends BaseAdvancement {

	public WearArmorWithSameTrimsAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		Registry<TrimMaterial> trimMaterialRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);
		Function<TrimMaterial, String> rawComponent = trimMaterial -> """
			{
				"predicates": {
					"minecraft:trim": {
						"material": "%s"
					}
				}
			}
			""".formatted(trimMaterialRegistry.getKeyOrThrow(trimMaterial));

		return RequiredAdvancementProgress.vanillaAny(
			trimMaterialRegistry.stream()
				.filter(trimMaterial -> NamespacedKey.MINECRAFT.equals(trimMaterialRegistry.getKeyOrThrow(trimMaterial).namespace()))
				.map(trimMaterial -> inventoryChanged(trimMaterialRegistry.getKeyOrThrow(trimMaterial).getKey())
					.withPlayer(player -> player
						.withEquipment(equipment -> equipment
							.withHelmet(ItemTriggerCondition.builder()
								.withRawComponents(rawComponent.apply(trimMaterial))
							)
							.withChestplate(ItemTriggerCondition.builder()
								.withRawComponents(rawComponent.apply(trimMaterial))
							)
							.withLeggings(ItemTriggerCondition.builder()
								.withRawComponents(rawComponent.apply(trimMaterial))
							)
							.withBoots(ItemTriggerCondition.builder()
								.withRawComponents(rawComponent.apply(trimMaterial))
							)
						)
					)
				)
				.toList()
		);
	}

}
