package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

// MCCheck: 1.21.10, new mob heads
@NullMarked
public class KillAMobWearingItsHeadAdvancement implements Listener {

	private static final Map<EntityType, Material> HEADS = new HashMap<>();

	static {
		addMobHead(EntityType.CREEPER, Material.CREEPER_HEAD);
		addMobHead(EntityType.ZOMBIE, Material.ZOMBIE_HEAD);
		addMobHead(EntityType.SKELETON, Material.SKELETON_SKULL);
		addMobHead(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SKULL);
		addMobHead(EntityType.PIGLIN, Material.PIGLIN_HEAD);
		addMobHead(EntityType.ENDER_DRAGON, Material.DRAGON_HEAD);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKill(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Mob entity)) return;

		Player killer = entity.getKiller();
		if (killer == null) return;

		Material headType = HEADS.get(entity.getType());
		if (headType == null) return;
		if (!isWearingHat(killer, headType)) return;

		TrappedNewbieAdvancements.KILL_A_MOB_WEARING_ITS_HEAD.awardAllCriteria(killer);
	}

	private boolean isWearingHat(Player player, Material type) {
		if (ItemStack.isType(player.getEquipment().getHelmet(), type))
			return true;

		VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
		return visualArmor.canUseVisualArmor() && visualArmor.hasHelmet() && visualArmor.getHelmet().getType() == type;
	}

	/**
	 * Gets registered heads
	 *
	 * @return entity type to its head map
	 */
	public static Map<EntityType, Material> getHeads() {
		return HEADS;
	}

	public static void addMobHead(EntityType entityType, Material headType) {
		HEADS.put(entityType, headType);
	}

}
