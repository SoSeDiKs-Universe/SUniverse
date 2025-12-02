package me.sosedik.trappednewbie.dataset;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;

// MCCheck: 1.21.10, new branches (chopping blocks, work stations), hardened
@NullMarked
public class TrappedNewbieTags {

	// items
	public static final Tag<Material> ARROW_HEAD_MATERIALS = itemTag("arrow_head_materials");
	public static final Tag<Material> ARROW_STICK_MATERIALS = itemTag("arrow_stick_materials");
	public static final Tag<Material> ARROW_FLETCHING_MATERIALS = itemTag("arrow_fletching_materials");
	public static final Tag<Material> ARROW_INFUSION_MATERIALS = itemTag("arrow_infusion_materials");
	public static final Tag<Material> STICKS = itemTag("sticks");
	public static final Tag<Material> BRANCHES = itemTag("branches");
	public static final Tag<Material> ROCKS = itemTag("rocks");
	public static final Tag<Material> ROCKY = itemTag("rocky");
	public static final Tag<Material> GLOVES = itemTag("gloves");
	public static final Tag<Material> HAMMERS = itemTag("hammers");
	public static final Tag<Material> COSMETIC_ARMOR = itemTag("cosmetic_armor");
	public static final Tag<Material> ITEM_CHOPPING_BLOCKS = itemTag("chopping_blocks");
	public static final Tag<Material> ITEM_WORK_STATIONS = itemTag("work_stations");
	public static final Tag<Material> GLASS_SHARDS = itemTag("glass_shards");
	public static final Tag<Material> PLACEABLE_ITEMS = itemTag("placeable_items");
	public static final Tag<Material> PAPERS = itemTag("papers");
	public static final Tag<Material> SCRAPPABLE = itemTag("scrappable");
	public static final Tag<Material> CANTEENS = itemTag("canteens");
	public static final Tag<Material> DEHYDRATING_FOOD = itemTag("dehydrating_food");
	public static final Tag<Material> GOODIE_BAGS = itemTag("goodie_bags");
	public static final Tag<Material> HALLOWEEN_CANDIES = itemTag("halloween_candies");

	// blocks
	public static final Tag<Material> MINEABLE_BY_HAND = blockTag("mineable/hand");
	public static final Tag<Material> CHOPPING_BLOCKS = blockTag("chopping_blocks");
	public static final Tag<Material> WORK_STATIONS = blockTag("work_stations");
	public static final Tag<Material> DRUMS = blockTag("drums");
	public static final Tag<Material> TOTEM_BASES = blockTag("totem_bases");
	public static final Tag<Material> HARDENED = blockTag("hardened");
	public static final Tag<Material> PRICKY_BLOCKS = blockTag("pricky_blocks");

	private static Tag<Material> itemTag(String key) {
		return ItemUtil.itemTag(TrappedNewbie.trappedNewbieKey(key));
	}

	private static Tag<Material> blockTag(String key) {
		return ItemUtil.blockTag(TrappedNewbie.trappedNewbieKey(key));
	}

}
