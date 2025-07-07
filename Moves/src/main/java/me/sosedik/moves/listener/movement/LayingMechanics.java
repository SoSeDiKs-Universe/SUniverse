package me.sosedik.moves.listener.movement;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.sosedik.utilizer.util.MetadataUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

/**
 * Laying everywhere!
 */
@NullMarked
public class LayingMechanics implements Listener {

	private static final String LOCATION_KEY = "fake_bed_loc";

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBedLeave(PlayerBedLeaveEvent event) {
		Player player = event.getPlayer();
		var metadata = MetadataUtil.removeMetadata(player, LOCATION_KEY);
		if (metadata == null) return;

		Block block = player.getLocation().getBlock();
		if (block.getBlockData() instanceof Bed bed && bed.isOccupied()) {
			bed.setOccupied(false);
			block.setBlockData(bed);
		}

		player.setSleepingIgnored(false);
		Location bedLoc = metadata.get(Location.class);
		BlockData blockData = bedLoc.getBlock().getBlockData();
		Collection<Player> nearbyPlayers = event.getBed().getLocation().getNearbyPlayers(128, 600);
		nearbyPlayers.forEach(np -> np.sendBlockChange(bedLoc, blockData));
	}

	@EventHandler
	public void onJoinCheck(PlayerClientLoadedWorldEvent event) {
		Player player = event.getPlayer();
		Collection<Player> nearbyPlayers = player.getLocation().getNearbyPlayers(128, 600);
		nearbyPlayers.forEach(nearbyPlayer -> {
			var metadata = MetadataUtil.getMetadata(nearbyPlayer, LOCATION_KEY);
			if (metadata == null) return;

			Bed fakeBed = (Bed) Material.WHITE_BED.createBlockData();
			fakeBed.setPart(Bed.Part.FOOT);
			fakeBed.setFacing(nearbyPlayer.getFacing().getOppositeFace());
			player.sendBlockChange(metadata.get(Location.class), fakeBed);
		});
	}

	/**
	 * Lay the player
	 *
	 * @param player player
	 * @param loc location
	 * @param facing facing
	 */
	public static void lay(Player player, Location loc, BlockFace facing) {
		SittingMechanics.unSit(player, false);

		Bed fakeBedBlockData = (Bed) Material.WHITE_BED.createBlockData();
		fakeBedBlockData.setPart(Bed.Part.FOOT);
		fakeBedBlockData.setFacing(facing);

		Location bedLoc = loc.clone();
		if (loc.getBlockY() > loc.getWorld().getMaxHeight() - 50) {
			bedLoc.addY(-30);
		} else {
			bedLoc.setY(loc.getWorld().getMaxHeight() - 1D);
		}
		MetadataUtil.setMetadata(player, LOCATION_KEY, bedLoc);
		Collection<Player> nearbyPlayers = loc.getNearbyPlayers(128, 600);
		nearbyPlayers.forEach(np -> np.sendBlockChange(bedLoc, fakeBedBlockData));

		player.lay(loc, bedLoc);
	}

}
