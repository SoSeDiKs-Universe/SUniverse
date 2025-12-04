package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.PlayerTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.context.AllOfTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.context.AnyOfTriggerCondition;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;

@NullMarked
public class GetALeatherNetheriteArmorAdvancement extends BaseAdvancement {

	public GetALeatherNetheriteArmorAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		Registry<TrimPattern> trimPatternRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN);
		List<TrimPattern> trimPatterns = trimPatternRegistry.stream()
			.filter(trimPattern -> NamespacedKey.MINECRAFT.equals(trimPatternRegistry.getKeyOrThrow(trimPattern).namespace()))
			.toList();
		List<String> rawComponents = new ArrayList<>(trimPatterns.size());
		trimPatterns.forEach(trimPattern -> rawComponents.add(
			"""
			{
				"components": {
					"minecraft:dyed_color": %s,
					"minecraft:trim": {
						"material": "%s",
						"pattern": "%s"
					}
				}
			}
			""".formatted(
				DyeColor.BLACK.getColor().asRGB(),
				RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKeyOrThrow(TrimMaterial.NETHERITE),
				trimPatternRegistry.getKeyOrThrow(trimPattern)
			)
		));

		return RequiredAdvancementProgress.vanilla(inventoryChanged()
			.withPlayer(
				new AllOfTriggerCondition(
					new AnyOfTriggerCondition(
						rawComponents.stream().map(rawComponent ->
							PlayerTriggerCondition.builder()
								.withEquipment(equipment -> equipment
									.withHelmet(ItemTriggerCondition.of(Material.LEATHER_HELMET).withRawComponents(rawComponent))
								)
						).toList()
					),
					new AnyOfTriggerCondition(
						rawComponents.stream().map(rawComponent ->
							PlayerTriggerCondition.builder()
								.withEquipment(equipment -> equipment
									.withChestplate(ItemTriggerCondition.of(Material.LEATHER_CHESTPLATE).withRawComponents(rawComponent))
								)
						).toList()
					),
					new AnyOfTriggerCondition(
						rawComponents.stream().map(rawComponent ->
							PlayerTriggerCondition.builder()
								.withEquipment(equipment -> equipment
									.withLeggings(ItemTriggerCondition.of(Material.LEATHER_LEGGINGS).withRawComponents(rawComponent))
								)
						).toList()
					),
					new AnyOfTriggerCondition(
						rawComponents.stream().map(rawComponent ->
							PlayerTriggerCondition.builder()
								.withEquipment(equipment -> equipment
									.withBoots(ItemTriggerCondition.of(Material.LEATHER_BOOTS).withRawComponents(rawComponent))
								)
						).toList()
					)
				)
			)
		);
	}

}
