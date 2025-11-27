package me.sosedik.trappednewbie.listener.block;

import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieDamageTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

/**
 * It hurts to break some blocks empty-handed without gloves
 */
@NullMarked
public class BlockBreakHurts implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (!TrappedNewbieTags.PRICKY_BLOCKS.isTagged(block.getType())) return;

		Player player = event.getPlayer();
		if (player.getGameMode().isInvulnerable()) return;
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;

		VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
		if (!visualArmor.canUseVisualArmor()) return;

		if (visualArmor.hasNonBrokenGloves()) {
			if (Math.random() < 0.15)
				visualArmor.setGloves(visualArmor.getGloves().damage(1, player));
			return;
		}

		player.damage(1, DamageSource.builder(TrappedNewbieDamageTypes.PRICKY_BLOCK).build());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (event.getDamageSource().getDamageType() != TrappedNewbieDamageTypes.PRICKY_BLOCK) return;

		entity.getWorld().playEffect(entity.getEyeLocation().addY(-(entity.getHeight() / 2)), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
	}

}
