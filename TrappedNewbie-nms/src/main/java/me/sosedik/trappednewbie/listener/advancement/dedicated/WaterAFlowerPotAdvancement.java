package me.sosedik.trappednewbie.listener.advancement.dedicated;

import io.papermc.paper.event.entity.WaterBottleSplashEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class WaterAFlowerPotAdvancement implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPotion(WaterBottleSplashEvent event) {
		Block hitBlock = event.getHitBlock();
		BlockFace hitBlockFace = event.getHitBlockFace();
		if (hitBlock == null) return;
		if (hitBlockFace == null) return;

		if (!Tag.FLOWER_POTS.isTagged(hitBlock.getType())) {
			hitBlock = hitBlock.getRelative(hitBlockFace);
			if (!Tag.FLOWER_POTS.isTagged(hitBlock.getType())) return;
		}

		ThrownPotion potion = event.getPotion();
		UUID ownerUuid = potion.getOwnerUniqueId();
		if (ownerUuid == null) return;

		Player player = Bukkit.getPlayer(ownerUuid);
		if (player == null) return;
		if (!player.getLocation().getBlock().equals(hitBlock)) return;

		TrappedNewbieAdvancements.WATER_A_FLOWER_POT.awardAllCriteria(player);
	}

}
