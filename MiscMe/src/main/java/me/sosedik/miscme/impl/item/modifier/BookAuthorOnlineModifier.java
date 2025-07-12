package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WrittenBookContent;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import static me.sosedik.utilizer.api.message.Mini.combined;

/**
 * Books show online status of the author in tooltip
 */
@NullMarked
public class BookAuthorOnlineModifier extends ItemModifier {

	private static final Pattern VALID_NICKNAME = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
	private static final Component ONLINE = Component.text(" ● ", NamedTextColor.GREEN);
	private static final Component OFFLINE = Component.text(" ● ", NamedTextColor.RED);

	public BookAuthorOnlineModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleName()) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.WRITTEN_BOOK_CONTENT)) return ModificationResult.PASS;

		WrittenBookContent writtenBookContent = item.getData(DataComponentTypes.WRITTEN_BOOK_CONTENT);
		assert writtenBookContent != null;
		String author = writtenBookContent.author();
		if (!VALID_NICKNAME.matcher(author).matches()) return ModificationResult.PASS;

		Component status = getStatus(author);
		if (status == null) return ModificationResult.PASS;

		// TODO replace with #toBuilder once available
		writtenBookContent = WrittenBookContent.writtenBookContent(writtenBookContent.title(), LegacyComponentSerializer.legacySection().serialize(status))
			.generation(writtenBookContent.generation())
			.resolved(writtenBookContent.resolved())
			.addFilteredPages(new ArrayList<>(writtenBookContent.pages()))
			.build();
		item.setData(DataComponentTypes.WRITTEN_BOOK_CONTENT, writtenBookContent);

		return ModificationResult.OK;
	}

	/**
	 * Gets the online status indicator from the player name
	 *
	 * @param author player name
	 * @return online status indicator, or null if unknown/invalid player
	 */
	public static @Nullable Component getStatus(String author) {
		Player playerExact = Bukkit.getPlayerExact(author);
		if (playerExact != null)
			return combined(playerExact.displayName(), ONLINE);

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(author);
		if (offlinePlayer == null) offlinePlayer = Bukkit.getOfflinePlayer(author);

		if (offlinePlayer.hasPlayedBefore())
			return combined(Component.text(author), OFFLINE);

		return null;
	}

	/**
	 * Gets the online status indicator from the player name
	 *
	 * @param author player name
	 * @return online status indicator, or null if unknown/invalid player
	 */
	public static @Nullable Component getStatus(UUID author) {
		Player player = Bukkit.getPlayer(author);
		if (player != null)
			return combined(ONLINE, player.displayName());

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(author);
		String name = offlinePlayer.getName();
		return name == null ? null : combined(OFFLINE, Component.text(name));
	}

}
