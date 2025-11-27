package me.sosedik.trappednewbie.listener.entity;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jspecify.annotations.NullMarked;

import java.util.Random;
import java.util.function.Consumer;

/**
 * Creepers can be sheared!
 */
@NullMarked
public class ShearableCreepers implements Listener {

	public static final String SHEARED_TAG = "sheared";

	private static final Random RANDOM = new Random();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onShear(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Creeper creeper)) return;
		if (creeper.isPowered()) return;

		Player player = event.getPlayer();
		if (!TrappedNewbieAdvancements.IGNITE_CHARGED_CREEPER_MIDAIR.isDone(player)) return;

		if (isSheared(creeper)) {
			creeper.emitSound(Sound.BLOCK_LEVER_CLICK, 0.25F, 1.8F);
			return;
		}

		if (tryDefusing(player, creeper, EquipmentSlot.HAND) || tryDefusing(player, creeper, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Creeper creeper)) return;
		if (!isSheared(creeper)) return;

		event.getDrops().clear();
		event.setDroppedExp(0);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPrime(ExplosionPrimeEvent event) {
		if (!(event.getEntity() instanceof Creeper creeper)) return;
		if (!isSheared(creeper)) return;

		event.setCancelled(true);
		makeFireworkExplosion(creeper);
	}

	private void makeFireworkExplosion(Creeper creeper) {
		creeper.setTarget(null);
		creeper.setInvulnerable(true);

		TrappedNewbie.scheduler().sync(creeper::remove, 40L);
		for (int j = 0; j < 5; j++) {
			TrappedNewbie.scheduler().sync(() -> {
				for (int i = 1; i < 4; i++) {
					TrappedNewbie.scheduler().sync(() -> {
						creeper.getWorld().spawn(creeper.getEyeLocation(), Firework.class, firework -> {
							FireworkMeta fireworkMeta = firework.getFireworkMeta();
							Color mainColor = Color.fromRGB(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
							FireworkEffect fwEffect = FireworkEffect.builder().withColor(mainColor).with(FireworkEffect.Type.CREEPER).build();
							fireworkMeta.addEffect(fwEffect);
							fireworkMeta.setPower(1);
							firework.setFireworkMeta(fireworkMeta);
						});
					}, i * 5L);
				}
				TrappedNewbie.scheduler().sync(() -> {
					int ticks = 1;
					for (int i = 0; i < 10; i++) {
						TrappedNewbie.scheduler().sync(() -> creeper.setVelocity(creeper.getVelocity().setY(0.5)), ticks);
						ticks += i;
					}
					TrappedNewbie.scheduler().sync(() -> creeper.getWorld().createExplosion(creeper, 3, true, true), ticks + 8L);
				}, 18L);
			}, j * 10L);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onExplode(EntityExplodeEvent event) {
		if (!(event.getEntity() instanceof Creeper creeper)) return;
		if (!isSheared(creeper)) return;

		event.blockList().clear();
	}

	private boolean tryDefusing(Player player, Creeper creeper, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!UtilizerTags.SHEARS.isTagged(item.getType())) return false;

		creeper.setExplosionRadius(0);
		NBT.modifyPersistentData(creeper, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(SHEARED_TAG, true));

		player.swingHand(hand);
		item.damage(1, player);

		creeper.emitSound(Sound.BLOCK_LEVER_CLICK, 0.25F, 0.5F);
		creeper.ignite(player);
		creeper.getWorld().spawnParticle(Particle.SMOKE, creeper.getLocation().addY(0.5), 20, 0.01F, 0.1F, 0.01F, 0.1F);
		creeper.getWorld().spawnParticle(Particle.ENCHANT, creeper.getLocation().addY(0.9), 10, 0.01F, 0.2F, 0.01F, 0.1F);

		return true;
	}

	public static boolean isSheared(Creeper creeper) {
		return NBT.getPersistentData(creeper, nbt -> nbt.getOrDefault(SHEARED_TAG, false));
	}

}
