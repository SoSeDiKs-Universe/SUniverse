package me.sosedik.miscme.listener.entity;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Baby animals can be age-locked via poisonous potato!
 */
@NullMarked
public class AnimalAgeLocking implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onAgeLock(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Animals animal)) return;
		if (animal.isAdult()) return;
		if (animal.getAgeLock()) return;

		Player player = event.getPlayer();
		if (tryToAgeLock(player, animal, EquipmentSlot.HAND) || tryToAgeLock(player, animal, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToAgeLock(Player player, Animals animal, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (item.getType() != Material.POISONOUS_POTATO) return false;

		if (!player.getGameMode().isInvulnerable())
			item.subtract();
		animal.setAgeLock(true);
		animal.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 20 * 5, 0));
		animal.emitSound(Sound.ENTITY_GENERIC_EAT, 1F, 1.2F);

		return true;
	}

}
