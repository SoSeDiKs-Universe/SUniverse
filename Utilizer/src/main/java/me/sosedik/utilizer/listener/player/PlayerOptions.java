package me.sosedik.utilizer.listener.player;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.book.BookType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.recipe.RecipeBookSettings;
import com.github.retrooper.packetevents.protocol.recipe.RecipeBookType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSetRecipeBookState;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRecipeBookSettings;
import com.google.common.base.Preconditions;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.event.player.PlayerOpenPreferencesEvent;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRecipeBookSettingsChangeEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * Controls player's options and makes recipe book's filtering button an options button
 */
@NullMarked
public class PlayerOptions implements Listener, PacketListener {

	public static final Key PREFERENCES_ACTION = Utilizer.utilizerKey("preferences");
	private static final String PREFERENCES_TAG = "preferences";
	private static final String AM_PM_TAG = "am_pm";

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPlayedBefore())
			openRecipeBook(player);
	}

	@EventHandler
	public void onSettings(PlayerRecipeBookSettingsChangeEvent event) {
		if (!event.isFiltering()) return;

		Player player = event.getPlayer();

		var preferencesEvent = new PlayerOpenPreferencesEvent(player);
		if (!preferencesEvent.callEvent()) return;

		var messenger = Messenger.messenger(player);
		Component title = messenger.getMessage("preferences.title");
		Component done = messenger.getMessage("preferences.done");
		Dialog dialog = Dialog.create(builder -> builder.empty()
			.base(DialogBase.builder(title).inputs(preferencesEvent.getPreferences()).build())
			.type(DialogType.notice(ActionButton.builder(done).action(DialogAction.customClick(PREFERENCES_ACTION, null)).build()))
		);
		player.showDialog(dialog);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPreferences(PlayerOpenPreferencesEvent event) {
		Player player = event.getPlayer();
		var messenger = Messenger.messenger(player);

		Component label = messenger.getMessage("preferences.preference.am_pm");
		var preference = DialogInput.bool(AM_PM_TAG, label).initial(false).build();

		event.getPreferences().add(preference);
	}

	@EventHandler
	public void onPreferences(PlayerCustomClickEvent event) {
		if (!event.getIdentifier().equals(PREFERENCES_ACTION)) return;
		if (!(event.getCommonConnection() instanceof PlayerGameConnection connection)) return;

		DialogResponseView view = event.getDialogResponseView();
		if (view == null) return;

		ReadWriteNBT preferences = getPreferences(connection.getPlayer());
		preferences.setBoolean(AM_PM_TAG, view.getBoolean(AM_PM_TAG));
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
		if (preData.hasTag(PREFERENCES_TAG))
			data.getOrCreateCompound(PREFERENCES_TAG).mergeCompound(preData.getOrCreateCompound(PREFERENCES_TAG));
	}

	@Override
	public void onPacketReceive(PacketReceiveEvent event) {
		if (event.getPacketType() != PacketType.Play.Client.SET_RECIPE_BOOK_STATE) return;

		var packet = new WrapperPlayClientSetRecipeBookState(event);
		if (!packet.isFilterActive()) return;

		forceNoFiltering(event.getPlayer(), packet);
	}

	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.RECIPE_BOOK_SETTINGS) return;

		var packet = new WrapperPlayServerRecipeBookSettings(event);
		RecipeBookSettings settings = packet.getSettings();
		settings.getStates().values().forEach(state -> state.setFiltering(false));
	}

	private void openRecipeBook(Player player) {
		Map<RecipeBookType, RecipeBookSettings.TypeState> states = new HashMap<>(RecipeBookType.values().length);
		for (RecipeBookType recipeBookType : RecipeBookType.values())
			states.put(recipeBookType, new RecipeBookSettings.TypeState(true, false));
		var settings = new RecipeBookSettings(states);
		var packet = new WrapperPlayServerRecipeBookSettings(settings);
		PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
	}

	private void forceNoFiltering(Player player, WrapperPlayClientSetRecipeBookState clientPacket) {
		var settings = new RecipeBookSettings(new HashMap<>(Map.of(getByBookType(clientPacket.getBookType()), new RecipeBookSettings.TypeState(clientPacket.isBookOpen(), false))));
		var packet = new WrapperPlayServerRecipeBookSettings(settings);
		PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
	}

	private RecipeBookType getByBookType(BookType type) {
		return switch (type) {
			case CRAFTING -> RecipeBookType.CRAFTING;
			case FURNACE -> RecipeBookType.FURNACE;
			case BLAST_FURNACE -> RecipeBookType.BLAST_FURNACE;
			case SMOKER -> RecipeBookType.SMOKER;
		};
	}

	public static boolean isAmPm(Player player) {
		return getPreferences(player).getOrDefault(AM_PM_TAG, false);
	}

	public static ReadWriteNBT getPreferences(Player player) {
		Preconditions.checkArgument(player.isOnline(), "Player must be online");
		return PlayerDataStorage.getData(player).getOrCreateCompound(PREFERENCES_TAG);
	}

}
