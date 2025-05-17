package me.sosedik.moves.dataset;

import me.sosedik.moves.Moves;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MovesTags {

	public static final Tag<Material> FALL_THROUGH_BLOCKS = blockTag("fall_through_blocks");
	public static final Tag<Material> FRAGILE_BLOCKS = blockTag("fragile_blocks");

	private static Tag<Material> blockTag(String key) {
		return ItemUtil.blockTag(Moves.movesKey(key));
	}

}
