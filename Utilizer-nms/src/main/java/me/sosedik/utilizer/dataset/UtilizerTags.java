package me.sosedik.utilizer.dataset;

import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UtilizerTags {

	// MCCheck: 1.21.10, new flattenables (hardcoded in ShovelItem)
	public static final Tag<Material> FLATTENABLES = itemTag("flattenables");
	// MCCheck: 1.21.10, new tillables (hardcoded in HoeItem)
	public static final Tag<Material> TILLABLES = itemTag("tillables");
	/**
	 * @see ItemUtil#isHot(ItemStack)
	 */
	public static final Tag<Material> HOT_ITEMS = itemTag("hot_items");
	/**
	 * @see ItemUtil#isLightSource(ItemStack)
	 */
	public static final Tag<Material> LIGHT_SOURCES = itemTag("light_sources");
	public static final Tag<Material> NO_TOOLTIP_ITEMS = itemTag("no_tooltip_items");
	public static final Tag<Material> MATERIAL_AIR = itemTag("material_air");
	public static final Tag<Material> NOT_DROPPABLE = itemTag("not_droppable");
	public static final Tag<Material> AUTO_RELEASING = itemTag("auto_releasing");
	public static final Tag<Material> AUTO_RELEASING_NO_CONSUME = itemTag("auto_releasing_no_consume");
	public static final Tag<Material> NO_ARROW_USABLE = itemTag("no_arrow_usable");
	public static final Tag<Material> FLINT_AND_STEEL = itemTag("flint_and_steel");
	public static final Tag<Material> SHEARS = itemTag("shears");
	public static final Tag<Material> BOWS = itemTag("bows");
	public static final Tag<Material> KNIFES = itemTag("knifes");
	public static final Tag<Material> FLAT_BLOCK_RENDER = itemTag("flat_block_render");
	public static final Tag<Material> STONE_ORES = itemTag("stone_ores");
	public static final Tag<Material> ORES = itemTag("ores");
	public static final Tag<Material> POISON_CURES = itemTag("poison_cures");
	public static final Tag<Material> SMITHING_TEMPLATES = itemTag("smithing_templates");

	public static final Tag<Material> STONE_ORES_BLOCKS = blockTag("stone_ores");
	public static final Tag<Material> ORES_BLOCKS = blockTag("ores");
	public static final Tag<Material> FRAGILE_BLOCKS = blockTag("fragile_blocks");

	// MCCheck: 1.21.10, new mobs
	public static final Tag<EntityType> HOSTILE_MONSTERS = entityTag("hostile_monsters");
	public static final Tag<EntityType> CHEST_BOATS = entityTag("chest_boats");
	public static final Tag<EntityType> NON_MOB_ENTITIES = entityTag("non_mob_entities");
	public static final Tag<EntityType> LINGERING_POTION_DAMAGE_SOURCES = entityTag("lingering_potion_damage_sources");
	public static final Tag<EntityType> BOW_SKELETONS = entityTag("bow_skeletons");
	public static final Tag<EntityType> LAND_ANIMALS = entityTag("land_animals");
	public static final Tag<EntityType> NATIVE_NETHER_MOBS = entityTag("native_nether_mobs");
	public static final Tag<EntityType> NON_NATIVE_NETHER_MOBS = entityTag("non_native_nether_mobs");
	public static final Tag<EntityType> HUMAN_LIKE_ZOMBIES = entityTag("human_like_zombies");
	public static final Tag<EntityType> MOBS_WITH_HANDS = entityTag("mobs_with_hands");
	public static final Tag<EntityType> OVERWORLD_HOSTILE_NIGHT_MOBS = entityTag("overworld_hostile_night_mobs");
	public static final Tag<EntityType> NETHER_HOSTILE_MOBS = entityTag("nether_hostile_mobs");
	public static final Tag<EntityType> END_HOSTILE_MOBS = entityTag("end_hostile_mobs");
	public static final Tag<EntityType> DUNGEON_HOSTILE_MOBS = entityTag("dungeon_hostile_mobs");
	public static final Tag<EntityType> HALLOWEEN_PUMPKIN_WEARERS = entityTag("halloween_pumpkin_wearers");
	public static final Tag<EntityType> SPIDERS = entityTag("spiders");
	/**
	 * Entities that can spawn from naturally generates spawners
	 */
	public static final Tag<EntityType> NATURAL_SPAWNERS = entityTag("natural_spawners");

	public static final TagKey<DamageType> IS_CAMPFIRE = damageTypeTag("is_campfire");

	private static Tag<Material> itemTag(String key) {
		return ItemUtil.itemTag(Utilizer.utilizerKey(key));
	}

	private static Tag<Material> blockTag(String key) {
		return ItemUtil.blockTag(Utilizer.utilizerKey(key));
	}

	private static Tag<EntityType> entityTag(String key) {
		return EntityUtil.entityTag(Utilizer.utilizerKey(key));
	}

	private static TagKey<DamageType> damageTypeTag(String key) {
		return DamageTypeTagKeys.create(Utilizer.utilizerKey(key));
	}

}
