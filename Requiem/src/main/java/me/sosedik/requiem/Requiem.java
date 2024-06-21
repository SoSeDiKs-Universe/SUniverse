package me.sosedik.requiem;

import me.sosedik.requiem.command.ReviveCommand;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.requiem.listener.entity.PrepareGhostMobs;
import me.sosedik.requiem.listener.player.DeathMakesGhosts;
import me.sosedik.requiem.listener.player.DeathMakesPossessed;
import me.sosedik.requiem.listener.player.GhostsDontSprint;
import me.sosedik.requiem.listener.player.GhostsDontStarveOrChoke;
import me.sosedik.requiem.listener.player.GhostsKeepNightVision;
import me.sosedik.requiem.listener.player.LoadSavePlayers;
import me.sosedik.requiem.listener.player.MobsDontTargetGhosts;
import me.sosedik.requiem.listener.player.NoAirForWaterPossessing;
import me.sosedik.requiem.listener.player.NoDamageToOrFromGhosts;
import me.sosedik.requiem.listener.player.NoExpFromPossessedKill;
import me.sosedik.requiem.listener.player.NoGhostInteractions;
import me.sosedik.requiem.listener.player.PossessedDismount;
import me.sosedik.requiem.listener.player.PossessedInfiniteProjectiles;
import me.sosedik.requiem.listener.player.PossessedLimitedControl;
import me.sosedik.requiem.listener.player.PossessedMimikPossessor;
import me.sosedik.requiem.listener.player.PossessingOverMobs;
import me.sosedik.requiem.listener.player.PossessorMimiksPossessed;
import me.sosedik.requiem.listener.player.TransformationsKeepPossessor;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Requiem extends JavaPlugin {

	private static Requiem instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		Requiem.instance = this;
		this.scheduler = new Scheduler(this);

		TranslationHolder.extractLocales(this);
	}

	@Override
	public void onEnable() {
		registerCommands();
		EventUtil.registerListeners(this,
			// entity
			PrepareGhostMobs.class,
			// player
			DeathMakesGhosts.class,
			DeathMakesPossessed.class,
			GhostsDontSprint.class,
			GhostsDontStarveOrChoke.class,
			GhostsKeepNightVision.class,
			LoadSavePlayers.class,
			MobsDontTargetGhosts.class,
			NoAirForWaterPossessing.class,
			NoDamageToOrFromGhosts.class,
			NoExpFromPossessedKill.class,
			NoGhostInteractions.class,
			PossessedDismount.class,
			PossessedInfiniteProjectiles.class,
			PossessedLimitedControl.class,
			PossessedMimikPossessor.class,
			PossessingOverMobs.class,
			PossessorMimiksPossessed.class,
			TransformationsKeepPossessor.class
		);
	}

	private void registerCommands() {
		CommandManager.commandManager().registerCommands(this,
			ReviveCommand.class
		);
	}

	@Override
	public void onDisable() {
		GhostyPlayer.saveAllData();
		PossessingPlayer.saveAllData();
		PrepareGhostMobs.unregisterGhosts();
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static @NotNull Requiem instance() {
		return Requiem.instance;
	}

	/**
	 * Gets the plugin's task scheduler
	 *
	 * @return the plugin's task scheduler
	 */
	public static @NotNull Scheduler scheduler() {
		return instance().scheduler;
	}

	/**
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static @NotNull ComponentLogger logger() {
		return instance().getComponentLogger();
	}

}
