package me.sosedik.trappednewbie.dataset;

import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

// MCCheck: 1.21.5, new branches, hardened
@NullMarked
public class TrappedNewbieTags {

	// items
	public static final Tag<Material> STICKS = ItemUtil.itemTag(trappedNewbieKey("sticks"));
	public static final Tag<Material> BRANCHES = ItemUtil.itemTag(trappedNewbieKey("branches"));
	public static final Tag<Material> ROCKS = ItemUtil.itemTag(trappedNewbieKey("rocks"));
	public static final Tag<Material> GLOVES = ItemUtil.itemTag(trappedNewbieKey("gloves"));
	public static final Tag<Material> HAMMERS = ItemUtil.itemTag(trappedNewbieKey("hammers"));
	public static final Tag<Material> COSMETIC_ARMOR = ItemUtil.itemTag(trappedNewbieKey("cosmetic_armor"));
	public static final Tag<Material> ITEM_CHOPPING_BLOCKS = ItemUtil.itemTag(trappedNewbieKey("chopping_blocks"));
	public static final Tag<Material> GLASS_SHARDS = ItemUtil.itemTag(trappedNewbieKey("glass_shards"));

	// blocks
	public static final Tag<Material> MINEABLE_BY_HAND = ItemUtil.blockTag(trappedNewbieKey("mineable/hand"));
	public static final Tag<Material> CHOPPING_BLOCKS = ItemUtil.blockTag(trappedNewbieKey("chopping_blocks"));
	public static final Tag<Material> HARDENED = ItemUtil.blockTag(trappedNewbieKey("hardened"));
	public static final Tag<Material> PRICKY_BLOCKS = ItemUtil.blockTag(trappedNewbieKey("pricky_blocks"));

}
