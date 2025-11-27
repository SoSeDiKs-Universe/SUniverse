package me.sosedik.utilizer.listener.misc;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import me.sosedik.utilizer.util.NbtProxies;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Makes specified effects immune to death
 */
@NullMarked
public class DeathImmuneEffects implements Listener {

	private static final String EFFECTS_TAG = "death_effects";
	private static final Set<NamespacedKey> IMMUNE_EFFECTS = new HashSet<>();

	static {
		addDeathImmune(PotionEffectType.UNLUCK);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEffect(EntityPotionEffectEvent event) {
		if (event.getCause() != EntityPotionEffectEvent.Cause.DEATH) return;
		if (event.getNewEffect() != null) return;

		PotionEffect oldEffect = event.getOldEffect();
		if (oldEffect == null) return;
		if (!isDeathImmune(oldEffect.getType())) return;
		if (!(event.getEntity() instanceof Player player)) return;

		event.setCancelled(true); // Death removes the effect regardless, hence persisting manually
		ReadWriteNBT data = PlayerDataStorage.getData(player);
		ReadWriteNBTCompoundList effects = data.getCompoundList(EFFECTS_TAG);
		effects.addCompound().set(null, oldEffect, NbtProxies.POTION_EFFECT);
	}

	@EventHandler
	public void onRespawn(PlayerPostRespawnEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT data = PlayerDataStorage.getData(player);
		if (!data.hasTag(EFFECTS_TAG)) return;

		ReadWriteNBTCompoundList effects = data.getCompoundList(EFFECTS_TAG);
		effects.forEach(effectNbt -> {
			PotionEffect potionEffect = effectNbt.get(null, NbtProxies.POTION_EFFECT);
			if (potionEffect != null)
				player.addPotionEffect(potionEffect);
		});
		data.removeKey(EFFECTS_TAG);
	}

	@EventHandler
	public void onSave(PlayerDataSaveEvent event) {
		ReadWriteNBT preData = event.getPreData();
		if (!preData.hasTag(EFFECTS_TAG)) return;

		ReadWriteNBT data = event.getData();
		ReadWriteNBTCompoundList effects = data.getCompoundList(EFFECTS_TAG);
		preData.getCompoundList(EFFECTS_TAG).forEach(effects::addCompound);
	}

	@EventHandler
	public void onLoad(PlayerDataLoadedEvent event) {
		ReadWriteNBT preData = event.getData();
		if (!preData.hasTag(EFFECTS_TAG)) return;

		ReadWriteNBT data = event.getBackupData();
		ReadWriteNBTCompoundList effects = data.getCompoundList(EFFECTS_TAG);
		preData.getCompoundList(EFFECTS_TAG).forEach(effects::addCompound);
	}

	/**
	 * Marks potion effect types as immune to death
	 *
	 * @param types potion effect types
	 */
	public static void addDeathImmune(PotionEffectType ... types) {
		IMMUNE_EFFECTS.addAll(Arrays.stream(types).map(Keyed::getKey).toList());
	}

	/**
	 * Checks whether this effect type is immune to death
	 *
	 * @param type potion effect type
	 * @return whether this effect is immune to death
	 */
	public static boolean isDeathImmune(PotionEffectType type) {
		return IMMUNE_EFFECTS.contains(type.getKey());
	}

}
