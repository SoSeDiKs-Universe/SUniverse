package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.essence.api.event.AsyncPlayerHealCommandEvent;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Healing players
 */
@NullMarked
@Permission("essence.command.heal")
public class HealCommand {

	private static final Set<PotionEffectType> BAD_EFFECTS = Set.of(
		PotionEffectType.POISON, PotionEffectType.WITHER
	);

	@Command("heal [player]")
	public void onCommand(
		CommandSourceStack stack,
		@Nullable @Argument(value = "player") Player player,
		@Flag(value = "silent") boolean silent
	) {
		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}

		Essence.scheduler().sync(() -> {
			target.setHealth(target.getMaxHealth());
			target.setArrowsInBody(0);
			target.setFoodLevel(20);
			target.setFireTicks(0);
			target.setRemainingAir(target.getMaximumAir());
			if (target.getVehicle() instanceof LivingEntity vehicle) {
				vehicle.setHealth(vehicle.getMaxHealth());
				vehicle.setArrowsInBody(0);
				vehicle.setFireTicks(0);
				BAD_EFFECTS.forEach(vehicle::removePotionEffect);
			}
			BAD_EFFECTS.forEach(target::removePotionEffect);
		});

		new AsyncPlayerHealCommandEvent(target).callEvent();
		if (!silent) Messenger.messenger(target).sendMessage("command.heal");
		if (stack.getSender() != target)
			Messenger.messenger(stack.getSender()).sendMessage("command.heal.other", raw("player", target.displayName()));
	}

}
