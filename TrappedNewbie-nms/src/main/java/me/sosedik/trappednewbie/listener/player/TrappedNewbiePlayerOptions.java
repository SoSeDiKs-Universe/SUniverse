package me.sosedik.trappednewbie.listener.player;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.sosedik.utilizer.api.event.player.PlayerOpenPreferencesEvent;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.listener.player.PlayerOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Plugin's player preferences
 */
@NullMarked
public class TrappedNewbiePlayerOptions implements Listener {

	private static final String ADV_HELPER_TAG = "adv_helper";

	@EventHandler(ignoreCancelled = true)
	public void onPreferences(PlayerOpenPreferencesEvent event) {
		Player player = event.getPlayer();
		var messenger = Messenger.messenger(player);

		ReadWriteNBT preferences = PlayerOptions.getPreferences(player);

		addBoolPreference(event, messenger, ADV_HELPER_TAG, preferences.getOrDefault(ADV_HELPER_TAG, false));
	}

	private void addBoolPreference(PlayerOpenPreferencesEvent event, Messenger messenger, String preference, boolean initial) {
		event.getPreferences().add(DialogInput.bool(preference, messenger.getMessage("preferences.preference." + preference)).initial(initial).build());
	}

	@EventHandler
	public void onPreferences(PlayerCustomClickEvent event) {
		if (!event.getIdentifier().equals(PlayerOptions.PREFERENCES_ACTION)) return;
		if (!(event.getCommonConnection() instanceof PlayerGameConnection connection)) return;

		DialogResponseView view = event.getDialogResponseView();
		if (view == null) return;

		ReadWriteNBT preferences = PlayerOptions.getPreferences(connection.getPlayer());

		preferences.setBoolean(ADV_HELPER_TAG, view.getBoolean(ADV_HELPER_TAG));
	}

	public static boolean showAdvancementHelper(Player player) {
		return PlayerOptions.getPreferences(player).getOrDefault(ADV_HELPER_TAG, false);
	}

}
