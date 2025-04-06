package me.sosedik.requiem.feature.playermodel;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.listener.player.damage.DamageModelLoadSave;
import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.resourcelib.feature.TabRenderer;
import me.sosedik.resourcelib.util.SpacingUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.sosedik.utilizer.api.message.Mini.combined;

@NullMarked
public class PlayerDamageModel extends BukkitRunnable {

	private static final Map<BodyPart, Integer> HUD_OFFSETS = Map.of(
		BodyPart.HEAD, calcOffset(BodyPart.LEFT_ARM),
		BodyPart.LEFT_ARM, calcOffset(),
		BodyPart.CHEST, calcOffset(BodyPart.LEFT_ARM),
		BodyPart.RIGHT_ARM, calcOffset(BodyPart.LEFT_ARM, BodyPart.CHEST),
		BodyPart.LEFT_LEG, calcOffset(BodyPart.LEFT_ARM),
		BodyPart.RIGHT_LEG, calcOffset(BodyPart.LEFT_ARM, BodyPart.LEFT_LEG),
		BodyPart.LEFT_FOOT, calcOffset(BodyPart.LEFT_ARM),
		BodyPart.RIGHT_FOOT, calcOffset(BodyPart.LEFT_ARM, BodyPart.LEFT_FOOT)
	);
	private static final NamespacedKey HUD_RENDERER_KEY = Requiem.requiemKey("player_damage_model");

	private static int calcOffset(BodyPart ... bodyParts) {
		int offset = 4;
		for (BodyPart bodyPart : bodyParts)
			offset += bodyPart.getFontData(false, BodyDamage.GREEN).width();
		return offset;
	}

	private final Player player;
	private final Map<String, BodyPartState> bodyParts = new HashMap<>();
	private boolean showDamage = false;
	private int damageTick = 0;

	public PlayerDamageModel(Player player, @Nullable ReadableNBT nbt) {
		this.player = player;
		for (BodyPart bodyPart : BodyPart.BODY_PARTS) {
			ReadableNBT bodyPartNbt = nbt == null ? null : nbt.getCompound(bodyPart.getId());
			this.bodyParts.put(bodyPart.getId(), new BodyPartState(bodyPart, bodyPartNbt));
		}

		HudMessenger.of(player).addHudElement(HUD_RENDERER_KEY, this::constructPlayerModel);
		TabRenderer.of(player).addHudElement(HUD_RENDERER_KEY, this::constructTabPlayerModel);

		Requiem.scheduler().sync(this, 1L, 1L);
	}

	private @Nullable Component constructPlayerModel() {
		if (!hasDamageTick()) return null;

		return getPlayerModel(false).color(SpacingUtil.TOP_LEFT_CORNER_HUD).shadowColor(ShadowColor.none());
	}

	private List<Component> constructTabPlayerModel() {
		return List.of(getPlayerModel(true).color(SpacingUtil.TOP_LEFT_CORNER_TAB).shadowColor(ShadowColor.none()));
	}

	private Component getPlayerModel(boolean tab) {
		List<Component> bodyComponents = new ArrayList<>();
		this.bodyParts.values().forEach(bodyPartState -> {
			int offset = HUD_OFFSETS.get(bodyPartState.getBodyPart());
			bodyComponents.add(bodyPartState.getFontData(tab).offsetMapping(offset));
			if (showDamage && bodyPartState.hasDamageTicks())
				bodyComponents.add(bodyPartState.getOverlayData(tab).offsetMapping(offset));
		});

		return combined(bodyComponents);
	}

	public boolean hasDamageTick() {
		for (BodyPartState bodyPartState : this.bodyParts.values()) {
			if (bodyPartState.hasDamageTicks())
				return true;
		}
		return false;
	}

	public BodyPartState getState(BodyPart bodyPart) {
		return this.bodyParts.get(bodyPart.getId());
	}

	@Override
	public void run() {
		this.damageTick++;
		if (this.damageTick % 3 == 0)
			this.showDamage = !this.showDamage;
		this.bodyParts.values().forEach(bodyPartState -> bodyPartState.tick(this.player));
	}

	public void save(ReadWriteNBT nbt) {
		this.bodyParts.forEach((id, bodyPartState) -> bodyPartState.save(nbt.getOrCreateCompound(id)));
	}

	public static PlayerDamageModel of(Player player) {
		return DamageModelLoadSave.of(player);
	}

}
