package me.sosedik.trappednewbie.listener.entity;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import me.sosedik.kiterino.event.entity.MaybeAsyncEntityChangeAgeEvent;
import me.sosedik.miscme.api.event.entity.EntityTurnBabyEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

/**
 * Nerf babies health by half
 */
@NullMarked
public class NerfedBabies implements Listener {

	private static final NamespacedKey MAX_HEALTH_KEY = trappedNewbieKey("nerfed_baby");

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onTurn(EntityTurnBabyEvent event) {
		addModifier(event.getEntity());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onTurn(MaybeAsyncEntityChangeAgeEvent event) {
		Ageable entity = event.getEntity();
		if (event.isBaby()) {
			addModifier(entity);
			return;
		}

		AttributeInstance attribute = entity.getAttribute(Attribute.MAX_HEALTH);
		if (attribute == null) return;

		double health = entity.getHealth();
		double maxHealth = entity.getMaxHealth();

		attribute.removeModifier(MAX_HEALTH_KEY);

		double newMaxHealth = entity.getMaxHealth();
		double scaledHealth = Math.ceil((health * newMaxHealth) / maxHealth);
		entity.setHealth(scaledHealth);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpawn(EntityAddToWorldEvent event) {
		if (!(event.getEntity() instanceof Ageable entity)) return;
		if (entity.isAdult()) return;

		addModifier(entity);
	}

	private void addModifier(LivingEntity entity) {
		AttributeInstance attribute = entity.getAttribute(Attribute.MAX_HEALTH);
		if (attribute == null) return;
		if (attribute.getModifier(MAX_HEALTH_KEY) != null) return;

		var modifier = new AttributeModifier(MAX_HEALTH_KEY, -(entity.getMaxHealth() / 2), AttributeModifier.Operation.ADD_NUMBER);
		attribute.addTransientModifier(modifier);
	}

}
