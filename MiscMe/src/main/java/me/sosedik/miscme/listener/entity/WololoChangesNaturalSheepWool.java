package me.sosedik.miscme.listener.entity;

import me.sosedik.kiterino.event.entity.EvokerWololoEvent;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Evoker's wololo cast changes the sheep color permanently
 */
@NullMarked
public class WololoChangesNaturalSheepWool implements Listener {

	@EventHandler
	public void onWololo(EvokerWololoEvent event) {
		Sheep sheep = event.getWololoTarget();
		DyeColor woolColor = sheep.getColor();
		if (woolColor == null) return;

		SheepRegrowNaturalWool.setNaturalWool(sheep, woolColor);
	}

}
