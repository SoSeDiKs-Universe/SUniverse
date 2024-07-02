package me.sosedik.requiem.dataset;

import org.bukkit.Material;

import static me.sosedik.kiterino.util.KiterinoBootstrapMaterialInjector.injectMaterial;
import static me.sosedik.resourcelib.util.ItemCreator.bowItem;

public final class RequiemItems {

	public static final Material GHOST_MOTIVATOR = injectMaterial(() -> bowItem().get()); // TODO remove workaround? can't access other plugins during bootstrap
	public static final Material GHOST_RELOCATOR = injectMaterial(() -> bowItem().get());

}
