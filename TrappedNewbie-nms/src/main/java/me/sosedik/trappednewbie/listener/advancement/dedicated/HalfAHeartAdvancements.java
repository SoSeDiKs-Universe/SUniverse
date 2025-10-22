package me.sosedik.trappednewbie.listener.advancement.dedicated;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HalfAHeartAdvancements implements Listener {

	private static final String HALF_A_HEART_TICKS_TAG = "half_a_heart_ticks";
	private static final String HALF_A_HEART_TICKS_NO_ARMOR_TAG = "half_a_heart_no_armor_ticks";
	private static final String HALF_A_HEART_TICKS_NO_ARMOR_NO_EFFECTS_TAG = "half_a_heart_no_armor_no_effects_ticks";

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTick(ServerTickEndEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			ReadWriteNBT playerData = PlayerDataStorage.getData(player);
			if (shouldReset(player)) {
				playerData.setInteger(HALF_A_HEART_TICKS_TAG, 0);
				playerData.setInteger(HALF_A_HEART_TICKS_NO_ARMOR_TAG, 0);
				playerData.setInteger(HALF_A_HEART_TICKS_NO_ARMOR_NO_EFFECTS_TAG, 0);
				continue;
			}

			boolean armor = hasAnyArmor(player);
			boolean effects = !player.getActivePotionEffects().isEmpty();
			int halfAHeartTicks = playerData.getOrDefault(HALF_A_HEART_TICKS_TAG, 0) + 1;
			playerData.setInteger(HALF_A_HEART_TICKS_TAG, halfAHeartTicks);
			if (halfAHeartTicks >= 60 * 20) TrappedNewbieAdvancements.HALF_A_HEART_1M.awardAllCriteria(player);
			if (armor) {
				playerData.setInteger(HALF_A_HEART_TICKS_NO_ARMOR_TAG, 0);
				playerData.setInteger(HALF_A_HEART_TICKS_NO_ARMOR_NO_EFFECTS_TAG, 0);
				continue;
			}
			halfAHeartTicks = playerData.getOrDefault(HALF_A_HEART_TICKS_NO_ARMOR_TAG, 0) + 1;
			playerData.setInteger(HALF_A_HEART_TICKS_NO_ARMOR_TAG, halfAHeartTicks);
			if (halfAHeartTicks >= 60 * 60 * 20) TrappedNewbieAdvancements.HALF_A_HEART_1H.awardAllCriteria(player);
			if (effects) {
				playerData.setInteger(HALF_A_HEART_TICKS_NO_ARMOR_NO_EFFECTS_TAG, 0);
				continue;
			}
			halfAHeartTicks = playerData.getOrDefault(HALF_A_HEART_TICKS_NO_ARMOR_NO_EFFECTS_TAG, 0) + 1;
			playerData.setInteger(HALF_A_HEART_TICKS_NO_ARMOR_NO_EFFECTS_TAG, halfAHeartTicks);
			if (halfAHeartTicks >= 6 * 60 * 60 * 20) TrappedNewbieAdvancements.HALF_A_HEART_6H.awardAllCriteria(player);
		}
	}

	@EventHandler
	public void onSave(PlayerDataSaveEvent event) {
		persistData(event.getPreData(), event.getData());
	}

	@EventHandler
	public void onLoad(PlayerDataLoadedEvent event) {
		persistData(event.getData(), event.getBackupData());
	}

	private void persistData(ReadWriteNBT preData, ReadWriteNBT data) {
		if (preData.hasTag(HALF_A_HEART_TICKS_TAG)) data.setInteger(HALF_A_HEART_TICKS_TAG, data.getInteger(HALF_A_HEART_TICKS_TAG));
		if (preData.hasTag(HALF_A_HEART_TICKS_NO_ARMOR_TAG)) data.setInteger(HALF_A_HEART_TICKS_NO_ARMOR_TAG, data.getInteger(HALF_A_HEART_TICKS_NO_ARMOR_TAG));
		if (preData.hasTag(HALF_A_HEART_TICKS_NO_ARMOR_NO_EFFECTS_TAG)) data.setInteger(HALF_A_HEART_TICKS_NO_ARMOR_NO_EFFECTS_TAG, data.getInteger(HALF_A_HEART_TICKS_NO_ARMOR_NO_EFFECTS_TAG));
	}

	private boolean shouldReset(Player player) {
		return player.isDead() || player.getHealth() > 1;
	}

	private boolean hasAnyArmor(Player player) {
		for (ItemStack item : player.getInventory().getArmorContents()) {
			if (!ItemStack.isEmpty(item))
				return true;
		}

		VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
		if (!visualArmor.canUseVisualArmor()) return false;

		for (ItemStack item : visualArmor.getArmorContents()) {
			if (item.isEmpty())
				return true;
		}
		return false;
	}

}
