package me.sosedik.miscme.listener.entity;

import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.miscme.MiscMe.miscMeKey;

/**
 * Entity visibility range is dynamic
 */
@NullMarked
public class DynamicVisibilityRange implements Listener {

	private static final AttributeModifier BLINDNESS_VISIBILITY = new AttributeModifier(miscMeKey("blindness_visibility"), -0.9, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	private static final AttributeModifier SPYGLASS_VISIBILITY = new AttributeModifier(miscMeKey("spyglass_visibility"), 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEffect(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		PotionEffect newEffect = event.getNewEffect();
		PotionEffect oldEffect = event.getOldEffect();
		if (newEffect == null) {
			if (oldEffect == null) return;
			if (oldEffect.getType() != PotionEffectType.BLINDNESS) return;

			AttributeInstance attribute = entity.getAttribute(Attribute.FOLLOW_RANGE);
			if (attribute != null)
				attribute.removeModifier(BLINDNESS_VISIBILITY);
			attribute = entity.getAttribute(Attribute.TEMPT_RANGE);
			if (attribute != null)
				attribute.removeModifier(BLINDNESS_VISIBILITY);
			return;
		}
		if (newEffect.getType() != PotionEffectType.BLINDNESS) return;

		AttributeInstance attribute = entity.getAttribute(Attribute.FOLLOW_RANGE);
		if (attribute != null && attribute.getModifier(BLINDNESS_VISIBILITY.key()) == null)
			attribute.addTransientModifier(BLINDNESS_VISIBILITY);
		attribute = entity.getAttribute(Attribute.TEMPT_RANGE);
		if (attribute != null && attribute.getModifier(BLINDNESS_VISIBILITY.key()) == null)
			attribute.addTransientModifier(BLINDNESS_VISIBILITY);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEquip(EntityEquipmentChangedEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player) return;

		AttributeInstance attribute = entity.getAttribute(Attribute.FOLLOW_RANGE);
		if (attribute == null) return;

		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return;
		if (equipment.getItemInMainHand().getType() != Material.SPYGLASS && equipment.getItemInOffHand().getType() != Material.SPYGLASS) {
			attribute.removeModifier(SPYGLASS_VISIBILITY);
			return;
		}

		if (attribute.getModifier(SPYGLASS_VISIBILITY.key()) == null)
			attribute.addTransientModifier(SPYGLASS_VISIBILITY);
	}

}
