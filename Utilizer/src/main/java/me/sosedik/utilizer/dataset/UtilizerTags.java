package me.sosedik.utilizer.dataset;

import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UtilizerTags {

	// MCCheck: 1.21.7, new flattenables (hardcoded in ShovelItem)
	public static final Tag<Material> FLATTENABLES = itemTag("flattenables");
	// MCCheck: 1.21.7, new tillables (hardcoded in HoeItem)
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

	public static final Tag<Material> STONE_ORES_BLOCKS = blockTag("stone_ores");
	public static final Tag<Material> ORES_BLOCKS = blockTag("ores");

	// MCCheck: 1.21.7, new hostile mobs
	public static final Tag<EntityType> HOSTILE_MONSTERS = entityTag("hostile_monsters");

	private static Tag<Material> itemTag(String key) {
		return ItemUtil.itemTag(Utilizer.utilizerKey(key));
	}

	private static Tag<Material> blockTag(String key) {
		return ItemUtil.itemTag(Utilizer.utilizerKey(key));
	}

	private static Tag<EntityType> entityTag(String key) {
		return EntityUtil.entityTag(Utilizer.utilizerKey(key));
	}

}
