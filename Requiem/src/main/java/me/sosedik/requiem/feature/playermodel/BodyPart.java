package me.sosedik.requiem.feature.playermodel;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BodyPart {

	public static final BodyPart HEAD = builder("head", "head").vital().build();
	public static final BodyPart LEFT_ARM = builder("left_arm", "arm").build();
	public static final BodyPart RIGHT_ARM = builder("right_arm", "arm").build();
	public static final BodyPart CHEST = builder("chest", "body").vital().build();
	public static final BodyPart LEFT_LEG = builder("left_leg", "leg").build();
	public static final BodyPart RIGHT_LEG = builder("right_leg", "leg").build();
	public static final BodyPart LEFT_FOOT = builder("left_foot", "foot").build();
	public static final BodyPart RIGHT_FOOT = builder("right_foot", "foot").build();

	public static final List<BodyPart> BODY_PARTS = List.of(
		HEAD, LEFT_ARM, RIGHT_ARM, CHEST, LEFT_LEG, RIGHT_LEG, LEFT_FOOT, RIGHT_FOOT
	);

	private final String id;
	private final int defaultMaxHealth;
	private final boolean vital;
	private final FontData[] displays;

	private BodyPart(
		@NotNull String id,
		int defaultMaxHealth,
		boolean vital,
		@NotNull FontData @NotNull [] displays
	) {
		this.id = id;
		this.defaultMaxHealth = defaultMaxHealth;
		this.vital = vital;
		this.displays = displays;
	}

	public @NotNull String getId() {
		return this.id;
	}

	public int getDefaultMaxHealth() {
		return this.defaultMaxHealth;
	}

	public boolean isVital() {
		return this.vital;
	}

	public @NotNull FontData getFontData(@NotNull BodyDamage bodyDamage) {
		return this.displays[bodyDamage.ordinal()];
	}

	private static @NotNull Builder builder(@NotNull String id, @NotNull String mapping) {
		return new Builder(id, mapping);
	}

	private static final class Builder {

		private final String id;
		private int defaultMaxHealth = 10;
		private boolean vital = false;
		private final FontData[] displays = new FontData[BodyDamage.values().length];

		public Builder(@NotNull String id, @NotNull String mapping) {
			this.id = id;
			for (BodyDamage bodyDamage : BodyDamage.values()) {
				displays[bodyDamage.ordinal()] = mapping(bodyDamage, mapping);
			}
		}

		private @NotNull FontData mapping(@NotNull BodyDamage bodyDamage, @NotNull String mapping) {
			mapping = "requiem:health/" + mapping + "_" + bodyDamage.name().toLowerCase(Locale.ENGLISH);
			FontData fontData = ResourceLib.storage().getFontData(mapping);
			return Objects.requireNonNull(fontData);
		}

		public @NotNull Builder maxHealth(int defaultMaxHealth) {
			this.defaultMaxHealth = defaultMaxHealth;
			return this;
		}

		public @NotNull Builder vital() {
			this.vital = true;
			return this;
		}

		public @NotNull BodyPart build() {
			return new BodyPart(
				this.id,
				this.defaultMaxHealth,
				this.vital,
				this.displays
			);
		}

	}

}
