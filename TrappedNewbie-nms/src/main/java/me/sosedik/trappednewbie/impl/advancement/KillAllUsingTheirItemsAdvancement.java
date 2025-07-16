package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.PlayerKilledEntityTriggerData;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;

@NullMarked
public class KillAllUsingTheirItemsAdvancement extends BaseAdvancement {

	public KillAllUsingTheirItemsAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		List<List<String>> requirements = new ArrayList<>();
		List<VanillaTriggerData<?>> triggerDatas = new ArrayList<>();

		addTriggerData(requirements, triggerDatas, EntityType.ZOMBIE, Material.IRON_SHOVEL, Material.IRON_SWORD);
		addTriggerData(requirements, triggerDatas, EntityType.ZOMBIE_VILLAGER, Material.IRON_SHOVEL, Material.IRON_SWORD);
		addTriggerData(requirements, triggerDatas, EntityType.HUSK, Material.IRON_SHOVEL, Material.IRON_SWORD);
		addTriggerData(requirements, triggerDatas, EntityType.SKELETON, Material.BOW);
		addTriggerData(requirements, triggerDatas, EntityType.STRAY, Material.BOW);
		addTriggerData(requirements, triggerDatas, EntityType.BOGGED, Material.BOW);
		addTriggerData(requirements, triggerDatas, EntityType.DROWNED, Material.FISHING_ROD, Material.NAUTILUS_SHELL, Material.TRIDENT);
		addTriggerData(requirements, triggerDatas, EntityType.ENDERMAN, Tag.ENDERMAN_HOLDABLE.getValues().toArray(Material[]::new));
		addPotionTriggerData(requirements, triggerDatas, EntityType.WITCH, PotionType.FIRE_RESISTANCE, PotionType.HEALING, PotionType.SWIFTNESS, PotionType.WATER_BREATHING);
		addTriggerData(requirements, triggerDatas, EntityType.PILLAGER, Material.CROSSBOW);
		addTriggerData(requirements, triggerDatas, EntityType.VEX, Material.IRON_SWORD);
		addTriggerData(requirements, triggerDatas, EntityType.VINDICATOR, Material.IRON_AXE);
		addTriggerData(requirements, triggerDatas, EntityType.PIGLIN, Material.CROSSBOW, Material.GOLDEN_SWORD);
		addTriggerData(requirements, triggerDatas, EntityType.PIGLIN_BRUTE, Material.GOLDEN_AXE);
		addTriggerData(requirements, triggerDatas, EntityType.ZOMBIFIED_PIGLIN, Material.CROSSBOW, Material.WARPED_FUNGUS_ON_A_STICK, Material.GOLDEN_SWORD, Material.GOLDEN_AXE);
		addTriggerData(requirements, triggerDatas, EntityType.WITHER_SKELETON, Material.STONE_SWORD);
		addTriggerData(requirements, triggerDatas, EntityType.FOX, Material.EMERALD, Material.RABBIT_FOOT, Material.RABBIT_HIDE, Material.EGG, Material.WHEAT, Material.LEATHER, Material.FEATHER);
		addTriggerData(requirements, triggerDatas, EntityType.PANDA, Material.BAMBOO, Material.CAKE);
		addTriggerData(requirements, triggerDatas, EntityType.WANDERING_TRADER, Material.MILK_BUCKET);
		addPotionTriggerData(requirements, triggerDatas, EntityType.WANDERING_TRADER, PotionType.INVISIBILITY);
		addTriggerData(requirements, triggerDatas, EntityType.IRON_GOLEM, Material.POPPY);

		return RequiredAdvancementProgress.vanilla(requirements, triggerDatas);
	}

	private static void addTriggerData(List<List<String>> requirements, List<VanillaTriggerData<?>> triggerDatas, EntityType entityType, Material... itemTypes) {
		for (Material itemType : itemTypes) {
			String criterion = entityType.key().value() + "_" + itemType.key().value();
			PlayerKilledEntityTriggerData triggerData = playerKilledEntity(criterion)
				.withEntity(entity -> entity.withEntityType(entityType))
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
					.withDirectEntity(entity -> entity
						.withEquipment(equipment -> equipment
							.withMainHand(itemType)
							.withOffHand(Material.AIR)
						)
					)
				);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData);
		}
	}

	private static void addPotionTriggerData(List<List<String>> requirements, List<VanillaTriggerData<?>> triggerDatas, EntityType entityType, PotionType... potionTypes) {
		for (PotionType potionType : potionTypes) {
			var itemType = Material.POTION;
			String components = """
				{
					"components": {
						"minecraft:potion_contents": {
							"potion":"%s"
						}
					}
				}
				""".formatted(potionType.key().value());
			String criterion = entityType.key().value() + "_" + potionType.key().value() + "_" + itemType.key().value();
			PlayerKilledEntityTriggerData triggerData = playerKilledEntity(criterion)
				.withEntity(entity -> entity.withEntityType(entityType))
				.withDamage(source -> source
					.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
					.withDirectEntity(entity -> entity
						.withEquipment(equipment -> equipment
							.withMainHand(ItemTriggerCondition.of(itemType).withRawComponents(components))
							.withOffHand(Material.AIR)
						)
					)
				);
			requirements.add(List.of(criterion));
			triggerDatas.add(triggerData);
		}
	}

}
