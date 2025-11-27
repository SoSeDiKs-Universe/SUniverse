package me.sosedik.miscme.listener.entity;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Sheep regrow their natural wool
 */
@NullMarked
public class SheepRegrowNaturalWool implements Listener {

	public static final String NATURAL_WOOL_COLOR_TAG = "natural_wool_color";

	@EventHandler(ignoreCancelled = true)
	public void onWoolRegrow(SheepRegrowWoolEvent event) {
		Sheep sheep = event.getEntity();
		NBT.modifyPersistentData(sheep, nbt -> {
			DyeColor naturalColor = nbt.getOrDefault(NATURAL_WOOL_COLOR_TAG, DyeColor.WHITE);
			sheep.setColor(naturalColor);
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpawn(CreatureSpawnEvent event) {
		if (!(event.getEntity() instanceof Sheep sheep)) return;

		DyeColor woolColor = sheep.getColor();
		if (woolColor == null) return;

		setNaturalWool(sheep, woolColor);
	}

	/**
	 * Sets the sheep's natural wool color
	 *
	 * @param sheep sheep
	 * @param woolColor wool color
	 */
	public static void setNaturalWool(Sheep sheep, @Nullable DyeColor woolColor) {
		NBT.modifyPersistentData(sheep, nbt -> {
			if (woolColor == null)
				nbt.removeKey(NATURAL_WOOL_COLOR_TAG);
			else
				nbt.setEnum(NATURAL_WOOL_COLOR_TAG, woolColor);
		});
	}

}
