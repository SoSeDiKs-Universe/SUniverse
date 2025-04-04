package me.sosedik.miscme.listener.block;

import me.sosedik.miscme.api.event.player.PlayerBlockKnockEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

/**
 * Knock on blocks!
 */
@NullMarked
public class BlockKnocking implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onKnock(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		if (player.getGameMode().isInvulnerable()) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
//		if (ToolType.getByBlock(block).contains(ToolType.getByItem(player.getInventory().getItemInMainHand()))) return; // TODO tool types

		Material blockType = block.getType();
		if (Tag.WOODEN_DOORS.isTagged(blockType))
			tryToKnock(player, block, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1.2F);
		else if (Tag.WOODEN_TRAPDOORS.isTagged(blockType))
			tryToKnock(player, block, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1.3F);
		else if (Tag.DOORS.isTagged(blockType))
			tryToKnock(player, block, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.2F);
		else if (Tag.TRAPDOORS.isTagged(blockType))
			tryToKnock(player, block, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.3F);
	}

	private void tryToKnock(Player player, Block block, Sound sound, float pitch) {
		if (!new PlayerBlockKnockEvent(player, block, player.getInventory().getItemInMainHand().getType() == Material.AIR).callEvent()) return;

		block.emitSound(sound, 1F, pitch);
	}

}
