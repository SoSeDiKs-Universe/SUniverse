package me.sosedik.delightfulfarming.dataset;

import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.delightfulfarming.DelightfulFarming.delightfulFarmingKey;

@NullMarked
public class DelightfulFarmingTags {

	public static final Tag<Material> MILK_CONTAINERS = ItemUtil.itemTag(delightfulFarmingKey("milk_containers"));

}
