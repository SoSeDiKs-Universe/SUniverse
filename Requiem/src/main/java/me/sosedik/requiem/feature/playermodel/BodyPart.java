package me.sosedik.requiem.feature.playermodel;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static me.sosedik.requiem.Requiem.requiemKey;

@NullMarked
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
	private final FontData[] tabDisplays;

	private BodyPart(
		String id,
		int defaultMaxHealth,
		boolean vital,
		FontData [] displays,
		FontData [] tabDisplays
	) {
		this.id = id;
		this.defaultMaxHealth = defaultMaxHealth;
		this.vital = vital;
		this.displays = displays;
		this.tabDisplays = tabDisplays;
	}

	public String getId() {
		return this.id;
	}

	public int getDefaultMaxHealth() {
		return this.defaultMaxHealth;
	}

	public boolean isVital() {
		return this.vital;
	}

	public FontData getFontData(boolean tab, BodyDamage bodyDamage) {
		if (tab) return this.tabDisplays[bodyDamage.ordinal()];
		return this.displays[bodyDamage.ordinal()];
	}

	private static Builder builder(String id, String mapping) {
		return new Builder(id, mapping);
	}

	private static final class Builder {

		private final String id;
		private int defaultMaxHealth = 10;
		private boolean vital = false;
		private final FontData[] displays = new FontData[BodyDamage.values().length];
		private final FontData[] tabDisplays = new FontData[displays.length];

		public Builder(String id, String mapping) {
			this.id = id;
			for (BodyDamage bodyDamage : BodyDamage.values()) {
				displays[bodyDamage.ordinal()] = mapping(bodyDamage, mapping, "");
				tabDisplays[bodyDamage.ordinal()] = mapping(bodyDamage, mapping, "_tab");
			}
		}

		private FontData mapping(BodyDamage bodyDamage, String mapping, String suffix) {
			mapping = "health/" + mapping + "_" + bodyDamage.name().toLowerCase(Locale.ENGLISH) + suffix;
			FontData fontData = ResourceLib.storage().getFontData(requiemKey(mapping));
			return Objects.requireNonNull(fontData);
		}

		public Builder maxHealth(int defaultMaxHealth) {
			this.defaultMaxHealth = defaultMaxHealth;
			return this;
		}

		public Builder vital() {
			this.vital = true;
			return this;
		}

		public BodyPart build() {
			return new BodyPart(
				this.id,
				this.defaultMaxHealth,
				this.vital,
				this.displays,
				this.tabDisplays
			);
		}

	}

}
