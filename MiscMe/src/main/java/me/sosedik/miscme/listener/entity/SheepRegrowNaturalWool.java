package me.sosedik.miscme.listener.entity;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Sheep regrow their natural wool
 */
public class SheepRegrowNaturalWool implements Listener {

	private static final String NATURAL_WOOL_COLOR_TAG = "natural_wool_color";

	@EventHandler(ignoreCancelled = true)
	public void onWoolRegrow(@NotNull SheepRegrowWoolEvent event) {
		Sheep sheep = event.getEntity();
		NBT.modifyPersistentData(sheep, nbt -> {
			DyeColor naturalColor = nbt.getOrDefault(NATURAL_WOOL_COLOR_TAG, DyeColor.WHITE);
			sheep.setColor(naturalColor);
		});
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpawn(@NotNull CreatureSpawnEvent event) {
		if (!(event.getEntity() instanceof Sheep sheep)) return;

		NBT.modifyPersistentData(sheep, nbt -> {
			DyeColor woolColor = sheep.getColor();
			if (woolColor != null)
				nbt.setEnum(NATURAL_WOOL_COLOR_TAG, woolColor);
		});
	}

}
