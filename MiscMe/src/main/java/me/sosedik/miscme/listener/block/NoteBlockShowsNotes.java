package me.sosedik.miscme.listener.block;

import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.NotePlayEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

/**
 * Display notes when playing note block
 */
// MCCheck: 1.21.4, new instruments
@NullMarked
public class NoteBlockShowsNotes implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlay(NotePlayEvent event) {
		Player player = event.getWhoPlayed();
		if (player == null) return;

		Note note = event.getNote();
		var sb = new StringBuilder()
			.append("<")
			.append(note.getColor().asHexString())
			.append(">")
			.append(note.getTone().name());
		if (note.isSharped())
			sb.append('#');
		sb.append(note.getOctave())
			.append(" <instrument> ")
			.append('(').append(note.getId()).append(')');

		var messenger = Messenger.messenger(player);
		Component instrument = messenger.getMessage("note_block.note." + event.getInstrument().name().toLowerCase(Locale.ROOT) + ".name");
		Component display = messenger.miniMessage().deserialize(sb.toString(), Mini.raw("instrument", instrument));
		HudMessenger.of(player).displayMessage(display);
	}

}
