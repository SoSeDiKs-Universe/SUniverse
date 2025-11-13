package me.sosedik.trappednewbie.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieDamageTypes;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.LocationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SuicideCommand {

	public static void init(JavaPlugin plugin) {
		new CommandAPICommand("suicide")
			.withOptionalArguments(
				new StringArgument("is")
					.replaceSuggestions(ArgumentSuggestions.strings("is")),
				new StringArgument("what")
					.replaceSuggestions(ArgumentSuggestions.strings("what"))
			)
			.executesPlayer(SuicideCommand::execute)
			.register(plugin);
	}

	private static void execute(Player player, CommandArguments args) {
		String isWhat = String.join(" ", (String) args.get("is"), (String) args.get("what"));
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

		LocationUtil.smartTeleport(player, TrappedNewbie.limboWorld().getSpawnLocation().center(1), false);
	}

}
