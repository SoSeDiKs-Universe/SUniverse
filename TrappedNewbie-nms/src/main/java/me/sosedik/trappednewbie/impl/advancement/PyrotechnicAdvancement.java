package me.sosedik.trappednewbie.impl.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

// MCCheck: 1.21.7, new firework shapes/effects
@NullMarked
public class PyrotechnicAdvancement extends BaseAdvancement {

	public PyrotechnicAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		JsonObject json = JsonParser.parseString(REQUIREMENTS).getAsJsonObject();
		List<List<String>> list = new ArrayList<>();
		json.keySet().forEach(key -> list.add(List.of(key)));
		return RequiredAdvancementProgress.vanillaCriteria(list, json);
	}

	private static final String REQUIREMENTS = """
			{
				"small_ball": {
					"trigger": "minecraft:inventory_changed",
					"conditions": {
						"items": [
							{
								"items": [
									"minecraft:firework_rocket"
								],
								"predicates": {
									"minecraft:fireworks": {
										"explosions": {
											"contains": [
												{
													"shape": "small_ball"
												}
											]
										}
									}
								}
							}
						]
					}
				},
				"large_ball": {
					"trigger": "minecraft:inventory_changed",
					"conditions": {
						"items": [
							{
								"items": [
									"minecraft:firework_rocket"
								],
								"predicates": {
									"minecraft:fireworks": {
										"explosions": {
											"contains": [
												{
													"shape": "large_ball"
												}
											]
										}
									}
								}
							}
						]
					}
				},
				"star": {
					"trigger": "minecraft:inventory_changed",
					"conditions": {
						"items": [
							{
								"items": [
									"minecraft:firework_rocket"
								],
								"predicates": {
									"minecraft:fireworks": {
										"explosions": {
											"contains": [
												{
													"shape": "star"
												}
											]
										}
									}
								}
							}
						]
					}
				},
				"creeper": {
					"trigger": "minecraft:inventory_changed",
					"conditions": {
						"items": [
							{
								"items": [
									"minecraft:firework_rocket"
								],
								"predicates": {
									"minecraft:fireworks": {
										"explosions": {
											"contains": [
												{
													"shape": "creeper"
												}
											]
										}
									}
								}
							}
						]
					}
				},
				"burst": {
					"trigger": "minecraft:inventory_changed",
					"conditions": {
						"items": [
							{
								"items": [
									"minecraft:firework_rocket"
								],
								"predicates": {
									"minecraft:fireworks": {
										"explosions": {
											"contains": [
												{
													"shape": "burst"
												}
											]
										}
									}
								}
							}
						]
					}
				},
				"flicker": {
					"trigger": "minecraft:inventory_changed",
					"conditions": {
						"items": [
							{
								"items": [
									"minecraft:firework_rocket"
								],
								"predicates": {
									"minecraft:fireworks": {
										"explosions": {
											"contains": [
												{
													"has_twinkle": true
												}
											]
										}
									}
								}
							}
						]
					}
				},
				"trail": {
					"trigger": "minecraft:inventory_changed",
					"conditions": {
						"items": [
							{
								"items": [
									"minecraft:firework_rocket"
								],
								"predicates": {
									"minecraft:fireworks": {
										"explosions": {
											"contains": [
												{
													"has_trail": true
												}
											]
										}
									}
								}
							}
						]
					}
				}
			}
			""";

}
