package me.sosedik.requiem.dataset;

import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import me.sosedik.requiem.Requiem;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("unused")
public final class RequiemItems {

	public static final Material SADDLE_OUTLINE = byKey("saddle_outline");

	public static final Material GHOST_MOTIVATOR = byKey("ghost_motivator");
	public static final Material GHOST_RELOCATOR = byKey("ghost_relocator");
	public static final Material HOST_REVOCATOR = byKey("host_revocator");

	public static final Material CREEPER_HEART = byKey("creeper_heart");

	private static Material byKey(String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(Requiem.requiemKey(value));
	}

}
