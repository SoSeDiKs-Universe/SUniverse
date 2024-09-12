package me.sosedik.requiem.dataset;

import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import me.sosedik.requiem.Requiem;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class RequiemItems {

	public static final Material GHOST_MOTIVATOR = byKey("ghost_motivator");
	public static final Material GHOST_RELOCATOR = byKey("ghost_relocator");

	private static @NotNull Material byKey(@NotNull String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(Requiem.requiemKey(value));
	}

}
