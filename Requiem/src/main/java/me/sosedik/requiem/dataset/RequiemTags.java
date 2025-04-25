package me.sosedik.requiem.dataset;

import me.sosedik.requiem.Requiem;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;

public class RequiemTags {

	public static final Tag<Material> TOMBSTONES = blockTag("tombstones");

	private static Tag<Material> blockTag(String key) {
		return ItemUtil.blockTag(Requiem.requiemKey(key));
	}

}
