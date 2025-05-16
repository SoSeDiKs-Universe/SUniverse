package me.sosedik.trappednewbie.listener.item;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.kiterino.event.entity.ItemConsumeEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Cutting yourself with glass shards
 */
@NullMarked
public class GlassShardCuts implements Listener {

	public static final DamageType GLASS_SHARD = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(TrappedNewbie.trappedNewbieKey("glass_shard"));

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUse(PlayerItemConsumeEvent event) {
		tryToUseGlassShard(event);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUse(EntityItemConsumeEvent event) {
		tryToUseGlassShard(event);
	}

	public void tryToUseGlassShard(ItemConsumeEvent event) {
		if (!TrappedNewbieTags.GLASS_SHARDS.isTagged(event.getItem().getType())) return;

		LivingEntity entity = event.getEntity();
		event.setReplacement(event.getItem().subtract());
		TrappedNewbie.scheduler().sync(() -> {
			if (!entity.isValid()) return;

			double health = entity.getHealth();
			double damage = Math.max(1, health - 0.5);

			entity.damage(damage, DamageSource.builder(GLASS_SHARD).build());
		}, 1L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (event.getDamageSource().getDamageType() != GLASS_SHARD) return;

		entity.emitSound(Sound.ENTITY_GHAST_SCREAM,1F, 0.9F + (float) Math.random() * 0.2F);
		entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 4));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40 * 20, 4));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60 * 20, 0));
		entity.getWorld().playEffect(entity.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
	}

}
