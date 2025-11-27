package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.listener.advancement.AdvancementTrophies;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

/**
 * Gives speed when sneaking with black belt
 */
@NullMarked
public class BlackBeltSpeed implements Listener {

	private static final AttributeModifier BLACK_BELT_SPEED = new AttributeModifier(trappedNewbieKey("black_belt_speed"), 3, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	private static final AttributeModifier BLACK_BELT_STEP = new AttributeModifier(trappedNewbieKey("black_belt_step"), 1, AttributeModifier.Operation.ADD_NUMBER);

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();

		if (!event.isSneaking() || !hasBelt(player)) {
			AttributeInstance attribute = player.getAttribute(Attribute.MOVEMENT_SPEED);
			if (attribute != null) attribute.removeModifier(BLACK_BELT_SPEED);
			attribute = player.getAttribute(Attribute.STEP_HEIGHT);
			if (attribute != null) attribute.removeModifier(BLACK_BELT_STEP);
			return;
		}

		AttributeInstance attribute = player.getAttribute(Attribute.MOVEMENT_SPEED);
		if (attribute != null) attribute.addTransientModifier(BLACK_BELT_SPEED);
		attribute = player.getAttribute(Attribute.STEP_HEIGHT);
		if (attribute != null) attribute.addTransientModifier(BLACK_BELT_STEP);
	}

	private boolean hasBelt(Player player) {
		if (AdvancementTrophies.isUsableTrophy(TrappedNewbieAdvancements.SNEAK_10KM, player, player.getInventory().getLeggings()))
			return true;

		VisualArmor visualArmor = VisualArmor.of(player);
		return visualArmor.hasLeggings() && AdvancementTrophies.isUsableTrophy(TrappedNewbieAdvancements.SNEAK_10KM, player, visualArmor.getLeggings());
	}

}
