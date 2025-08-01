package me.sosedik.trappednewbie.listener.item;

import me.sosedik.packetadvancements.util.ToastMessage;
import me.sosedik.socializer.listener.FriendlyPlayers;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Handles friendship letters
 */
@NullMarked
public class FriendshipLetters implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onFriendship(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player playerTo)) return;
		if (!playerTo.getInventory().getItemInMainHand().isEmpty()) return;

		Player playerFrom = event.getPlayer();
		ItemStack item = playerFrom.getInventory().getItemInMainHand();
		if (item.getType() != TrappedNewbieItems.LETTER) return;

		if (LetterModifier.isBound(item, playerTo, playerFrom)) {
			playerFrom.swingMainHand();
			playerFrom.getInventory().setItemInMainHand(null);
			playerTo.swingMainHand();
			FriendlyPlayers.getFriendshipData(playerFrom).befriend(playerTo);
			ToastMessage.showToast(playerFrom, ItemUtil.playerHead(playerTo), Messenger.messenger(playerFrom).getMessage("friendship.player", raw("player", playerTo.displayName())));
			ToastMessage.showToast(playerTo, ItemUtil.playerHead(playerFrom), Messenger.messenger(playerTo).getMessage("friendship.player", raw("player", playerFrom.displayName())));
			return;
		}
		if (FriendlyPlayers.getFriendshipData(playerFrom).isFriendsWith(playerTo)) return;

		ItemStack boundLetter = LetterModifier.tryToBind(item.asOne(), playerFrom, playerTo);
		if (boundLetter == null) return;

		playerFrom.swingMainHand();
		item.subtract();
		playerTo.swingMainHand();
		playerFrom.getInventory().setItemInMainHand(boundLetter);

		event.setCancelled(true);
	}

}
