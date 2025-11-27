package me.sosedik.trappednewbie.impl.blockstorage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.listener.player.TotemRituals;
import net.kyori.adventure.sound.Sound;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DrumBlockStorage extends DisplayBlockStorage {

	private static final NamespacedKey DRUM_SOUND = ResourceLib.getSound(TrappedNewbie.trappedNewbieKey("block/drum"));

	public DrumBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
	}

	@Override
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		player.swingMainHand();

		getBlock().emitSound(Sound.sound(DRUM_SOUND, Sound.Source.PLAYER, 1F, 0.9F + (float) Math.random() * 0.2F));
		TotemRituals.playedInstrument(player, requireMatchingMaterial(), getBlock().getLocation().center());
	}

}
