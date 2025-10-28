package me.sosedik.trappednewbie.listener.world;

import me.sosedik.moves.listener.movement.FreeFall;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieFonts;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.LocationUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

/**
 * Teleport to resource world when falling in limbo world
 */
@NullMarked
public class LimboWorldFall implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFall(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.getWorld() != TrappedNewbie.limboWorld()) return;
		if (event.getDamageSource().getDamageType() != DamageType.OUT_OF_WORLD) return;

		event.setCancelled(true);
		if (TrappedNewbieAdvancements.REQUIEM_ROOT.isDone(player)) {
			if (TrappedNewbieAdvancements.BRAVE_NEW_WORLD.awardAllCriteria(player))
				removeFreeFriendshipLetters(player);
			World world = PerPlayerWorlds.getResourceWorld(player.getUniqueId(), World.Environment.NORMAL);
			spawnTeleport(player, world);
		} else {
			player.setVelocity(new Vector());
			LocationUtil.smartTeleport(player, TrappedNewbie.limboWorld().getSpawnLocation().center(1));
			player.sendMessage(Mini.combine(Component.space(), TrappedNewbieFonts.WANDERING_TRADER_HEAD.mapping(), Messenger.messenger(player).getMessage("limbo.welcome.ignored")));
		}
	}

	private void removeFreeFriendshipLetters(Player player) {
		ItemStack item;
		// TODO separate remove method, otherwise it can technically get stuck on folding
		while ((item = InventoryUtil.findItem(player, i -> i.getType() == TrappedNewbieItems.LETTER && LetterModifier.isUnboundFriendshipLetter(i))) != null)
			item.setAmount(0);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldChange(PlayerTeleportEvent event) {
		if (event.getTo().getWorld() != TrappedNewbie.limboWorld()) return;
		if (event.getFrom().getWorld() == TrappedNewbie.limboWorld()) return;

		event.setTo(TrappedNewbie.limboWorld().getSpawnLocation().center(1));
	}

	/**
	 * Teleports the player to a spawn location in the world
	 *
	 * @param player player
	 * @param world world
	 */
	public static void spawnTeleport(Player player, World world) {
		LocationUtil.smartTeleport(player, world.getSpawnLocation().center().y(world.getMaxHeight() + 50))
			.thenRun(() -> {
				Entity vehicle = player.getVehicle();
				if (vehicle == null) {
					FreeFall.startLeaping(player);
				} else {
					vehicle.setFallDistance(0F);
					player.setFallDistance(0F);
					Location loc = player.getLocation().toHighestLocation().above();
					LocationUtil.smartTeleport(player, loc);
				}
			});
	}

}
