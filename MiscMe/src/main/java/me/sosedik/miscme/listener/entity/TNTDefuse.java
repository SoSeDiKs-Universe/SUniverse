package me.sosedik.miscme.listener.entity;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/**
 * TNT can be defused with shears
 */
@NullMarked
public class TNTDefuse implements Listener {

	private static final String DEFUSED_TAG = "defused";

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTNTDefuse(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Explosive tnt)) return;
		if (!(tnt instanceof TNTPrimed || tnt instanceof ExplosiveMinecart)) return;

		if (NBT.getPersistentData(tnt, nbt -> nbt.hasTag(DEFUSED_TAG))) {
			tnt.emitSound(Sound.BLOCK_LEVER_CLICK, 0.25F, 1.8F);
			return;
		}

		Player player = event.getPlayer();
		if (tryDefusing(player, tnt, EquipmentSlot.HAND)
			|| tryDefusing(player, tnt, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryDefusing(Player player, Explosive tnt, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!UtilizerTags.SHEARS.isTagged(item.getType())) return false;

		NBT.modifyPersistentData(tnt, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(DEFUSED_TAG, true));

		player.swingHand(hand);
		item.damage(1, player);

		tnt.emitSound(Sound.BLOCK_LEVER_CLICK, 0.25F, 0.5F);
		tnt.getWorld().spawnParticle(Particle.SMOKE, tnt.getLocation().addY(0.5), 20, 0.01F, 0.1F, 0.01F, 0.1F);
		tnt.getWorld().spawnParticle(Particle.ENCHANT, tnt.getLocation().addY(0.9), 10, 0.01F, 0.2F, 0.01F, 0.1F);

		return true;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPrime(ExplosionPrimeEvent event) {
		if (!(event.getEntity() instanceof Explosive tnt)) return;
		if (!(tnt instanceof TNTPrimed || tnt instanceof ExplosiveMinecart)) return;
		if (!NBT.getPersistentData(tnt, nbt -> nbt.hasTag(DEFUSED_TAG))) return;

		event.setCancelled(true);
		tnt.remove();
		tnt.getWorld().dropItemNaturally(tnt.getLocation(), ItemStack.of(tnt instanceof TNTPrimed ? Material.TNT : Material.TNT_MINECART));
		tnt.emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 0.75F, 1F);
		tnt.getWorld().spawnParticle(Particle.CLOUD, tnt.getLocation().addY(0.25), 20, 0.075F, 0.1F, 0.075F, 0.1F);
	}

}
