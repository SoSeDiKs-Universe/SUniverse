package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Material;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class KungFuPandaAdvancement implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Panda panda)) return;

		Player rider = panda.getRider();
		if (rider == null) return;
		if (!PossessingPlayer.isPossessing(rider)) return;

		ItemStack item = panda.getEquipment().getItemInMainHand();
		if (item.getType() != Material.WOODEN_SWORD) return;

		TrappedNewbieAdvancements.KUNG_FU_PANDA.awardAllCriteria(rider);
	}

}
