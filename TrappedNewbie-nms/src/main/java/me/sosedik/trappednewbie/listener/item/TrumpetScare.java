package me.sosedik.trappednewbie.listener.item;

import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.kiterino.event.entity.ItemConsumeEvent;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import net.kyori.adventure.sound.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Trumpet release logic
 */
public class TrumpetScare implements Listener {

	private static final NamespacedKey SOUND = ResourceLib.getSound(TrappedNewbie.trappedNewbieKey("item/doot_doot"));

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUse(PlayerItemConsumeEvent event) {
		tryToUse(event);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUse(EntityItemConsumeEvent event) {
		tryToUse(event);
	}

	public void tryToUse(ItemConsumeEvent event) {
		if (!ItemStack.isType(event.getItem(), TrappedNewbieItems.TRUMPET)) return;

		LivingEntity livingEntity = event.getEntity();
		livingEntity.emitSound(Sound.sound(SOUND, Sound.Source.HOSTILE, 1F, 0.9F + (float) Math.random() * 0.2F));
		boolean undead = Tag.ENTITY_TYPES_UNDEAD.isTagged(livingEntity.getType());

		if (livingEntity instanceof Player player && !player.getGameMode().isInvulnerable())
			event.setReplacement(event.getItem().damage(1, livingEntity));

		livingEntity.getWorld().getNearbyEntities(livingEntity.getBoundingBox().expand(5), livingEntity::hasLineOfSight).forEach(entity -> {
			if (entity == livingEntity) return;
			if (!(entity instanceof LivingEntity living)) return;
			if (undead && Tag.ENTITY_TYPES_UNDEAD.isTagged(entity.getType())) return;

			double deltaX = entity.getX() - livingEntity.getX() + Math.random() - Math.random();
			double deltaZ = entity.getZ() - livingEntity.getZ() + Math.random() - Math.random();
			double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
			entity.setVelocity(new Vector(deltaX / (10 + distance), 5 / (10 + distance), deltaZ / (10 + distance)));

			living.damage(1, livingEntity);
		});
	}

}
