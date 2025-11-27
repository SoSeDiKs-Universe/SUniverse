package me.sosedik.trappednewbie.dataset;

import me.sosedik.kiterino.util.KiterinoBootstrapEntityTypeInjector;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TrappedNewbieEntityTypes {

	public static final EntityType PAPER_PLANE = byKey("paper_plane");
	public static final EntityType GLIDER = byKey("glider");

	private static EntityType byKey(String value) {
		return KiterinoBootstrapEntityTypeInjector.injectEntityType(TrappedNewbie.trappedNewbieKey(value));
	}

}
