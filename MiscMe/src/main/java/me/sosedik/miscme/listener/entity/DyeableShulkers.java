package me.sosedik.miscme.listener.entity;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.miscme.listener.item.ImmersiveDyes;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/**
 * Shulkers can be dyed
 */
@NullMarked
public class DyeableShulkers implements Listener {

	public static final String CLEARED_SHULKER_KEY = "cleared";

	@EventHandler(ignoreCancelled = true)
	private void onDye(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Shulker shulker)) return;

		Player player = event.getPlayer();
		if (tryToDyeShulker(player, shulker, EquipmentSlot.HAND)
			|| tryToDyeShulker(player, shulker, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToDyeShulker(Player player, Shulker shulker, EquipmentSlot hand) {
		ItemStack handItem = player.getInventory().getItem(hand);
		if (!ImmersiveDyes.isDyingItem(handItem)) return false;

		DyeColor color = handItem.getType() == ImmersiveDyes.CLEARING_MATERIAL ? null : ImmersiveDyes.getDyeColor(handItem);
		if (color == shulker.getColor()) return false;

		Location loc = shulker.getLocation();
		Material effect = handItem.getType() == ImmersiveDyes.CLEARING_MATERIAL ? Material.SHULKER_BOX : Material.getMaterial(handItem.getType().name().replace("DYE", "SHULKER_BOX"));
		if (effect == null) return false;

		ImmersiveDyes.playEffect(player, hand, loc, effect.createBlockData());

		shulker.setColor(color);
		if (color == null)
			NBT.modifyPersistentData(shulker, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(CLEARED_SHULKER_KEY, true));
		else
			NBT.modifyPersistentData(shulker, (Consumer<ReadWriteNBT>) nbt -> nbt.removeKey(CLEARED_SHULKER_KEY));

		if (!player.getGameMode().isInvulnerable() && Math.random() < ImmersiveDyes.DYE_REDUCE_CHANCE)
			handItem.subtract();

		return true;
	}

}
