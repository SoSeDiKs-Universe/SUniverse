package me.sosedik.trappednewbie.dataset;

import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

public class TrappedNewbieTags {

	public static final Tag<Material> GLOVES = ItemUtil.itemTag(trappedNewbieKey("gloves"));
	public static final Tag<Material> COSMETIC_ARMOR = ItemUtil.itemTag(trappedNewbieKey("cosmetic_armor"));

}
