package me.sosedik.trappednewbie.listener.item;

import me.sosedik.miscme.listener.entity.ItemFrameFallables;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.trappednewbie.listener.thirst.DrinkableWater;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bowl can be filled with water
 */
@NullMarked
public class FillingBowlWithWater implements Listener {
	
	public static final Map<Material, Material> BOWLS = Map.ofEntries(
		Map.entry(Material.GLASS_BOTTLE, Material.POTION),
		Map.entry(Material.BOWL, TrappedNewbieItems.FILLED_BOWL),
		Map.entry(TrappedNewbieItems.CACTUS_BOWL, TrappedNewbieItems.FILLED_CACTUS_BOWL)
	);
	public static final Map<Material, Material> REVERSED_BOWLS = BOWLS.entrySet()
		.stream()
		.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

	public FillingBowlWithWater() {
		addItemFrameFallables();
	}

	private void addItemFrameFallables() {
		ItemFrameFallables.ItemFrameFallable fallableRule = (item, block) -> { // TODO custom cauldrons
			if (block.getType() == Material.CAULDRON) {
				block.setType(Material.WATER_CAULDRON);
				block.emitSound(Sound.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, 0.7F, 1.3F);
				return null;
			}

			if (block.getType() == Material.WATER_CAULDRON && block.getBlockData() instanceof Levelled levelled) {
				if (levelled.getLevel() < levelled.getMaximumLevel()) {
					levelled.setLevel(levelled.getLevel() + 1);
					block.setBlockData(levelled);
				}
				block.emitSound(Sound.BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, 0.6F, 1.3F);
				return null;
			}

			if (block.getType() == Material.LAVA_CAULDRON) {
				block.emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.6F, 1.3F);
				return null;
			}

			block.emitSound(Sound.ENTITY_GENERIC_DRINK, 0.6F, 1.3F);
			return null;
		};
		FillingBowlWithWater.BOWLS.forEach((bowl, filledBowl) -> {
			if (bowl == Material.GLASS_BOTTLE) return;

			ItemFrameFallables.addFallable(filledBowl, fallableRule);
		});
	}

	@EventHandler
	public void onFill(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;

		if (tryToFillBowl(event, EquipmentSlot.HAND) || tryToFillBowl(event, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onDispense(BlockDispenseEvent event) { // ToDo: vanilla puts water bottle into dispenser if there's place, but the event does not support such behaviour yet
		Material resultType = BOWLS.get(event.getItem().getType());
		if (resultType == null) return;

		Block block = event.getBlock();
		if (!(block.getBlockData() instanceof Dispenser dispenser)) return;

		block = block.getRelative(dispenser.getFacing());

		event.setItem(ThirstData.of(block).saveInto(ItemStack.of(resultType)));
	}

	private boolean tryToFillBowl(PlayerInteractEvent event, EquipmentSlot hand) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(hand);
		Material resultType = BOWLS.get(item.getType());
		if (resultType == null) return false;
		if (player.hasCooldown(item.getType())) return false;

		Block block = getTargetBlock(event);
		if (block == null) return false;

		var drinkableData = ThirstData.of(block);
		if (!DrinkableWater.decreaseWater(block, true)) return false;

		event.setCancelled(true);
		player.setCooldown(item.getType(), 20);
		block.emitSound(Sound.AMBIENT_UNDERWATER_EXIT, SoundCategory.PLAYERS, 0.5F, 2F);
		player.swingHand(hand);
		if (!player.getGameMode().isInvulnerable())
			item.subtract();
		InventoryUtil.replaceOrAdd(player, hand, drinkableData.saveInto(ItemStack.of(resultType)));
//		TrappedNewbieAdvancements.PICK_UP_SOME_WATER.awardAllCriteria(player); // TODO adv

		return true;
	}

	public static @Nullable Block getTargetBlock(PlayerInteractEvent event) {
		Block block;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			block = event.getClickedBlock();
			if (block == null) return null;
			if (!DrinkableWater.isDrinkable(block)) {
				block = block.getRelative(event.getBlockFace());
				if (block.getType() != Material.WATER) return null;
			}
		} else {
			block = event.getPlayer().getTargetBlockExact(4, FluidCollisionMode.ALWAYS);
			if (block == null) return null;
			if (block.getType() != Material.WATER) return null;
		}
		return block;
	}

}
