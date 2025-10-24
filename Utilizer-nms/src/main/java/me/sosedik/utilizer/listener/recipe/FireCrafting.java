package me.sosedik.utilizer.listener.recipe;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.impl.recipe.FireCraft;
import me.sosedik.utilizer.util.RecipeManager;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Crafting by burning items in fire
 */
@NullMarked
public class FireCrafting implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBurn(EntityRemoveFromWorldEvent event) {
		if (!(event.getEntity() instanceof Item entity)) return;

		EntityDamageEvent lastDamageCause = entity.getLastDamageCause();
		if (lastDamageCause == null) return;
		if (!isFire(lastDamageCause.getCause())) return;

		Block block = entity.getLocation().getBlock();

		ItemStack item = entity.getItemStack();
		FireCraft recipe = RecipeManager.getRecipe(FireCraft.class, new ItemStack[] {item});
		if (recipe == null) return;

		Utilizer.scheduler().sync(() -> {
			if (!block.getLocation().isChunkLoaded()) return;

			recipe.performAction(null, block);
		}, 1L);
	}

	private boolean isFire(EntityDamageEvent.DamageCause cause) {
		return cause == EntityDamageEvent.DamageCause.FIRE
				|| cause == EntityDamageEvent.DamageCause.FIRE_TICK;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBurn(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		ItemStack item = event.getItem();
		if (item == null) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		if (!Tag.FIRE.isTagged(block.getType())) {
			block = block.getRelative(event.getBlockFace());
			if (!Tag.FIRE.isTagged(block.getType()))
				return;
		}

		FireCraft recipe = RecipeManager.getRecipe(FireCraft.class, new ItemStack[] {item});
		if (recipe == null) return;

		event.setCancelled(true);
		Player player = event.getPlayer();
		if (!player.getGameMode().isInvulnerable())
			item.subtract();
		recipe.performAction(player, block);
	}

}
