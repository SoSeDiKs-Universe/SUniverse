package me.sosedik.trappednewbie.api.task;

import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

@NullMarked
public class BossBarTask extends BukkitRunnable {

	private static final Component PREFIX = Mini.asIcon(Component.text("✨"));
	private static final List<NamespacedKey> subtitlePriorities = new ArrayList<>();

	private final Player player;
	private final BossBar[] bossBars = new BossBar[]{constructBossBar(), constructBossBar(), constructBossBar(), constructBossBar()};
	private @Nullable Task progressionTask;
	private final List<IconProvider> iconProviders = new ArrayList<>();
	private final List<Component> iconTitles;
	private @Nullable Component @Nullable [] subtitle;
	private final Map<NamespacedKey, SubtitleProvider> subtitleProviders = new HashMap<>();

	public BossBarTask(Player player) {
		this.player = player;
		this.iconTitles = new ArrayList<>();
		for (BossBar bossBar : bossBars)
			player.showBossBar(bossBar);
		runTaskTimer(TrappedNewbie.instance(), 10L, 1L);
	}

	private BossBar constructBossBar() {
		return BossBar.bossBar(Component.empty(), 1F, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
	}

	private void constructTitles() {
		iconTitles.clear();
		subtitle = null;

		for (NamespacedKey key : subtitlePriorities) {
			SubtitleProvider provider = subtitleProviders.get(key);
			if (provider == null) continue;

			subtitle = provider.getSubtitles();
			if (subtitle != null)
				break;
		}

		if (subtitle == null) {
			for (IconProvider provider : iconProviders) {
				Component icon = provider.getIcon();
				if (icon != null)
					iconTitles.add(icon);
			}
		}

		if (progressionTask != null && subtitle == null && iconTitles.isEmpty() && !player.getGameMode().isInvulnerable()) {
			Component task = Messenger.messenger(player).getMessage("task.current_goal");
			int length = SpacingUtil.getWidth(task);
			iconTitles.add(SpacingUtil.getOffset((int) Math.ceil(length / -2D), length, task));
		}
	}

	@Override
	public void run() {
		if (!player.isOnline()) {
			cancel();
			return;
		}

		constructTitles();

		if (iconTitles.isEmpty()) {
			bossBars[0].name(Component.empty());
		} else {
			bossBars[0].name(combined(List.copyOf(iconTitles)));
		}

		if (subtitle != null) {
			applySubtitle(1);
			applySubtitle(2);
			applySubtitle(3);
			return;
		}

		if (progressionTask != null) {
			Component[] task = progressionTask.getDisplay();
			bossBars[1].name(task == null || task[0] == null ? Component.empty() : combine(Component.space(), PREFIX, task[0], PREFIX));
			bossBars[2].name(task == null || task[1] == null ? Component.empty() : task[1]);
			bossBars[3].name(Component.empty());
			return;
		}

		bossBars[1].name(Component.empty());
		bossBars[2].name(Component.empty());
		bossBars[3].name(Component.empty());
	}

	private void applySubtitle(int subtitleNum) {
		if (subtitle == null) return;

		if (subtitle.length < subtitleNum) {
			bossBars[subtitleNum].name(Component.empty());
			return;
		}

		Component sub = subtitle[subtitleNum - 1];
		bossBars[subtitleNum].name(sub == null ? Component.empty() : sub);
	}

	public void setProgressionTask(@Nullable Task task) {
		this.progressionTask = task;
	}

	public @Nullable Task getProgressionTask() {
		return progressionTask;
	}

	public void addIconProvider(NamespacedKey providerId, IconProvider iconProvider) {
		iconProviders.add(iconProvider);
	}

	public void addProviderSingle(NamespacedKey providerId, Supplier<@Nullable Component> subtitleProvider) {
		addProvider(providerId, () -> {
			Component display = subtitleProvider.get();
			return display == null ? null : new Component[]{display};
		});
	}

	public void addProvider(NamespacedKey providerId, SubtitleProvider subtitleProvider) {
		subtitleProviders.put(providerId, subtitleProvider);
	}

	@FunctionalInterface
	public interface IconProvider {

		@Nullable Component getIcon();

	}

	@FunctionalInterface
	public interface SubtitleProvider {

		@Nullable Component @Nullable[] getSubtitles();

	}

	/**
	 * Initializes scoreboard renderer's options
	 *
	 * @param plugin plugin instance
	 */
	public static void init(TrappedNewbie plugin) {
		FileConfiguration config = plugin.getConfig();
		if (!config.contains("boss-bar.subtitle-priorities")) {
			config.set("boss-bar.subtitle-priorities", List.of());
			return;
		}

		List<String> priorities = config.getStringList("boss-bar.subtitle-priorities");
		priorities.forEach(key -> subtitlePriorities.add(NamespacedKey.fromString(key)));
	}

}
