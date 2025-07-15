package me.sosedik.trappednewbie.impl.advancement;

import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GiveWindMaceToAFoxAdvancement extends BaseAdvancement {

	public GiveWindMaceToAFoxAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		return RequiredAdvancementProgress.vanilla(REQUIREMENT);
	}

	private static final String REQUIREMENT = """
			{
				"trigger": "minecraft:thrown_item_picked_up_by_entity",
				"conditions": {
					"item": {
						"items": "minecraft:mace",
						"predicates": {
							"minecraft:enchantments": [
								{
									"enchantments": "minecraft:wind_burst",
									"levels": 3
								}
							]
						}
					},
					"entity": {
						"type": "minecraft:fox"
					}
				}
			}
			""";

}
