package me.sosedik.resourcelib.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.listener.player.DisplayCustomPotionEffectsOnHud;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Giving custom effects
 */
@NullMarked
@Permission("rlib.command.ceffect")
public class CEffectCommand { // TODO not really needed, should fix vanilla command instead

	@Command("ceffect <effect> [duration] [amplifier] [player]")
	public void onCommand(
		CommandSourceStack stack,
		@Argument(value = "effect", suggestions = "@ceffectCommandSuggestionEffectGive") NamespacedKey effectKey,
		@Argument(value = "duration") @Default("20") @Range(min = "1") int duration,
		@Argument(value = "amplifier") @Default("0") @Range(min = "0") int amplifier,
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

		PotionEffectType effectType = Registry.EFFECT.get(effectKey);
		if (effectType == null) return;

		ResourceLib.scheduler().sync(() -> {
			var effect = new PotionEffect(effectType, duration * 20, amplifier);
			target.addPotionEffect(effect);
			if (!silent) Messenger.messenger(target).sendMessage("command.ceffect.give", raw("effect", combine(Component.space(), DisplayCustomPotionEffectsOnHud.getIconAndName(target, effect))));
			if (stack.getSender() != target)
				Messenger.messenger(stack.getSender()).sendMessage("command.ceffect.give.other", raw("effect", DisplayCustomPotionEffectsOnHud.getIconAndName(stack.getSender(), effect)), raw("player", target.displayName()));
		});
	}

	@Command("ceffect clear [effect] [player]")
	public void onCommand(
		CommandSourceStack stack,
		@Nullable @Argument(value = "effect", suggestions = "@ceffectCommandSuggestionEffectClear") NamespacedKey effectKey,
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

		if (effectKey != null && !"minecraft:all".equals(effectKey.asString())) {
			PotionEffectType effectType = Registry.EFFECT.get(effectKey);
			if (effectType == null) return;

			ResourceLib.scheduler().sync(() -> {
				PotionEffect effect = target.getPotionEffect(effectType);
				if (effect == null) return;

				target.removePotionEffect(effectType);

				if (!silent) Messenger.messenger(target).sendMessage("command.ceffect.clear", raw("effect", DisplayCustomPotionEffectsOnHud.getIconAndName(target, effect)));
				if (stack.getSender() != target)
					Messenger.messenger(stack.getSender()).sendMessage("command.ceffect.clear.other", raw("effect", DisplayCustomPotionEffectsOnHud.getIconAndName(stack.getSender(), effect)), raw("player", target.displayName()));
			});
		} else {
			ResourceLib.scheduler().sync(target::clearActivePotionEffects);
		}
	}

	@Suggestions("@ceffectCommandSuggestionEffectGive")
	public Stream<String> onEffectGiveSuggestion(CommandSourceStack stack) {
		return Registry.EFFECT.stream()
				.filter(effectType -> !NamespacedKey.MINECRAFT.equals(effectType.getKey().getNamespace()))
				.map(effectType -> effectType.getKey().asString());
	}

	@Suggestions("@ceffectCommandSuggestionEffectClear")
	public List<String> onEffectClearSuggestion(CommandSourceStack stack) {
		List<String> suggestions = new ArrayList<>();
		suggestions.add("all");
		if (stack.getSender() instanceof Player player) {
			suggestions.addAll(
				player.getActivePotionEffects().stream()
					.map(PotionEffect::getType)
					.filter(effectType -> !NamespacedKey.MINECRAFT.equals(effectType.getKey().getNamespace()))
					.map(effectType -> effectType.getKey().asString())
					.toList()
			);
		} else {
			suggestions.addAll(onEffectGiveSuggestion(stack).toList());
		}
		return suggestions;
	}

}
