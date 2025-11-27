package me.sosedik.resourcelib.feature;

import com.google.common.base.Preconditions;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.ScoreboardUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.combined;

/**
 * Renders player's scoreboard
 */
@NullMarked
public class ScoreboardRenderer extends BukkitRunnable {

	private static final NamespacedKey EMPTY_LINE_KEY = ResourceLib.resourceLibKey("empty_line");

	private static final boolean MINIMIZE_SCOREBOARD = false;
	private static final List<String> LINE_IDS = new ArrayList<>();

	private static final Map<UUID, ScoreboardRenderer> STORED_HUDS = new HashMap<>();
	private static final List<NamespacedKey> HEADER_PRIORITIES = new ArrayList<>();
	private static final List<NamespacedKey> LINE_PRIORITIES = new ArrayList<>();

	static {
		for (NamedTextColor namedTextColor : NamedTextColor.NAMES.values()) {
			String text = LegacyComponentSerializer.legacySection().serialize(Component.text(" ", namedTextColor));
			LINE_IDS.add(text.strip());
		}
	}

	private final List<Component> headers = new ArrayList<>();
	private final List<Component> lines = new ArrayList<>();
	private final Map<NamespacedKey, ScoreboardProvider> headerProviders = new HashMap<>();
	private final Map<NamespacedKey, ScoreboardProvider> lineProviders = new HashMap<>();

	private final Player player;
	private final Scoreboard scoreboard;
	private final Objective infoTab;

	private ScoreboardRenderer(Player player) {
		this.player = player;
		this.scoreboard = ScoreboardUtil.getScoreboard(player);
		this.infoTab = this.scoreboard.registerNewObjective("InfoTab", Criteria.DUMMY, Component.empty(), RenderType.INTEGER);
		this.infoTab.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.infoTab.numberFormat(NumberFormat.blank());

		addProvider(EMPTY_LINE_KEY, () -> List.of(Component.empty()));

		ResourceLib.scheduler().sync(this, 1L, 1L);
	}

	@Override
	public void run() {
		if (!this.player.isOnline()) {
			cancel();
			return;
		}

		this.headers.clear();
		HEADER_PRIORITIES.forEach(providerId -> {
			ScoreboardProvider provider = this.headerProviders.get(providerId);
			if (provider == null) return;

			List<Component> components = provider.getLines();
			if (components != null)
				this.headers.addAll(components);
		});

		this.lines.clear();
		boolean potentialEmpty = !this.headers.isEmpty();
		if (potentialEmpty) this.lines.add(Mini.combine(SpacingUtil.getNegativePixel(), this.headers));
		for (NamespacedKey providerId : LINE_PRIORITIES) {
			ScoreboardProvider provider = this.lineProviders.get(providerId);
			if (provider == null) continue;

			if (EMPTY_LINE_KEY.equals(providerId)) {
				if (potentialEmpty) {
					this.lines.add(Component.empty());
					potentialEmpty = false;
				}
				continue;
			}

			List<Component> components = provider.getLines();
			if (components == null) continue;

			if (this.lines.size() + components.size() > 16) continue;

			potentialEmpty = true;
			this.lines.addAll(components);
		}

		applyScoreboard();
	}

	private void applyScoreboard() {
		if (this.lines.isEmpty()) {
			this.infoTab.displayName(SpacingUtil.getSpacing(-500));
			this.lines.add(SpacingUtil.getSpacing(-500)); // Scoreboard hides itself if empty
		} else {
			this.infoTab.displayName(combined(SpacingUtil.getSpacing(-500), this.lines.removeFirst()));
			if (this.lines.isEmpty())
				this.lines.add(SpacingUtil.getSpacing(-500)); // Scoreboard hides itself if empty
		}
		int score = this.lines.size();
		for (int i = 0; i < 15; i++) {
			String id = LINE_IDS.get(i);
			Team team = this.scoreboard.getTeam(String.valueOf(i));
			if (team == null) {
				team = this.scoreboard.registerNewTeam(String.valueOf(i));
				team.addEntry(id);
				this.infoTab.getScore(id).setScore(15 - i); // random ConcurrentModificationException in async
			}
			if (i < score) {
				team.prefix(combined(SpacingUtil.getSpacing(-500), this.lines.get(i)));
			} else if (MINIMIZE_SCOREBOARD) {
				this.scoreboard.resetScores(id);
			} else {
				team.prefix(SpacingUtil.getSpacing(-500));
			}
		}
	}

	public void addHeader(NamespacedKey providerId, ScoreboardProvider provider) {
		this.headerProviders.put(providerId, provider);
	}

	public void addProvider(NamespacedKey providerId, ScoreboardProvider provider) {
		this.lineProviders.put(providerId, provider);
	}

	public static ScoreboardRenderer of(Player player) {
		Preconditions.checkArgument(player.isOnline(), "Player must be online");
		return STORED_HUDS.computeIfAbsent(player.getUniqueId(), k -> new ScoreboardRenderer(player));
	}

	public static void removePlayer(Player player) {
		ScoreboardRenderer hudMessenger = STORED_HUDS.remove(player.getUniqueId());
		if (hudMessenger != null)
			hudMessenger.cancel();
	}

	@FunctionalInterface
	public interface ScoreboardProvider {

		@Nullable List<Component> getLines();

	}

	/**
	 * Initializes scoreboard renderer's options
	 *
	 * @param plugin plugin instance
	 */
	public static void init(ResourceLib plugin) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("scoreboard.header-priorities") || !config.contains("scoreboard.line-priorities")) {
			config.set("scoreboard.header-priorities", List.of());
			config.set("scoreboard.line-priorities", List.of());
			return;
		}

		List<String> priorities = config.getStringList("scoreboard.header-priorities");
		priorities.forEach(key -> HEADER_PRIORITIES.add(NamespacedKey.fromString(key)));
		priorities = config.getStringList("scoreboard.line-priorities");
		priorities.forEach(key -> LINE_PRIORITIES.add(NamespacedKey.fromString(key)));
	}

}
