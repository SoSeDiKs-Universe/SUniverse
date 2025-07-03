package me.sosedik.trappednewbie.listener.item;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.listener.entity.SprayItemDrops;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;

/**
 * Effects upon attacking with a flower bouquet
 */
@NullMarked
public class FlowerBouquetAttackEffects implements Listener {

	// Items with 0 damage don't cause entity attack, force it
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSwing(PlayerArmSwingEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() != TrappedNewbieItems.FLOWER_BOUQUET) return;
		if (ItemUtil.getAttributeValue(item, EquipmentSlot.HAND, Attribute.ATTACK_DAMAGE, player) != 0) return;

		RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(),
				player.getEyeLocation().getDirection(), EntityUtil.PLAYER_REACH, entity -> entity != player);
		if (rayTraceResult == null) return;
		if (!(rayTraceResult.getHitEntity() instanceof LivingEntity entity)) return;

		event.setCancelled(true);
		entity.damage(0, player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof LivingEntity damager)) return;
		if (damager.getEquipment() == null) return;

		ItemStack item = damager.getEquipment().getItemInMainHand();
		if (item.getType() != TrappedNewbieItems.FLOWER_BOUQUET) return;

		Location loc = event.getEntity().getLocation().addY(event.getEntity().getHeight() - 0.1);
		Tag.SMALL_FLOWERS.getValues().forEach(flower -> SprayItemDrops.spawnSpray(loc, new ItemStack(flower)));
	}

}
