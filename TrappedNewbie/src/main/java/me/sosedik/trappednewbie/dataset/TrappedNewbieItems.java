package me.sosedik.trappednewbie.dataset;

import me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class TrappedNewbieItems {

	public static final Material OAK_BRANCH = byKey("oak_branch");

	private static @NotNull Material byKey(@NotNull String value) {
		return KiterinoBootstrapMaterialInjector.injectMaterial(TrappedNewbie.trappedNewbieKey(value));
	}

}
