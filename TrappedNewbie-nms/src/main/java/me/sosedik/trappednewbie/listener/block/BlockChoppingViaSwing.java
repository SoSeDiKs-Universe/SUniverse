package me.sosedik.trappednewbie.listener.block;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.impl.blockstorage.ChoppingBlockStorage;
import me.sosedik.utilizer.listener.BlockStorage;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Chop blocks with swings, not just interactions
 */
@NullMarked
public class BlockChoppingViaSwing implements Listener {

	private final Set<UUID> onCooldown = new HashSet<>();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChopping(PlayerArmSwingEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if (this.onCooldown.contains(uuid)) return;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (!Tag.ITEMS_AXES.isTagged(item.getType())) return;

		Block exact = player.getTargetBlockExact(4, FluidCollisionMode.ALWAYS);
		if (exact == null) return;
		if (!TrappedNewbieTags.CHOPPING_BLOCKS.isTagged(exact.getType())) return;

		if (!(BlockStorage.getByLoc(exact) instanceof ChoppingBlockStorage storage)) return;
		if (!storage.hasItemToChop()) return;

		this.onCooldown.add(uuid);
		TrappedNewbie.scheduler().async(() -> this.onCooldown.remove(uuid), 10L);

		var interactEvent = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, item, exact, BlockFace.SELF, EquipmentSlot.HAND);
		storage.onInteract(interactEvent);
	}

}
