package me.sosedik.requiem.listener.player.damage;

import me.sosedik.requiem.feature.playermodel.BodyPart;
import me.sosedik.requiem.feature.playermodel.PlayerDamageModel;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Falling from high distances causes feet damage
 */
@NullMarked
public class DamageFeetOnFall implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getDamageSource().getDamageType() != DamageType.FALL) return;

		player.sendMessage("Ouch!"); // TODO Ouch!
		var damageModel = PlayerDamageModel.of(player);
		damageModel.getState(BodyPart.LEFT_FOOT).damage(1);
		damageModel.getState(BodyPart.RIGHT_FOOT).damage(1);
	}

}
