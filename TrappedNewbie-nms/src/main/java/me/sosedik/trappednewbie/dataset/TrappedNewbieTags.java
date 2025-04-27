package me.sosedik.trappednewbie.dataset;

import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

public class TrappedNewbieTags {

	// items
	public static final Tag<Material> GLOVES = ItemUtil.itemTag(trappedNewbieKey("gloves"));
	public static final Tag<Material> HAMMERS = ItemUtil.itemTag(trappedNewbieKey("hammers"));
	public static final Tag<Material> COSMETIC_ARMOR = ItemUtil.itemTag(trappedNewbieKey("cosmetic_armor"));

	// blocks
	public static final Tag<Material> MINEABLE_BY_HAND = ItemUtil.blockTag(trappedNewbieKey("mineable/hand"));

}
