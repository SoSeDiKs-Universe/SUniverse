package me.sosedik.requiem.listener.block;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.dataset.RequiemTags;
import me.sosedik.requiem.impl.block.TombstoneBlockStorage;
import me.sosedik.utilizer.listener.BlockStorage;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Stray;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Random;
import java.util.function.Consumer;

/**
 * Natural tombstones may spawn some creatures
 */
public class TombstoneCreatures implements Listener {

	private static final Random RANDOM = new Random();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Material blockType = block.getType();
		if (!RequiemTags.TOMBSTONES.isTagged(blockType)) return;
		if (BlockStorage.getByLoc(block) instanceof TombstoneBlockStorage storage && storage.isPlayerTombstone()) return;

		Player player = event.getPlayer();
		if (player.getInventory().getItemInMainHand().hasEnchant(Enchantment.SILK_TOUCH)) return;

		Class<? extends Entity> silverfishType;
		World.Environment environment = block.getWorld().getEnvironment();
		if (environment == World.Environment.THE_END) {
			silverfishType = Endermite.class;
		} else if (environment == World.Environment.NETHER) {
			silverfishType = null;
		} else {
			silverfishType = Silverfish.class;
		}

		if (RequiemTags.SKELETON_SPAWNING_TOMBSTONES.isTagged(blockType)) {
			if (RANDOM.nextDouble() < 0.2) {
				if (RequiemTags.WITHER_SKELETON_TOMBSTONES.isTagged(blockType)) {
					spawnCreature(block, WitherSkeleton.class, mob -> {
						if (silverfishType != null) mob.addPassenger(spawnCreature(block, silverfishType, null));
					});
				} else if (RequiemTags.STRAY_SKELETON_TOMBSTONES.isTagged(blockType)) {
					spawnCreature(block, Stray.class, mob -> {
						mob.getEquipment().setItemInMainHand(ItemStack.empty());
						if (silverfishType != null) {
							mob.getEquipment().setHelmet(chainMailHelmet());
							mob.getEquipment().setHelmetDropChance(0F);
							mob.addPassenger(spawnCreature(block, silverfishType, null));
						}
					});
				} else {
					spawnCreature(block, Skeleton.class, mob -> {
						if (blockType != RequiemItems.SHOT_SKELETON_TOMBSTONE && blockType != RequiemItems.BOW_SKELETON_TOMBSTONE) {
							mob.getEquipment().setItemInMainHand(ItemStack.empty());
						}
						if (blockType == RequiemItems.HUNTED_SKELETON_TOMBSTONE) {
							mob.setArrowsInBody(1);
						} else if (blockType == RequiemItems.ARROWS_SKELETON_TOMBSTONE) {
							mob.setArrowsInBody(4);
						} else if (blockType == RequiemItems.SHOT_SKELETON_TOMBSTONE || blockType == RequiemItems.BOW_SKELETON_TOMBSTONE) {
							mob.setArrowsInBody(2);
						} else if (blockType == RequiemItems.HEADACHE_SKELETON_TOMBSTONE || blockType == RequiemItems.SLAIN_SKELETON_TOMBSTONE) {
							mob.getEquipment().setItemInMainHand(ItemStack.of(Material.STONE_SWORD));
							mob.getEquipment().setItemInMainHandDropChance(0F);
						} else if (blockType == RequiemItems.TRIDENT_SKELETON_TOMBSTONE) {
							mob.getEquipment().setItemInMainHand(ItemStack.of(Material.TRIDENT));
							mob.getEquipment().setItemInMainHandDropChance(0F);
						}
						if (silverfishType != null) {
							mob.getEquipment().setHelmet(chainMailHelmet());
							mob.getEquipment().setHelmetDropChance(0F);
							mob.addPassenger(spawnCreature(block, silverfishType, null));
						}
					});
				}
				return;
			}
		}

		if (RequiemTags.SILVERFISH_SPAWNING_TOMBSTONES.isTagged(blockType)) {
			for (int i = 0; i < 3; i++) {
				if (RANDOM.nextDouble() < 0.3) {
					spawnCreature(block, silverfishType, null);
				}
			}
		}
	}

	private ItemStack chainMailHelmet() {
		var item = ItemStack.of(Material.CHAINMAIL_HELMET);
		int maxDamage = (int) (item.getType().getMaxDurability() * 0.7);
		item.setData(DataComponentTypes.DAMAGE, RANDOM.nextInt(maxDamage));
		return item;
	}

	private <T extends Entity> T spawnCreature(Block block, Class<T> creatureClass, @Nullable Consumer<T> function) {
		return block.getWorld().spawn(block.getLocation().center(0.1), creatureClass, mob -> {
			mob.setPersistent(true);
			if (function != null)
				function.accept(mob);
		});
	}

}
