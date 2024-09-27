package me.sosedik.miscme.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import static me.sosedik.utilizer.api.message.Mini.combined;

/**
 * Books show online status of the author in tooltip
 */
public class BookAuthorOnlineModifier extends ItemModifier {

	private static final Pattern VALID_NICKNAME = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
	private static final Component ONLINE = Component.text(" ●", NamedTextColor.GREEN);
	private static final Component OFFLINE = Component.text(" ●", NamedTextColor.RED);

	public BookAuthorOnlineModifier(@NotNull NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public @NotNull ModificationResult modify(@NotNull ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleName()) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		if (!(contextBox.getMeta() instanceof BookMeta meta)) return ModificationResult.PASS;
		if (!meta.hasAuthor()) return ModificationResult.PASS;

		String author = meta.getAuthor();
		if (author == null) return ModificationResult.PASS;
		if (!VALID_NICKNAME.matcher(author).matches()) return ModificationResult.PASS;

		Component status = getStatus(author);
		if (status == null) return ModificationResult.PASS;

		meta.author(status);

		return ModificationResult.OK;
	}

	/**
	 * Gets the online status indicator from the player name
	 *
	 * @param author player name
	 * @return online status indicator, or null if unknown/invalid player
	 */
	public static @Nullable Component getStatus(@NotNull String author) {
		if (Bukkit.getPlayerExact(author) != null) return combined(Component.text(author), ONLINE);
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(author);
		if (offlinePlayer == null)
			offlinePlayer = Bukkit.getOfflinePlayer(author);
		if (offlinePlayer.hasPlayedBefore())
			return combined(Component.text(author), OFFLINE);
		return null;
	}

}
