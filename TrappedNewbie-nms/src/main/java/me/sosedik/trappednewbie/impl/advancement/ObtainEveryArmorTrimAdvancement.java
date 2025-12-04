package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;

@NullMarked
public class ObtainEveryArmorTrimAdvancement extends BaseAdvancement {

	public ObtainEveryArmorTrimAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder, Collection<Material> items) {
		super(advancementBuilder.requiredProgress(getProgress(items)));
	}

	private static RequiredAdvancementProgress getProgress(Collection<Material> items) {
		Registry<TrimMaterial> trimMaterialRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);
		Registry<TrimPattern> trimPatternRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN);

		List<List<String>> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();

		items.forEach(type ->
			trimMaterialRegistry.forEach(trimMaterial -> {
				NamespacedKey materialKey = trimMaterialRegistry.getKeyOrThrow(trimMaterial);
				if (!NamespacedKey.MINECRAFT.equals(materialKey.namespace())) return;

				trimPatternRegistry.forEach(trimPattern -> {
					NamespacedKey patternKey = trimPatternRegistry.getKeyOrThrow(trimPattern);
					if (!NamespacedKey.MINECRAFT.equals(patternKey.namespace())) return;

					String key = type.key().value() + "_" + materialKey.value() + "_" + patternKey.value();
					requirements.add(List.of(key));
					triggerDatas.add(triggerData(key, type, materialKey.asString(), patternKey.asString()));
				});
			})
		);

		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static VanillaTriggerData<?> triggerData(String key, Material type, String trimMaterial, String trimPattern) {
		return inventoryChanged(key)
			.withItems(ItemTriggerCondition.of(type)
				.withRawComponents(
					"""
					{
						"components": {
							"minecraft:trim": {
								"material": "%s",
								"pattern": "%s"
							}
						}
					}
					""".formatted(trimMaterial, trimPattern)
				)
			);
	}

}
