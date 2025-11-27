package me.sosedik.resourcelib.listener.player;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.resourcelib.feature.TabRenderer;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.RomanNumerals;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;
import static me.sosedik.utilizer.api.message.Mini.component;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Fakes custom effects displays
 */
@NullMarked
public class DisplayCustomPotionEffectsOnHud implements Listener {

	private static final FontData BACKGROUND_BENEFICIAL;
	private static final FontData BACKGROUND_HARMFUL;
	private static final Map<NamespacedKey, Component> SMALL_ICONS = new HashMap<>();
	private static final Map<NamespacedKey, Component> BIG_ICONS = new HashMap<>();
	private static final NamespacedKey HUD_RENDERER_KEY = ResourceLib.resourceLibKey("custom_potion_effects");

	static {
		BACKGROUND_BENEFICIAL = requireNonNull(ResourceLib.storage().getFontData(NamespacedKey.minecraft("effect_background_beneficial")));
		BACKGROUND_HARMFUL = requireNonNull(ResourceLib.storage().getFontData(NamespacedKey.minecraft("effect_background_harmful")));

		Registry.MOB_EFFECT.forEach(effectType -> {
			NamespacedKey key = effectType.getKey();
			if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) return;

			String effectKey = key.getNamespace() + ":effect/" + key.getKey();
			FontData bigFontData = ResourceLib.storage().getFontData(requireNonNull(NamespacedKey.fromString(effectKey)));
			if (bigFontData == null) {
				ResourceLib.logger().warn("Couldn't find effect mapping for {}", effectKey);
				return;
			}
			FontData smallFontData = ResourceLib.storage().getFontData(requireNonNull(NamespacedKey.fromString(effectKey + "_icon")));
			if (smallFontData == null) {
				ResourceLib.logger().warn("Couldn't find effect mapping for {}_icon", effectKey);
				return;
			}
			Component smallIcon = smallFontData.icon();
			FontData backgroundData = effectType.getEffectCategory() == PotionEffectType.Category.BENEFICIAL ? BACKGROUND_BENEFICIAL : BACKGROUND_HARMFUL;
			Component bigIcon = combined(
				backgroundData.offsetMapping(),
				bigFontData.offsetMapping((backgroundData.width() - bigFontData.width()) / 2)
			);
			SMALL_ICONS.put(key, smallIcon);
			BIG_ICONS.put(key, bigIcon);
		});
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		HudMessenger.of(player).addHudElement(HUD_RENDERER_KEY, () -> getPotionEffectsHud(player));
		TabRenderer.of(player).addHeaderElement(HUD_RENDERER_KEY, () -> getPotionEffectsTab(player));
	}

	private @Nullable Component getPotionEffectsHud(Player player) {
		PotionEffectData effectData = getEffectsData(player);
		if (effectData == null) return null;

		List<PotionEffect> customEffects = effectData.customEffects();
		int beneficialPotions = effectData.vanillaBeneficial();
		int harmfulPotions = effectData.vanillaHarmful();

		List<Component> beneficialEffects = new ArrayList<>();
		List<Component> harmfulEffects = new ArrayList<>();
		for (PotionEffect effect : customEffects) {
			Component icon = getBigIcon(effect.getType());
			if (icon == null) icon = SpacingUtil.ICON_SPACE;
			if (effect.getType().getEffectCategory() == PotionEffectType.Category.BENEFICIAL)
				beneficialEffects.add(SpacingUtil.getOffset(++beneficialPotions * -25, 0, icon));
			else
				harmfulEffects.add(SpacingUtil.getOffset(++harmfulPotions * -25, 0, icon));
		}
		return combined(
			combined(beneficialEffects),
			combined(harmfulEffects)
		).color(SpacingUtil.TOP_RIGHT_CORNER_HUD).shadowColor(ShadowColor.none());
	}

	private @Nullable List<Component> getPotionEffectsTab(Player player) {
		PotionEffectData effectData = getEffectsData(player);
		if (effectData == null) return null;

		List<PotionEffect> customEffects = effectData.customEffects();
		customEffects.sort(Comparator.comparingInt(PotionEffect::getDuration));

		// Sort & prepare
		List<Map.Entry<PotionEffect, Integer>> effects = new ArrayList<>();
		final int[] maxEffectLength = {0};
		customEffects.forEach(effect -> {
			Component icon = SMALL_ICONS.get(effect.getType().getKey());
			if (icon == null) return;

			Component name = getName(player, effect);
			int nameWidth = SpacingUtil.getWidth(name);
			maxEffectLength[0] = Math.max(maxEffectLength[0], nameWidth);
			effects.add(Map.entry(effect, nameWidth));
		});

		// Add visuals
		List<Component> visuals = new ArrayList<>();
		effects.forEach(entry -> {
			PotionEffect effect = entry.getKey();
			int spacing = maxEffectLength[0] - entry.getValue();
			visuals.add(getTabDisplay(player, effect, spacing));
		});

		return visuals;
	}

	private @Nullable PotionEffectData getEffectsData(Player player) {
		List<PotionEffect> customEffects = new ArrayList<>();
		int vanillaBeneficial = 0;
		int vanillaHarmful = 0;
		List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects()); // We're async, avoid CME
		for (PotionEffect potionEffect : effects) {
			if (!potionEffect.hasIcon()) continue;
			if (NamespacedKey.MINECRAFT.equals(potionEffect.getType().getKey().getNamespace())) {
				if (potionEffect.getType().getEffectCategory() == PotionEffectType.Category.BENEFICIAL)
					vanillaBeneficial++;
				else
					vanillaHarmful++;
				continue;
			}
			customEffects.add(potionEffect);
		}
		return customEffects.isEmpty() ? null : new PotionEffectData(customEffects, vanillaBeneficial, vanillaHarmful);
	}

	private record PotionEffectData(
		List<PotionEffect> customEffects,
		int vanillaBeneficial,
		int vanillaHarmful
	) {}

	private static @Nullable Component getBigIcon(PotionEffectType effectType) {
		return BIG_ICONS.get(effectType.getKey());
	}

	public static Component getSmallIcon(PotionEffectType effectType) {
		return SMALL_ICONS.get(effectType.getKey());
	}

	private static Component getTabDisplay(Audience viewer, PotionEffect effect, int spacing) {
		var messenger = Messenger.messenger(viewer);
		String durationDisplay;
		int duration = effect.getDuration();
		if (duration == PotionEffect.INFINITE_DURATION) {
			durationDisplay = "∞";
		} else {
			duration /= 20;
			int minutes = duration / 60;
			int seconds = duration % 60;
			durationDisplay = minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
		}
		int amplifier = effect.getAmplifier();
		Component icon = getSmallIcon(effect.getType());
		return messenger.getMessage("resourcelib.tab.effects",
			component("effect_icon", icon),
			component("effect_name", messenger.getMessage(getTranslationKey(effect.getType()))),
			raw("effect_level", (amplifier == 0 ? "" : " " + RomanNumerals.toRoman(amplifier))),
			raw("effect_duration", durationDisplay),
			component("spacing", SpacingUtil.getSpacing(spacing))
		);
	}

	public static Component getName(Audience viewer, PotionEffect effect) {
		int amplifier = effect.getAmplifier();
		if (amplifier == 0)
			return Messenger.messenger(viewer).getMessage(getTranslationKey(effect.getType()));
		return combine(Component.space(), Messenger.messenger(viewer).getMessage(getTranslationKey(effect.getType())), Component.text(RomanNumerals.toRoman(amplifier)));
	}

	public static Component getIconAndName(Audience viewer, PotionEffect effect) {
		return combine(Component.space(), getSmallIcon(effect.getType()), getName(viewer, effect));
	}

	private static String getTranslationKey(PotionEffectType effectType) {
		NamespacedKey effectId = effectType.getKey();
		return "effect." + effectId.getNamespace() + "." + effectId.getKey() + ".name";
	}

}
