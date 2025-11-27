package me.sosedik.miscme.listener.entity;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Drops upon shearing entities
 */
@NullMarked
public class ShearableEntities implements Listener {

	private static final Map<EntityType, ShearableBehavior> SHEARABLE = new HashMap<>();
	private static final String LAST_SHEAR_TAG = "last_shear";

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onShear(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
		if (!entity.isValid()) return;
		if (entity instanceof Ageable ageable && !ageable.isAdult()) return;

		ShearableBehavior shearableBehavior = SHEARABLE.get(entity.getType());
		if (shearableBehavior == null) return;
		if (shearableBehavior.hasShearCooldown(entity)) return;
		if (shearableBehavior.entityCheck != null && !shearableBehavior.entityCheck.test(entity)) return;

		Player player = event.getPlayer();
		if (tryShearingEntity(player, entity, EquipmentSlot.HAND) || tryShearingEntity(player, entity, EquipmentSlot.OFF_HAND)) {
			event.setCancelled(true);
			NBT.modifyPersistentData(entity, (Consumer<ReadWriteNBT>) nbt -> nbt.setInteger(LAST_SHEAR_TAG, entity.getTicksLived()));
			if (shearableBehavior.shearAction != null)
				shearableBehavior.shearAction.accept(entity);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Ageable ageable && !ageable.isAdult()) return;

		ShearableBehavior shearableBehavior = SHEARABLE.get(entity.getType());
		if (shearableBehavior == null) return;
		if (shearableBehavior.deathAction == null) return;
		if (!shearableBehavior.hasShearCooldown(entity)) return;
		if (shearableBehavior.entityCheck != null && !shearableBehavior.entityCheck.test(entity)) return;

		shearableBehavior.deathAction.accept(event);
	}

	private boolean tryShearingEntity(Player player, LivingEntity entity, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!UtilizerTags.SHEARS.isTagged(item.getType())) return false;

		item.damage(1, player);
		entity.emitSound(Sound.ENTITY_SHEEP_SHEAR, 1F, 1F);

		player.swingHand(hand);
		if (hand == EquipmentSlot.OFF_HAND && entity instanceof Vehicle)
			MiscMe.scheduler().sync(player::swingOffHand, 5L);

		return true;
	}

	public static class ShearableBehavior {

		private @Nullable Predicate<LivingEntity> entityCheck;
		private @Nullable Consumer<LivingEntity> shearAction;
		private @Nullable Consumer<EntityDeathEvent> deathAction;
		private final int delay;

		public ShearableBehavior() {
			this(20 * 60 * 3);
		}

		public ShearableBehavior(int delay) {
			this.delay = delay;
		}

		public ShearableBehavior withEntityCheck(@Nullable Predicate<LivingEntity> entityCheck) {
			this.entityCheck = entityCheck;
			return this;
		}

		public ShearableBehavior withShearAction(@Nullable Consumer<LivingEntity> shearAction) {
			this.shearAction = shearAction;
			return this;
		}

		public ShearableBehavior withDeathAction(@Nullable Consumer<EntityDeathEvent> deathAction) {
			this.deathAction = deathAction;
			return this;
		}

		public ShearableBehavior withDrop(Material type, int minAmount, int maxAmount) {
			return withShearAction(entity -> entity.getWorld().dropItemNaturally(entity.getLocation(), ItemStack.of(type, MathUtil.getRandomIntInRange(minAmount, maxAmount))))
				.withDeathAction(event -> event.getDrops().removeIf(item -> item.getType() == type));
		}

		public ShearableBehavior registerFor(EntityType... types) {
			for (EntityType type : types)
				SHEARABLE.put(type, this);
			return this;
		}

		boolean hasShearCooldown(LivingEntity entity) {
			int ticksLived = entity.getTicksLived();
			int lastShearTick = NBT.getPersistentData(entity, nbt -> nbt.getOrDefault(LAST_SHEAR_TAG, -1));
			return lastShearTick != -1 && ticksLived - lastShearTick < this.delay;
		}

	}

}
