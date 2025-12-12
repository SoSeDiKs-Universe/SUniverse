package me.sosedik.trappednewbie.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.trappednewbie.dataset.TrappedNewbieDamageTypes;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.LocationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jspecify.annotations.NullMarked;

import static org.incendo.cloud.parser.standard.StringParser.stringParser;

/**
 * Where the ends meet
 */
@NullMarked
public class SuicideCommand {

	public SuicideCommand(CommandManager commandManager) {
		PaperCommandManager<CommandSourceStack> manager = commandManager.manager();
		manager.command(
			manager.commandBuilder("suicide")
				.optional("is", stringParser(), Description.of("is"), SuggestionProvider.suggestingStrings("is"))
				.optional("what", stringParser(), Description.of("what"), SuggestionProvider.suggestingStrings("what"))
				.handler(SuicideCommand::execute)
		);
	}

	private static void execute(CommandContext<CommandSourceStack> context) {
		if (!(context.sender().getExecutor() instanceof Player player)) return;

		String isWhat = String.join(" ", context.getOrDefault("is", ""), context.getOrDefault("what", ""));
		if (!"is bad".equals(isWhat)) {
			var messenger = Messenger.messenger(player);
			var command = Mini.component("command", Component.text("/suicide is bad")
				.hoverEvent(messenger.getMessage("command.suicide.hover"))
				.clickEvent(ClickEvent.suggestCommand("/suicide is bad")));
			messenger.sendMessage("command.suicide", command);
			return;
		}

		LivingEntity possessed = PossessingPlayer.stopPossessing(player);
		if (possessed != null) {
			if (possessed.getEquipment() != null) possessed.getEquipment().clear();
			possessed.remove();
		}

		player.setLevel(0);
		player.setExp(0F);
		player.getInventory().clear();
		player.damage(0, DamageSource.builder(TrappedNewbieDamageTypes.SUICIDE).build()); // Reset last damage cause
		GhostyPlayer.markGhost(player);
		player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 20 * 60 * 60 * 3, 0));

		LocationUtil.smartTeleport(player, Utilizer.limboWorld().getSpawnLocation().center(1), false);
	}

}
