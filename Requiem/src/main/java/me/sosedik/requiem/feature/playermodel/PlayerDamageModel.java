package me.sosedik.requiem.feature.playermodel;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.listener.player.damage.DamageModelLoadSave;
import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.resourcelib.feature.TabRenderer;
import me.sosedik.resourcelib.util.SpacingUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.sosedik.utilizer.api.message.Mini.combined;

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

	private static int calcOffset(@NotNull BodyPart @NotNull ... bodyParts) {
		int offset = 4;
		for (BodyPart bodyPart : bodyParts)
			offset += bodyPart.getFontData(BodyDamage.GREEN).width();
		return offset;
	}

	private final Player player;
	private final Map<String, BodyPartState> bodyParts = new HashMap<>();
	private boolean showDamage = false;
	private int damageTick = 0;

	public PlayerDamageModel(@NotNull Player player, @Nullable ReadableNBT nbt) {
		this.player = player;
		for (BodyPart bodyPart : BodyPart.BODY_PARTS) {
			ReadableNBT bodyPartNbt = nbt == null ? null : nbt.getCompound(bodyPart.getId());
			this.bodyParts.put(bodyPart.getId(), new BodyPartState(bodyPart, bodyPartNbt));
		}

		HudMessenger.of(player).addHudElement(Requiem.requiemKey("player_damage_model"), this::constructPlayerModel);
		TabRenderer.of(player).addTopElement(Requiem.requiemKey("player_damage_model"), this::constructTabPlayerModel);

		Requiem.scheduler().sync(this, 1L, 1L);
	}

	private @Nullable Component constructPlayerModel() {
		if (!hasDamageTick()) return null; // TODO

		return getPlayerModel();
	}

	private @NotNull List<Component> constructTabPlayerModel() {
		return List.of(getPlayerModel());
	}

	private @NotNull Component getPlayerModel() {
		List<Component> bodyComponents = new ArrayList<>();
		this.bodyParts.values().forEach(bodyPartState -> {
			int offset = HUD_OFFSETS.get(bodyPartState.getBodyPart());
			bodyComponents.add(bodyPartState.getFontData().offsetMapping(offset));
			if (showDamage && bodyPartState.hasDamageTicks())
				bodyComponents.add(bodyPartState.getOverlayData().offsetMapping(offset));
		});

		return combined(bodyComponents).color(SpacingUtil.TOP_LEFT_CORNER);
	}

	public boolean hasDamageTick() {
		for (BodyPartState bodyPartState : this.bodyParts.values()) {
			if (bodyPartState.hasDamageTicks())
				return true;
		}
		return false;
	}

	public @NotNull BodyPartState getState(@NotNull BodyPart bodyPart) {
		return this.bodyParts.get(bodyPart.getId());
	}

	@Override
	public void run() {
		this.damageTick++;
		if (this.damageTick % 3 == 0)
			this.showDamage = !this.showDamage;
		this.bodyParts.values().forEach(bodyPartState -> bodyPartState.tick(this.player));
	}

	public void save(@NotNull ReadWriteNBT nbt) {
		this.bodyParts.forEach((id, bodyPartState) -> bodyPartState.save(nbt.getOrCreateCompound(id)));
	}

	public static @NotNull PlayerDamageModel of(@NotNull Player player) {
		return DamageModelLoadSave.of(player);
	}

}