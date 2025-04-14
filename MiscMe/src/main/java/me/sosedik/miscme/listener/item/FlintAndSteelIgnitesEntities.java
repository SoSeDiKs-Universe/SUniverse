package me.sosedik.miscme.listener.item;

import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.DurabilityUtil;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Using flint and steel on an entity will ignite it
 */
@NullMarked
public class FlintAndSteelIgnitesEntities implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onFlintAndSteelApply(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof LivingEntity rightClicked)) return;

		var player = event.getPlayer();
		if (!(player.isSneaking() || (player.hasFixedPose() && player.getPose() == Pose.SWIMMING))) return;

		if (tryLitEntity(player, rightClicked, EquipmentSlot.HAND)
				|| tryLitEntity(player, rightClicked, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryLitEntity(Player player, LivingEntity rightClicked, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!UtilizerTags.FLINT_AND_STEEL.isTagged(item.getType())) return false;
		if (DurabilityUtil.isBroken(item)) return false;
		if (!rightClicked.setFireTicks(8 * 20)) return false;

		player.swingHand(hand);
		item.damage(1, player);
		rightClicked.emitSound(Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1F, 1F);

		if (rightClicked instanceof Mob mob && mob.getTarget() == null)
			mob.setTarget(player);

		return true;
	}

}
