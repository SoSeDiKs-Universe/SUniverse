package me.sosedik.requiem.feature;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTType;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.api.event.player.PlayerStartPossessingEntityEvent;
import me.sosedik.requiem.api.event.player.PlayerStopPossessingEntityEvent;
import me.sosedik.requiem.api.event.player.PlayerTryPossessingEntityEvent;
import me.sosedik.requiem.dataset.RequiemEffects;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.task.DynamicScaleTask;
import me.sosedik.requiem.task.PoseMimicingTask;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Golem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

// MCCheck: 1.21.11, new mobs visually carrying items outside entity equipment
@NullMarked
public class PossessingPlayer {

	public static final int MAX_ATTRITION_LEVEL = 5;

	private static final String POSSESSED_TAG = "possessed";
	private static final String POSSESSED_PERSISTENT_TAG = "persistent";
	private static final String POSSESSED_ENTITY_DATA = "entity_data";
	private static final String RESURRECTED_ENTITY_TAG = "resurrected";

	private static final Set<UUID> POSSESSING = new HashSet<>();
	private static final List<Predicate<Player>> ITEM_RULES = new ArrayList<>();

	/**
	 * Checks whether the player is possessing a mob
	 *
	 * @param player player
	 * @return whether the player is possessing a mob
	 */
	public static boolean isPossessing(Player player) {
		boolean possessing = isPossessingSoft(player);
		if (possessing && getPossessed(player) == null) {
			possessing = false;
			stopPossessing(player);
		}
		return possessing;
	}

	/**
	 * Checks whether the player is possessing a mob without validation checks
	 *
	 * @param player player
	 * @return whether the player is possessing a mob
	 */
	public static boolean isPossessingSoft(Player player) {
		return POSSESSING.contains(player.getUniqueId());
	}

	/**
	 * Makes the player to start possessing the mob
	 *
	 * @param player player
	 * @param entity possessed entity
	 * @param preAction pre-possessing action
	 */
	public static boolean startPossessing(Player player, LivingEntity entity, @Nullable Runnable preAction) {
		return startPossessing(player, entity, preAction, false);
	}

	/**
	 * Makes the player to start possessing the mob
	 *
	 * @param player player
	 * @param entity possessed entity
	 * @param preAction pre-possessing action
	 * @param skipEvent whether to skip the pre event
	 */
	public static boolean startPossessing(Player player, LivingEntity entity, @Nullable Runnable preAction, boolean skipEvent) {
		if (isPossessing(player) && getPossessed(player) == entity) return false;
		if (!skipEvent && !new PlayerTryPossessingEntityEvent(player, entity).callEvent()) return false;
		if (!entity.setRider(player)) return false;

		POSSESSING.add(player.getUniqueId());

		if (preAction != null)
			preAction.run();

		GhostyPlayer.clearGhost(player);
//		TemperaturedPlayer.of(player).addFlag(TempFlag.GHOST_IMMUNE); // TODO

		boolean persistent = entity.isPersistent();
		entity.setPersistent(true);

		NBT.modifyPersistentData(entity, nbt -> {
			nbt = nbt.getOrCreateCompound(POSSESSED_TAG);
			nbt.setBoolean(POSSESSED_PERSISTENT_TAG, persistent);
		});

		int level = player.getLevel();
		if (level < 2 && entity instanceof Golem)
			level = 5;
		applyAttrition(player, level);
		player.setInvisible(true);
		player.setInvulnerable(false); // Prevents mobs from targeting the player if true
		player.setSleepingIgnored(true);
		player.setRemainingAir(entity.getRemainingAir());

		checkPossessedExtraItems(player, false);

		if (entity instanceof Bat)
			player.addPotionEffect(infinitePotionEffect(PotionEffectType.NIGHT_VISION));
		if (EntityUtil.isFireImmune(entity.getType()))
			player.addPotionEffect(infinitePotionEffect(PotionEffectType.FIRE_RESISTANCE));

		new PlayerStartPossessingEntityEvent(player, entity).callEvent();

		new DynamicScaleTask(player, entity);
		new PoseMimicingTask(player, entity);

		Requiem.logger().info("Making " + player.getName() + " possess " + entity.getType().getKey());
		return true;
	}

	/**
	 * Makes player to stop possessing a mob
	 *
	 * @param player player
	 */
	public static @Nullable LivingEntity stopPossessing(Player player) {
		return stopPossessing(player, getPossessed(player), false);
	}

	/**
	 * Makes player to stop possessing a mob
	 *
	 * @param player player
	 * @param riding possessed entity
	 */
	public static @Nullable LivingEntity stopPossessing(Player player, @Nullable LivingEntity riding, boolean quit) {
		if (!isPossessingSoft(player)) return null;

		if (quit) {
			if (riding != null) riding.remove();
		} else {
			if (riding != null) {
				NBT.modifyPersistentData(riding, nbt -> {
					nbt = nbt.getOrCreateCompound(POSSESSED_TAG);

					if (nbt.hasTag(POSSESSED_PERSISTENT_TAG)) {
						boolean persistent = nbt.getBoolean(POSSESSED_PERSISTENT_TAG);
						nbt.removeKey(POSSESSED_PERSISTENT_TAG);
						riding.setPersistent(persistent);
					}

					if (canPreserveInventory(player))
						InventoryUtil.storeSlotted(player.getInventory(), nbt, item -> !isExtraPossessedItem(item));
				});
			}
			player.getInventory().clear();
		}

		player.setInvisible(false);
		player.setSleepingIgnored(false);

//		TemperaturedPlayer.of(player).removeFlag(TempFlag.GHOST_IMMUNE); // TODO

		POSSESSING.remove(player.getUniqueId());

		if (riding != null)
			removePossessedExtraItems(riding);

		PotionEffect unluck = player.getPotionEffect(PotionEffectType.UNLUCK);
		player.clearActivePotionEffects();
		if (unluck != null)
			player.addPotionEffect(unluck);

		if (riding != null)
			new PlayerStopPossessingEntityEvent(player, riding).callEvent();

		if (quit) return riding;

		player.setLevel(0);
		player.setExp(0F);

		player.leaveVehicle();

		if (riding != null) Requiem.logger().info("Making " + player.getName() + " no longer possess " + riding.getType().name());
		else Requiem.logger().info("Making " + player.getName() + " no longer possess an entity");

		return riding;
	}

	/**
	 * Gets the entity the player's currently possessing
	 *
	 * @param player player
	 * @return the possessed entity
	 */
	public static @Nullable LivingEntity getPossessed(Player player) {
		if (!(player.getVehicle() instanceof LivingEntity riding)) return null;
		if (player != riding.getRider()) return null;
		return riding;
	}

	/**
	 * Migrates stats (including equipment) from player to the entity
	 *
	 * @param player player
	 * @param entity entity
	 */
	public static void migrateStatsToEntity(Player player, LivingEntity entity) {
		migrateInvToEntity(player, entity);

		entity.setPersistent(false);
		entity.setArrowsInBody(player.getArrowsInBody());
		entity.setFireTicks(player.getFireTicks());
		entity.setRemainingAir(player.getRemainingAir());
		if (entity instanceof Mob mob)
			mob.setLeftHanded(player.getMainHand() == MainHand.LEFT);
		if (entity instanceof Ageable ageable)
			ageable.setAdult();
	}

	private static void migrateInvToEntity(Player player, LivingEntity entity) {
		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		PlayerInventory playerInventory = player.getInventory();
		entityEquipment.clear();
		entityEquipment.setItemInMainHand(playerInventory.getItemInMainHand());
		entityEquipment.setItemInOffHand(playerInventory.getItemInOffHand());
		entityEquipment.setHelmet(playerInventory.getHelmet());
		entityEquipment.setChestplate(playerInventory.getChestplate());
		entityEquipment.setLeggings(playerInventory.getLeggings());
		entityEquipment.setBoots(playerInventory.getBoots());

		if (entity instanceof Enderman enderman) {
			ItemStack item = playerInventory.getItemInMainHand();
			if (item.getType().isBlock()) {
				enderman.setCarriedBlock(item.hasBlockData() ? item.getBlockData(item.getType()) : item.getType().createBlockData());
			} else {
				enderman.setCarriedBlock(null);
			}
		}
	}

	/**
	 * Migrates stats (including equipment) from entity to the player.
	 * This will erase the player's current inventory!
	 *
	 * @param player player
	 * @param entity entity
	 */
	public static void migrateStatsToPlayer(Player player, LivingEntity entity) {
		player.getInventory().clear();
		migrateInvFromEntity(player, entity, true);
	}

	/**
	 * Migrates entity's equipment to player
	 *
	 * @param player player
	 * @param entity entity
	 * @param fromNbt whether to restore inventory from nbt
	 */
	public static void migrateInvFromEntity(Player player, LivingEntity entity, boolean fromNbt) {
		if (fromNbt) {
			NBT.modifyPersistentData(entity, nbt -> {
				if (!nbt.hasTag(POSSESSED_TAG)) return;

				nbt = nbt.getCompound(POSSESSED_TAG);
				if (nbt == null) return;

				InventoryUtil.restoreFromSlotted(player.getInventory(), nbt, items -> items.forEach(item -> InventoryUtil.addOrDrop(player, item, false)));
				nbt.removeKey(InventoryUtil.STORED_SLOTTED_ITEMS_TAG);
			});
		}

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		PlayerInventory playerInventory = player.getInventory();
		playerInventory.setItemInMainHand(entityEquipment.getItemInMainHand());
		playerInventory.setItemInOffHand(entityEquipment.getItemInOffHand());
		playerInventory.setHelmet(entityEquipment.getHelmet());
		playerInventory.setChestplate(entityEquipment.getChestplate());
		playerInventory.setLeggings(entityEquipment.getLeggings());
		playerInventory.setBoots(entityEquipment.getBoots());

		if (entity instanceof Enderman enderman) {
			BlockData blockData = enderman.getCarriedBlock();
			if (blockData != null) {
				var item = ItemStack.of(blockData.getMaterial());
				item.setBlockData(blockData);
				playerInventory.setItemInMainHand(item);
			}
		}
	}

	private static PotionEffect infinitePotionEffect(PotionEffectType type) {
		return new PotionEffect(type, PotionEffect.INFINITE_DURATION, 0, false, false, false);
	}

	/**
	 * Checks whether the entity can be controlled by player
	 *
	 * @param player player
	 * @param entity entity
	 * @return whether the entity can be controlled by player
	 */
	public static boolean isAllowedForCapture(Player player, LivingEntity entity) {
		if (!isPossessable(entity)) return false;
		if (!entity.getPassengers().isEmpty()) return false;
		if (entity instanceof AbstractHorse) return false;
		return switch (entity) {
			case Animals animals -> true;
			case Fish fish -> true;
			case Squid squid -> true;
			case Golem golem -> true;
			default -> {
				EntityType entityType = entity.getType();
				yield Tag.ENTITY_TYPES_UNDEAD.isTagged(entityType);
			}
		};
	}

	/**
	 * Checks whether the entity has soul
	 *
	 * @param entity entity
	 * @return whether the entity has soul
	 */
	public static boolean isPossessable(LivingEntity entity) {
		EntityType entityType = entity.getType();
		return switch (entityType) {
			case PLAYER, ARMOR_STAND, MANNEQUIN, ENDER_DRAGON, WITHER -> false;
			default -> true;
		};
	}

	/**
	 * Loads player's possessed data
	 *
	 * @param player player
	 * @param data data
	 * @return whether the player is now a possessor
	 */
	public static boolean loadPossessingData(Player player, ReadWriteNBT data) {
		if (!data.hasTag(POSSESSED_TAG)) return false;

		data = data.getCompound(POSSESSED_TAG);
		if (data == null) return false;
		if (!data.hasTag(POSSESSED_ENTITY_DATA, NBTType.NBTTagByteArray)) return false;

		byte[] entityData = data.getByteArray(POSSESSED_ENTITY_DATA);
		assert entityData != null;
		LivingEntity entity = (LivingEntity) Bukkit.getUnsafe().deserializeEntity(entityData, player.getWorld(), true);

		entity.spawnAt(player.getLocation());
		startPossessing(player, entity, null, true);

		return true;
	}

	/**
	 * Saves player's possessed data
	 *
	 * @param player player
	 * @param data data to save into
	 * @param quit whether this saving is due to player quitting
	 */
	public static void savePossessedData(Player player, ReadWriteNBT data, boolean quit) {
		if (!isPossessing(player)) return;

		LivingEntity entity = getPossessed(player);
		if (entity == null) return;

		if (quit)
			player.leaveVehicle();

		byte[] entityData = Bukkit.getUnsafe().serializeEntity(entity);

		if (quit) {
			stopPossessing(player, entity, true);
			entity.remove();
		}

		data = data.getOrCreateCompound(POSSESSED_TAG);
		data.setByteArray(POSSESSED_ENTITY_DATA, entityData);
	}

	/**
	 * Saves all data, should be called on server shutdown
	 */
	public static void saveAllData() {
		for (UUID uuid : POSSESSING) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) continue;

			savePossessedData(player, PlayerDataStorage.getData(uuid), true);
		}
		POSSESSING.clear();
	}

	/**
	 * Adds items rule
	 *
	 * @param rule items rule
	 */
	public static void addItemsDenyRule(Predicate<Player> rule) {
		ITEM_RULES.add(rule);
	}

	/**
	 * Checks if this ghost can hold ghost items
	 *
	 * @param player player
	 * @return whether the player can hold ghost items
	 */
	public static boolean checkPossessedExtraItems(Player player, boolean remove) {
		LivingEntity possessed = getPossessed(player);
		if (possessed == null) {
			removePossessedExtraItems(player);
			return false;
		}

		if (remove)
			removePossessedExtraItems(player);

		for (Predicate<Player> predicate : ITEM_RULES) {
			if (predicate.test(player)) {
				if (!remove)
					removePossessedExtraItems(player);
				return false;
			}
		}

		if (!player.getInventory().contains(RequiemItems.HOST_REVOCATOR) && canHoldHostRevocator(player, possessed)) { // TODO should also be soulbound
			ItemStack currentItem = player.getInventory().getItem(8);
			if (ItemStack.isEmpty(currentItem)) {
				player.getInventory().setItem(8, ItemStack.of(RequiemItems.HOST_REVOCATOR));
			} else {
				if (!player.getInventory().addItem(ItemStack.of(RequiemItems.HOST_REVOCATOR)).isEmpty()) {
					player.getInventory().setItem(8, ItemStack.of(RequiemItems.HOST_REVOCATOR));
					InventoryUtil.addOrDrop(player, currentItem, false);
				}
			}
		}

		return true;
	}

	private static boolean canHoldHostRevocator(Player player, LivingEntity possessed) {
		return possessed instanceof Golem || hasAttritionAtOrHigherThan(player, MAX_ATTRITION_LEVEL);
	}

	private static void removePossessedExtraItems(Player player) {
		removePossessedExtraItems((LivingEntity) player);
		LivingEntity possessed = getPossessed(player);
		if (possessed != null)
			removePossessedExtraItems(possessed);
	}

	private static void removePossessedExtraItems(LivingEntity entity) {
		if (entity.getEquipment() == null) return;

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (!entity.canUseEquipmentSlot(slot)) continue;

			ItemStack item = entity.getEquipment().getItem(slot);
			if (isExtraPossessedItem(item))
				entity.getEquipment().setItem(slot, null);
		}
	}

	/**
	 * Checks whether the item is a possessed control one
	 *
	 * @param item item
	 * @return whether the item is a possessed control one
	 */
	public static boolean isExtraPossessedItem(ItemStack item) {
		return item.getType() == RequiemItems.HOST_REVOCATOR;
	}

	/**
	 * Marks the entity as a resurrected one
	 *
	 * @param entity entity
	 */
	public static void markResurrected(LivingEntity entity) {
		NBT.modifyPersistentData(entity, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(RESURRECTED_ENTITY_TAG, true));
	}

	/**
	 * Checks if this entity is a resurrected one
	 *
	 * @param entity entity
	 * @return whether this entity is a resurrected one
	 */
	public static boolean isResurrected(LivingEntity entity) {
		return NBT.getPersistentData(entity, nbt -> nbt.getOrDefault(RESURRECTED_ENTITY_TAG, false));
	}

	/**
	 * Applies attrition level
	 *
	 * @param player player
	 * @param level level
	 */
	public static void applyAttrition(Player player, int level) {
		int amplifier = Math.clamp(MAX_ATTRITION_LEVEL - level, 0, MAX_ATTRITION_LEVEL);
		player.setLevel(level);
		player.setExp(0F);
		player.addPotionEffect(new PotionEffect(RequiemEffects.ATTRITION, PotionEffect.INFINITE_DURATION, amplifier));
	}

	/**
	 * Checks whether the possessed player can drop items
	 *
	 * @param player player
	 * @return whether the possessed player can drop items
	 */
	public static boolean canDropItems(Player player) {
		return hasAttritionLowerThan(player, 4);
	}

	/**
	 * Checks whether the possessed player can open inventories
	 *
	 * @param player player
	 * @return whether the possessed player can open inventories
	 */
	public static boolean canOpenInventories(Player player) {
		return hasAttritionLowerThan(player, 3);
	}

	/**
	 * Checks whether the possessed player should keep items on death
	 *
	 * @param player player
	 * @return whether the possessed player should keep items on death
	 */
	public static boolean canKeepItemsOnDeath(Player player) {
		return hasAttritionLowerThan(player, 3);
	}

	/**
	 * Checks whether the possessed player should preserve inventory when leaving the host
	 *
	 * @param player player
	 * @return whether the possessed player should preserve inventory when leaving the host
	 */
	public static boolean canPreserveInventory(Player player) {
		return hasAttritionLowerThan(player, 2);
	}

	/**
	 * Checks whether the possessed player can trade
	 *
	 * @param player player
	 * @return whether the possessed player can trade
	 */
	public static boolean canTrade(Player player) {
		return hasAttritionLowerThan(player, 3);
	}

	private static boolean hasAttritionLowerThan(Player player, int level) {
		if (!isPossessingSoft(player)) return true;
		if (!player.hasPotionEffect(RequiemEffects.ATTRITION)) return true;

		PotionEffect potionEffect = player.getPotionEffect(RequiemEffects.ATTRITION);
		return potionEffect != null && potionEffect.getAmplifier() < level;
	}

	private static boolean hasAttritionAtOrHigherThan(Player player, int level) {
		return !hasAttritionLowerThan(player, level);
	}

}
