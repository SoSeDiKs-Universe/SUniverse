package me.sosedik.trappednewbie.listener.advancement.dedicated;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class YouMonsterAdvancement implements Listener {

	private static final String ANGRY_TAG = "angry";

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Wolf wolf) {
			NBT.modifyPersistentData(wolf, nbt -> {
				if (wolf.isTamed()) return;

				nbt.setBoolean(ANGRY_TAG, wolf.isAngry());
			});
		} else if (event.getEntity() instanceof Bee bee) {
			NBT.modifyPersistentData(bee, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(ANGRY_TAG, bee.isAggressive()));
		}
	}

}
