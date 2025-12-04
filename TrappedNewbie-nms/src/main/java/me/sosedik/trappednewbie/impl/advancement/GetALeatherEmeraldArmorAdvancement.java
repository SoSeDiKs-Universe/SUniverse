package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;

@NullMarked
public class GetALeatherEmeraldArmorAdvancement extends BaseAdvancement {

	public GetALeatherEmeraldArmorAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		String rawComponent = """
			{
				"components": {
					"minecraft:dyed_color": %s
				},
				"predicates": {
					"minecraft:trim": {
						"material": "%s"
					}
				}
			}
			""".formatted(
				DyeColor.LIME.getColor().asRGB(),
				RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKeyOrThrow(TrimMaterial.EMERALD)
			);

		return RequiredAdvancementProgress.vanilla(inventoryChanged()
			.withPlayer(player -> player
				.withEquipment(equipment -> equipment
					.withHelmet(ItemTriggerCondition.of(Material.LEATHER_HELMET).withRawComponents(rawComponent))
					.withChestplate(ItemTriggerCondition.of(Material.LEATHER_CHESTPLATE).withRawComponents(rawComponent))
					.withLeggings(ItemTriggerCondition.of(Material.LEATHER_LEGGINGS).withRawComponents(rawComponent))
					.withBoots(ItemTriggerCondition.of(Material.LEATHER_BOOTS).withRawComponents(rawComponent))
				)
			));
	}

}
