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
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;
import static me.sosedik.utilizer.api.message.Mini.component;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Fakes custom effects displays
 */
public class DisplayCustomPotionEffectsOnHud implements Listener {

	private static final FontData BACKGROUND_BENEFICIAL;
	private static final FontData BACKGROUND_HARMFUL;
	private static final Map<NamespacedKey, Component> SMALL_ICONS = new HashMap<>();
	private static final Map<NamespacedKey, Component> BIG_ICONS = new HashMap<>();
	private static final NamespacedKey HUD_RENDERER_KEY = ResourceLib.resourceLibKey("custom_potion_effects");

	static {
		BACKGROUND_BENEFICIAL = Objects.requireNonNull(ResourceLib.storage().getFontData("minecraft:effect_background_beneficial"));
		BACKGROUND_HARMFUL = Objects.requireNonNull(ResourceLib.storage().getFontData("minecraft:effect_background_harmful"));

		Registry.EFFECT.forEach(effectType -> {
			NamespacedKey key = effectType.getKey();
			if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) return;

			String effectKey = key.getNamespace() + ":effect/" + key.getKey();
			FontData bigFontData = ResourceLib.storage().getFontData(effectKey);
			if (bigFontData == null) {
				ResourceLib.logger().warn("Couldn't find effect mapping for {}", effectKey);
				return;
			}
			FontData smallFontData = ResourceLib.storage().getFontData(effectKey + "_icon");
			if (smallFontData == null) {
				ResourceLib.logger().warn("Couldn't find effect mapping for {}_icon", effectKey);
				return;
			}
			Component smallIcon = smallFontData.mapping().color(NamedTextColor.WHITE);
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
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();
		HudMessenger.of(player).addHudElement(HUD_RENDERER_KEY, () -> getPotionEffectsHud(player));
		TabRenderer.of(player).addHeaderElement(HUD_RENDERER_KEY, () -> getPotionEffectsTab(player));
	}

	private @Nullable Component getPotionEffectsHud(@NotNull Player player) {
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
				beneficialEffects.add(SpacingUtil.getOffset(++beneficialPotions * -24, 0, icon));
			else
				harmfulEffects.add(SpacingUtil.getOffset(++harmfulPotions * -25, 0, icon));
		}
		return combined(
			combined(beneficialEffects),
			combined(harmfulEffects)
		).color(SpacingUtil.TOP_RIGHT_CORNER_HUD);
	}

	private @Nullable List<@NotNull Component> getPotionEffectsTab(@NotNull Player player) {
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

	private @Nullable PotionEffectData getEffectsData(@NotNull Player player) {
		List<PotionEffect> customEffects = new ArrayList<>();
		int vanillaBeneficial = 0;
		int vanillaHarmful = 0;
		List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects()); // We're async, avoid CME
		for (PotionEffect potionEffect : effects) {
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
		@NotNull List<@NotNull PotionEffect> customEffects,
		int vanillaBeneficial,
		int vanillaHarmful
	) {}

	private static @Nullable Component getBigIcon(@NotNull PotionEffectType effectType) {
		return BIG_ICONS.get(effectType.getKey());
	}

	public static @NotNull Component getSmallIcon(@NotNull PotionEffectType effectType) {
		return SMALL_ICONS.get(effectType.getKey());
	}

	private static @NotNull Component getTabDisplay(@NotNull Audience viewer, @NotNull PotionEffect effect, int spacing) {
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

	public static @NotNull Component getName(@NotNull Audience viewer, @NotNull PotionEffect effect) {
		int amplifier = effect.getAmplifier();
		if (amplifier == 0)
			return Messenger.messenger(viewer).getMessage(getTranslationKey(effect.getType()));
		return combine(Component.space(), Messenger.messenger(viewer).getMessage(getTranslationKey(effect.getType())), Component.text(RomanNumerals.toRoman(amplifier)));
	}

	public static @NotNull Component getIconAndName(@NotNull Audience viewer, @NotNull PotionEffect effect) {
		return combine(Component.space(), getSmallIcon(effect.getType()), getName(viewer, effect));
	}

	private static @NotNull String getTranslationKey(@NotNull PotionEffectType effectType) {
		NamespacedKey effectId = effectType.getKey();
		return "effect." + effectId.getNamespace() + "." + effectId.getKey() + ".name";
	}

}
