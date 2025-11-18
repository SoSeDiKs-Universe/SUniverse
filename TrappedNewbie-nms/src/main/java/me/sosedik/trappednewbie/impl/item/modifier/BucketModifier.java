package me.sosedik.trappednewbie.impl.item.modifier;

import com.destroystokyo.paper.MaterialTags;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

/**
 * Bucket visuals
 */
// MCCheck: 1.21.10, new wood types / new buckets
@NullMarked
public class BucketModifier extends ItemModifier {

	private static final DyedItemColor DEFAULT_CERAMIC_COLOR = DyedItemColor.dyedItemColor(Color.fromRGB(14975336));

	public BucketModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();

		BucketType bucketType = BucketType.fromBucket(item, contextBox.getInitialType());
		if (bucketType == null) return ModificationResult.PASS;

		Player viewer = contextBox.getViewer();
		BucketOverlay bucketOverlay = BucketOverlay.fromBucket(item, contextBox.getInitialType());

		if (bucketType == BucketType.VANILLA && (bucketOverlay == null || bucketOverlay.usesVanillaModel()))
			return ModificationResult.PASS;

		boolean jumping = bucketOverlay != null && bucketOverlay.isJumping() && !ItemUtil.shouldFreeze(contextBox.getContext());
		if (jumping && bucketOverlay.isSlime())
			jumping = viewer != null && viewer.getChunk().isSlimeChunk();

		boolean modified = false;
		if (bucketType.isDyable() && !item.hasData(DataComponentTypes.DYED_COLOR)) {
			item.setData(DataComponentTypes.DYED_COLOR, DEFAULT_CERAMIC_COLOR);
			modified = true;
			contextBox.addHiddenComponents(DataComponentTypes.DYED_COLOR);
		}

		if (bucketOverlay == null) {
			var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
			item.setData(DataComponentTypes.ITEM_NAME, messenger.getMessage("item." + bucketType.getKey().namespace() + "." + bucketType.getKey().value() + ".name"));
			item.setData(DataComponentTypes.ITEM_MODEL, bucketType.getModelKey());
			return ModificationResult.OK;
		}

		NamespacedKey modelKey = jumping ? bucketOverlay.getJumpingModelKey(bucketType) : bucketOverlay.getModelKey(bucketType);
		if (modelKey == null) return modified ? ModificationResult.OK : ModificationResult.PASS;

		NamespacedKey key = bucketOverlay.getKey(bucketType);
		if (key == null) return modified ? ModificationResult.OK : ModificationResult.PASS;

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		item.setData(DataComponentTypes.ITEM_NAME, messenger.getMessage("item." + key.namespace() + "." + key.value() + ".name"));
		item.setData(DataComponentTypes.ITEM_MODEL, modelKey);

		return ModificationResult.OK;
	}

	public enum BucketType {

		VANILLA, // IRON
		CLAY,
		CERAMIC,
		ACACIA,
		BAMBOO,
		BIRCH,
		CHERRY,
		CRIMSON,
		DARK_OAK,
		JUNGLE,
		MANGROVE,
		OAK,
		PALE_OAK,
		SPRUCE,
		WARPED;

		private final NamespacedKey key;
		private final NamespacedKey modelKey;

		BucketType() {
			this.key = trappedNewbieKey(name().toLowerCase(Locale.US) + "_bucket");
			this.modelKey = ResourceLib.storage().getItemModelMapping(this.key);
		}

		public NamespacedKey getKey() {
			return this.key;
		}

		public NamespacedKey getModelKey() {
			return this.modelKey;
		}

		public boolean canHoldLava() {
			return this == CLAY
				|| this == CERAMIC
				|| this == CRIMSON
				|| this == WARPED;
		}

		public boolean isDyable() {
			return this == CERAMIC;
		}

		public ItemStack save(ItemStack item) {
			NBT.modify(item, nbt -> {
				if (this == VANILLA)
					nbt.removeKey("bucket_type");
				else
					nbt.setEnum("bucket_type", this);
			});
			return item;
		}

		public static @Nullable BucketType fromBucket(ItemStack item) {
			return fromBucket(item, item.getType());
		}

		public static @Nullable BucketType fromBucket(ItemStack item, Material itemType) {
			if (!MaterialTags.BUCKETS.isTagged(itemType)) return null;
			return NBT.get(item, nbt -> (BucketType) nbt.getOrDefault("bucket_type", VANILLA));
		}

	}

	public enum BucketOverlay {

		WATER(false, true),
		LAVA(false, true),
		MILK(false, true),
		POWDER_SNOW(false, true),
		COD(false, true),
		SALMON(false, true),
		TROPICAL_FISH(false, true),
		PUFFERFISH(false, true),
		FROG_COLD(false, false),
		FROG_TEMPERATE(false, false),
		FROG_WARM(false, false),
		TADPOLE(false, true),
		AXOLOTL_LEUCISTIC(false, false),
		AXOLOTL_LEUCISTIC_BABY(false, false),
		AXOLOTL_WILD(false, false),
		AXOLOTL_WILD_BABY(false, false),
		AXOLOTL_GOLD(false, false),
		AXOLOTL_GOLD_BABY(false, false),
		AXOLOTL_CYAN(false, false),
		AXOLOTL_CYAN_BABY(false, false),
		AXOLOTL_BLUE(false, false),
		AXOLOTL_BLUE_BABY(false, false),
		SLIME(true, false),
		MAGMA_CUBE(true, false);

		private final Map<BucketType, NamespacedKey> keys = new EnumMap<>(BucketType.class);
		private final Map<BucketType, NamespacedKey> modelKeys = new EnumMap<>(BucketType.class);
		private final Map<BucketType, NamespacedKey> jumpingModelKeys;
		private final boolean vanillaModel;

		BucketOverlay(boolean jumping, boolean vanillaModel) {
			this.vanillaModel = vanillaModel;
			this.jumpingModelKeys = jumping ? new EnumMap<>(BucketType.class) : Map.of();
			for (BucketType bucketType : BucketType.values()) {
				String modelKey = (bucketType == BucketType.VANILLA ? "" : bucketType.name().toLowerCase(Locale.US) + "_") + name().toLowerCase(Locale.US) + "_bucket";
				if (!vanillaModel || bucketType != BucketType.VANILLA) {
					this.keys.put(bucketType, trappedNewbieKey(modelKey));
					this.modelKeys.put(bucketType, ResourceLib.storage().getItemModelMapping(trappedNewbieKey(modelKey)));
				}
				if (jumping) this.jumpingModelKeys.put(bucketType, ResourceLib.storage().getItemModelMapping(trappedNewbieKey("jumping_" + modelKey)));
			}
		}

		public @Nullable NamespacedKey getKey(BucketType bucketType) {
			return this.keys.get(bucketType);
		}

		public @Nullable NamespacedKey getModelKey(BucketType bucketType) {
			return this.modelKeys.get(bucketType);
		}

		public @Nullable NamespacedKey getJumpingModelKey(BucketType bucketType) {
			return this.jumpingModelKeys.get(bucketType);
		}

		public boolean isJumping() {
			return !this.jumpingModelKeys.isEmpty();
		}

		public boolean usesVanillaModel() {
			return this.vanillaModel;
		}

		public boolean isSlime() {
			return this == SLIME
				|| this == MAGMA_CUBE;
		}

		public boolean isAxolotl() {
			return this == AXOLOTL_LEUCISTIC
				|| this == AXOLOTL_LEUCISTIC_BABY
				|| this == AXOLOTL_WILD
				|| this == AXOLOTL_WILD_BABY
				|| this == AXOLOTL_GOLD
				|| this == AXOLOTL_GOLD_BABY
				|| this == AXOLOTL_CYAN
				|| this == AXOLOTL_CYAN_BABY
				|| this == AXOLOTL_BLUE
				|| this == AXOLOTL_BLUE_BABY;
		}

		public boolean isFrog() {
			return this == FROG_COLD
				|| this == FROG_TEMPERATE
				|| this == FROG_WARM;
		}

		public static @Nullable BucketOverlay fromBucket(ItemStack item) {
			return fromBucket(item, item.getType());
		}

		public static @Nullable BucketOverlay fromBucket(ItemStack item, Material itemType) {
			return switch (itemType) {
				case Material m when m == TrappedNewbieItems.SLIME_BUCKET -> SLIME;
				case Material m when m == TrappedNewbieItems.MAGMA_CUBE_BUCKET -> MAGMA_CUBE;
				case Material m when m == TrappedNewbieItems.FROG_BUCKET -> NBT.getComponents(item, nbt -> {
					ReadableNBT bucketEntityData = nbt.getCompound("minecraft:bucket_entity_data");
					if (bucketEntityData == null) return FROG_TEMPERATE;

					String type = bucketEntityData.getOrNull("Variant", String.class);
					if ("cold".equals(type)) return FROG_COLD;
					if ("warm".equals(type)) return FROG_WARM;
					return FROG_TEMPERATE;
				});
				case WATER_BUCKET -> WATER;
				case LAVA_BUCKET -> LAVA;
				case MILK_BUCKET -> MILK;
				case POWDER_SNOW_BUCKET -> POWDER_SNOW;
				case COD -> COD;
				case SALMON_BUCKET -> SALMON;
				case TROPICAL_FISH_BUCKET -> TROPICAL_FISH;
				case PUFFERFISH_BUCKET -> PUFFERFISH;
				case TADPOLE_BUCKET -> TADPOLE;
				case AXOLOTL_BUCKET -> {
					if (!item.hasData(DataComponentTypes.AXOLOTL_VARIANT)) yield AXOLOTL_LEUCISTIC;

					boolean adult = item.getItemMeta() instanceof AxolotlBucketMeta meta && meta.isAdult(); // TODO remove meta
					Axolotl.Variant data = item.getData(DataComponentTypes.AXOLOTL_VARIANT);
					assert data != null;
					yield switch (data) {
						case LUCY -> adult ? AXOLOTL_LEUCISTIC : AXOLOTL_LEUCISTIC_BABY;
						case WILD -> adult ? AXOLOTL_WILD : AXOLOTL_WILD_BABY;
						case GOLD -> adult ? AXOLOTL_GOLD : AXOLOTL_GOLD_BABY;
						case CYAN -> adult ? AXOLOTL_CYAN : AXOLOTL_CYAN_BABY;
						case BLUE -> adult ? AXOLOTL_BLUE : AXOLOTL_BLUE_BABY;
					};
				}
				default -> null;
			};
		}

	}

}
