package me.sosedik.trappednewbie.listener.entity;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.entity.craft.CraftGlider;
import me.sosedik.trappednewbie.entity.nms.GliderEntityImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.craftbukkit.entity.CraftFirework;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

/**
 * Interacting with gliders
 */
@NullMarked
public class GliderInteraction implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Interaction interaction)) return;
		if (!(interaction.getVehicle() instanceof CraftGlider craftGlider)) return;
		if (!(interaction.getWorld() instanceof CraftWorld world)) return;
		if (!(event.getDamageSource() instanceof CraftDamageSource damageSource)) return;

		GliderEntityImpl gliderEntity = craftGlider.getHandle();
		if (damageSource.getDamageType() == DamageType.GENERIC)
			damageSource = (CraftDamageSource) CraftDamageSource.buildFromBukkit(DamageType.PLAYER_ATTACK, damageSource.getDirectEntity(), damageSource.getCausingEntity(), damageSource.getDamageLocation());
		gliderEntity.hurtServer(world.getHandle(), damageSource.getHandle(), (float) event.getDamage());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInteract(PlayerInteractAtEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Interaction interaction)) return;
		if (!(interaction.getVehicle() instanceof CraftGlider craftGlider)) return;
		if (!(event.getPlayer() instanceof CraftPlayer player)) return;

		GliderEntityImpl gliderEntity = craftGlider.getHandle();
		gliderEntity.interact(player.getHandle(), InteractionHand.OFF_HAND);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;

		Player player = event.getPlayer();
		if (!(player.getVehicle() instanceof CraftGlider craftGlider)) return;

		if (tryToBoost(player, craftGlider, EquipmentSlot.HAND)
			|| tryToBoost(player, craftGlider, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToBoost(Player player, CraftGlider glider, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (item.getType() != Material.FIREWORK_ROCKET) return false;

		Firework firework = player.fireworkBoost(item);
		if (firework == null) return false;
		if (!(firework instanceof CraftFirework craftFirework)) {
			firework.remove();
			return false;
		}

		player.swingHand(hand);
		item.subtract();

		Vec3 rotationVector = glider.getHandle().getLookAngle();
		Vec3 velocity = glider.getHandle().getDeltaMovement();

		glider.getHandle().setDeltaMovement(
			velocity.add(rotationVector.x * 0.1 + (rotationVector.x * 1.5 - velocity.x) * 0.5, rotationVector.y * 0.1 + (rotationVector.y * 1.5 - velocity.y) * 0.5, rotationVector.z * 0.1 + (rotationVector.z * 1.5 - velocity.z) * 0.5)
		);

		if (craftFirework.getHandle().tickCount % 2 == 0)
			glider.getHandle().damageStack(1);

		craftFirework.getHandle().lifetime -= 1;

		glider.getHandle().push(new Vec3(0, 1.2, 0));

		return true;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPreDismount(EntityDismountEvent event) {
		if (!(event.getDismounted() instanceof CraftGlider craftGlider)) return;
		if (craftGlider.getHandle().hasLanded()) return;

		ItemStack item = craftGlider.getItemStack();
		if (!item.hasEnchant(Enchantment.BINDING_CURSE)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getDismounted() instanceof CraftGlider craftGlider)) return;

		Vector velocity = craftGlider.getVelocity();
		TrappedNewbie.scheduler().sync(() -> event.getEntity().setVelocity(velocity), 1L);
	}

}
