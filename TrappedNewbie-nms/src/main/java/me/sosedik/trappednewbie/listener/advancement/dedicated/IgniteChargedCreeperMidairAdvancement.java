package me.sosedik.trappednewbie.listener.advancement.dedicated;

import com.destroystokyo.paper.event.entity.CreeperIgniteEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IgniteChargedCreeperMidairAdvancement implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onIgnite(CreeperIgniteEvent event) {
		Creeper entity = event.getEntity();
		if (!entity.isPowered()) return;
		if (!entity.hasPotionEffect(PotionEffectType.LEVITATION)) return;
		if (!EntityUtil.isInAirLazy(entity)) return;
		if (!(entity.getIgniter() instanceof Player player)) return;

		TrappedNewbieAdvancements.IGNITE_CHARGED_CREEPER_MIDAIR.awardAllCriteria(player);
	}

}
