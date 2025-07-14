package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

// MCCheck: 1.21.7, new weapons / attack opportunities
@NullMarked
public class AttackWithAllWeaponsAdvancement extends BaseAdvancement {

	public AttackWithAllWeaponsAdvancement(BaseAdvancementBuilder<?, ?> advancementBuilder) {
		super(advancementBuilder.requiredProgress(getProgress()));
	}

	private static RequiredAdvancementProgress getProgress() {
		return RequiredAdvancementProgress.vanilla(
			VanillaTriggerData.playerHurtEntity("axe")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
						.withSourceEntity(entity -> entity
							.withEquipment(equipment -> equipment
								.withMainHand(ItemTriggerCondition.of(Tag.ITEMS_AXES))
							)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("shovel")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
						.withSourceEntity(entity -> entity
							.withEquipment(equipment -> equipment
								.withMainHand(ItemTriggerCondition.of(Tag.ITEMS_SHOVELS))
							)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("pickaxe")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
						.withSourceEntity(entity -> entity
							.withEquipment(equipment -> equipment
								.withMainHand(ItemTriggerCondition.of(Tag.ITEMS_PICKAXES))
							)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("hoe")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
						.withSourceEntity(entity -> entity
							.withEquipment(equipment -> equipment
								.withMainHand(ItemTriggerCondition.of(Tag.ITEMS_HOES))
							)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("sword")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
						.withSourceEntity(entity -> entity
							.withEquipment(equipment -> equipment
								.withMainHand(ItemTriggerCondition.of(Tag.ITEMS_SWORDS))
							)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("bow")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(Tag.ENTITY_TYPES_ARROWS)
							.withNbt("{weapon:{id:\"minecraft:bow\"}}")
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("crossbow")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(Tag.ENTITY_TYPES_ARROWS)
							.withNbt("{weapon:{id:\"minecraft:crossbow\"}}")
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("firework_rocket")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.FIREWORK_ROCKET)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("trident_melee")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
						.withSourceEntity(entity -> entity
							.withEquipment(equipment -> equipment
								.withMainHand(ItemTriggerCondition.of(Material.TRIDENT))
							)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("trident_thrown")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.TRIDENT)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("tnt")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_EXPLOSION, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.TNT)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("snowball")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.SNOWBALL)
							.withNbt("{Item:{id:\"%s\"}}".formatted(Material.SNOWBALL.key()))
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("egg")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.EGG)
						)
					)
				),
			VanillaTriggerData.fishingRodHooked("fishing_rod")
				.withEntity(entity -> entity.inverted()
					.withEntityType(UtilizerTags.NON_MOB_ENTITIES)
				),
			VanillaTriggerData.playerHurtEntity("splash_potion")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.SPLASH_POTION)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("lingering_potion")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withDirectEntity(entity -> entity
							.withEntityType(UtilizerTags.LINGERING_POTION_DAMAGE_SOURCES)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("mace")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PLAYER_ATTACK, true)
						.withSourceEntity(entity -> entity
							.withEquipment(equipment -> equipment
								.withMainHand(ItemTriggerCondition.of(Material.MACE))
							)
						)
					)
				),
			VanillaTriggerData.playerHurtEntity("wind_charge")
				.withDamage(damage -> damage
					.withDamageSource(source -> source
						.withTag(DamageTypeTagKeys.IS_PROJECTILE, true)
						.withDirectEntity(entity -> entity
							.withEntityType(EntityType.WIND_CHARGE)
						)
					)
				)
		);
	}

}
