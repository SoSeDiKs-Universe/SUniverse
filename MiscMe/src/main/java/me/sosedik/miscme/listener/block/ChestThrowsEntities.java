package me.sosedik.miscme.listener.block;

import io.papermc.paper.block.LidMode;
import io.papermc.paper.block.LidState;
import io.papermc.paper.block.Lidded;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.world.damagesource.CombatEntry;
import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.EnderChest;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;

/**
 * Opening or closing a chest throws entities on it
 */
@NullMarked
public class ChestThrowsEntities implements Listener {

	/**
	 * Fall damage caused by the player opening a chest with the entity on its lid
	 */
	public static final DamageType CHEST_THROW = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(MiscMe.miscmeKey("chest_throw"));
	/**
	 * Damage caused by the player closing a chest with the entity under its lid
	 */
	public static final DamageType CHEST_CRUSH = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(MiscMe.miscmeKey("chest_crush"));

	@EventHandler(priority = EventPriority.HIGH)
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player player)) return;

		List<HumanEntity> viewers = event.getViewers();
		if (viewers.size() > 1) return;
		if (viewers.size() == 1 && viewers.getFirst() != player) return;
		if (!(event.getInventory().getHolder(false) instanceof TileState tileState)) return;
		if (!(tileState.getBlockData() instanceof Directional directional)) return;
		if (!isChest(tileState)) return;
		if (tileState instanceof Lidded lidded && lidded.getLidMode() == LidMode.FORCED_OPEN) return;

		Vector velocity = directional.getFacing().getDirection().multiply(0.4).setY(0.4);
		Location loc = tileState.getLocation().toCenterLocation().shiftTowards(BlockFace.UP);
		Collection<LivingEntity> entities = loc.getNearbyLivingEntities(0.4);
		entities.removeIf(EntityUtil.IGNORE_INTERACTION);
		entities.forEach(entity -> {
			entity.setVelocity(entity.getVelocity().add(velocity));
			if (entity != player) {
				var damageSource = DamageSource.builder(CHEST_CRUSH)
					.withCausingEntity(player)
					.withDirectEntity(player)
					.withDamageLocation(tileState.getLocation())
					.build();
				entity.damage(1, damageSource);
			}
		});
	}

	private boolean isChest(TileState tileState) {
		return tileState instanceof org.bukkit.block.Chest || tileState instanceof EnderChest;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onOpen(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (player.isSneaking() && (player.getInventory().getItemInMainHand().getType().isBlock() || player.getInventory().getItemInOffHand().getType().isBlock())) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!isChest(block.getType())) return;
		if (!(block.getBlockData() instanceof Directional directional)) return;
		if (block.getRelative(BlockFace.UP).isSolid()) return;
		if (!(block.getState(false) instanceof TileState tileState)) return;
		if (tileState instanceof Lidded lidded && lidded.getEffectiveLidState() == LidState.OPEN) return;

		Collection<LivingEntity> entities = block.getLocation().toCenterLocation().shiftTowards(BlockFace.UP).getNearbyLivingEntities(0.4);
		entities.removeIf(EntityUtil.IGNORE_INTERACTION);

		if (directional instanceof Chest && hasCat(entities)) {
			// Cats block chest opening, open manually
			ClickThroughHanging.openContainer(player, block, true);
		}

		boolean damage = block.getRelative(BlockFace.UP).getRelative(directional.getFacing().getOppositeFace()).isSolid();

		var velocity = directional.getFacing().getOppositeFace().getDirection().multiply(0.4).setY(0.4);
		entities.forEach(entity -> {
			entity.setVelocity(entity.getVelocity().add(velocity));

			var damageSource = DamageSource.builder(CHEST_THROW)
				.withCausingEntity(player)
				.withDirectEntity(player)
				.withDamageLocation(tileState.getLocation())
				.build();
			entity.getCombatTracker().addCombatEntry(CombatEntry.combatEntry(entity, damageSource, 0F));

			if (entity != player) {
				if (damage) {
					damageSource = DamageSource.builder(CHEST_CRUSH)
						.withCausingEntity(player)
						.withDirectEntity(player)
						.withDamageLocation(tileState.getLocation())
						.build();
					entity.damage(1, damageSource);
				}
			}
		});
	}

	private boolean hasCat(Collection<LivingEntity> entities) {
		for (LivingEntity entity : entities) {
			if (entity.getType() == EntityType.CAT)
				return true;
		}
		return false;
	}

	private boolean isChest(Material type) {
		return type == Material.CHEST
				|| type == Material.TRAPPED_CHEST
				|| type == Material.ENDER_CHEST;
	}

}
