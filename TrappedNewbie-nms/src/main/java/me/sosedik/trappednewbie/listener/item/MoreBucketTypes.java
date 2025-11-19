package me.sosedik.trappednewbie.listener.item;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.kiterino.event.entity.ItemConsumeEvent;
import me.sosedik.miscme.listener.entity.ItemFrameSpillables;
import me.sosedik.miscme.listener.item.ImmersiveDyes;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.impl.item.modifier.BucketModifier;
import me.sosedik.trappednewbie.impl.item.modifier.ScrapModifier;
import me.sosedik.trappednewbie.listener.block.FluidPickupRequiresGloves;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.DurabilityUtil;
import net.minecraft.world.item.MobBucketItem;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Custom buckets handler
 */
// MCCheck: 1.21.10, new buckets (spills)
@NullMarked
public class MoreBucketTypes implements Listener {

	public MoreBucketTypes() {
		MaterialTags.BUCKETS.getValues().forEach(bucketMaterial ->
			ImmersiveDyes.addExtraDyeRule(bucketMaterial, (item, dye) -> {
				BucketModifier.BucketType bucketType = BucketModifier.BucketType.fromBucket(item);
				if (bucketType == null) return null;
				if (!bucketType.isDyable()) return null;

				item = item.clone();
				if (!ImmersiveDyes.tryToDye(item, dye)) return null;

				return item;
			})
		);

		ItemFrameSpillables.SpillableItem waterSpillLogic = (spiller, item, block) -> {
			block.emitSound(Sound.ITEM_BUCKET_EMPTY, 1F, 1F);

			if (spiller instanceof Player player
				&& FluidPickupRequiresGloves.REQUIRE_GLOVES.contains(player.getLocation().getBlock().getBiome())) {
				VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
				if (visualArmor.canUseVisualArmor()) {
					if (visualArmor.hasNonBrokenGloves()) {
						ItemStack damaged = visualArmor.getGloves().damage(1, player);
						visualArmor.setGloves(damaged);
					} else {
						player.setFreezeTicks(player.getFreezeTicks() + 12 * 20);
					}
				}
			}

			BucketModifier.BucketType bucketType = BucketModifier.BucketType.fromBucket(item);
			if (bucketType == null) return null;

			ItemStack reminder = bucketType.save(item, ItemStack.of(Material.BUCKET));

			if (block.getType() == Material.LAVA_CAULDRON) {
				block.emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
				block.getWorld().spawnParticle(Particle.SMOKE, block.getLocation().center(), 50);
				block.setType(Material.CAULDRON);
				return reminder;
			}

			if (block.getType() == Material.CAULDRON)
				block.setType(Material.WATER_CAULDRON);

			if (block.getBlockData() instanceof Levelled levelled && block.getType() == Material.WATER_CAULDRON) {
				if (levelled.getLevel() < levelled.getMaximumLevel()) {
					levelled.setLevel(levelled.getMaximumLevel());
					block.setBlockData(levelled);
				}
				return reminder;
			}

			if (block.getBlockData() instanceof Waterlogged waterlogged) {
				if (!waterlogged.isWaterlogged()) {
					waterlogged.setWaterlogged(true);
					block.setBlockData(waterlogged);
				}
				return reminder;
			}

			if (block.getType() == Material.WATER && block.getBlockData() instanceof Levelled levelled && levelled.getLevel() != 0) {
				block.setType(Material.WATER);
				return reminder;
			}

			if (block.getType() == Material.LAVA) {
				block.emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
				block.setType(Material.OBSIDIAN);
				return reminder;
			}

			if (!block.isReplaceable()) {
				block = block.getRelative(BlockFace.UP);
				if (!block.isReplaceable()) return reminder;
			}

			block.setType(Material.WATER);
			return reminder;
		};
		ItemFrameSpillables.addSpillable(Material.WATER_BUCKET, waterSpillLogic);

		ItemFrameSpillables.SpillableItem lavaSpillLogic = (spiller, item, block) -> {
			block.emitSound(Sound.ITEM_BUCKET_EMPTY_LAVA, 1F, 1F);

			if (spiller instanceof LivingEntity entity)
				entity.setFireTicks(Math.max(10 * 20, entity.getFireTicks()));

			BucketModifier.BucketType bucketType = BucketModifier.BucketType.fromBucket(item);
			if (bucketType == null) return null;

			ItemStack reminder = bucketType.save(item, ItemStack.of(Material.BUCKET));

			if (block.getType() == Material.CAULDRON) {
				block.setType(Material.LAVA_CAULDRON);
				return reminder;
			}

			if (block.getType() == Material.WATER_CAULDRON) {
				block.emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
				block.getWorld().spawnParticle(Particle.SMOKE, block.getLocation().center(), 50);
				block.setType(Material.CAULDRON);
				return reminder;
			}

			// ToDo: custom levelled lava cauldron

			if (block.getType() == Material.WATER) {
				block.emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
				block.getWorld().spawnParticle(Particle.SMOKE, block.getLocation().center(), 50);
				block.setType(Material.STONE);
				return reminder;
			}

			if (block.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged()) {
				waterlogged.setWaterlogged(false);
				block.setBlockData(waterlogged);
				block.emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
				block.getWorld().spawnParticle(Particle.SMOKE, block.getLocation().center(), 50);
				return reminder;
			}

			if (block.getType() == Material.LAVA && block.getBlockData() instanceof Levelled levelled && levelled.getLevel() != 0) {
				block.setType(Material.LAVA);
				return reminder;
			}

			if (!block.getType().isBurnable() && !block.isReplaceable())
				block = block.getRelative(BlockFace.UP);

			if (!block.isReplaceable()) return reminder;

			block.setType(Material.LAVA);
			return reminder;
		};
		ItemFrameSpillables.addSpillable(Material.LAVA_BUCKET, lavaSpillLogic);

		ItemFrameSpillables.addSpillable(Material.MILK_BUCKET, (spiller, item, block) -> {
			block.emitSound(Sound.ITEM_BUCKET_EMPTY, 1F, 1F);

			BucketModifier.BucketType bucketType = BucketModifier.BucketType.fromBucket(item);
			if (bucketType == null) return null;

			ItemStack reminder = bucketType.save(item, ItemStack.of(Material.BUCKET));

			// ToDo: custom milk cauldron

			return reminder;
		});

		ItemFrameSpillables.addSpillable(Material.POWDER_SNOW_BUCKET, (spiller, item, block) -> {
			block.emitSound(Sound.ITEM_BUCKET_EMPTY, 1F, 1F);

			if (spiller instanceof LivingEntity entity)
				entity.setFreezeTicks(entity.getFreezeTicks() + 5 * 20);

			BucketModifier.BucketType bucketType = BucketModifier.BucketType.fromBucket(item);
			if (bucketType == null) return null;

			ItemStack reminder = bucketType.save(item, ItemStack.of(Material.BUCKET));

			if (block.getType() == Material.LAVA_CAULDRON) {
				block.emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
				block.getWorld().spawnParticle(Particle.SMOKE, block.getLocation().center(), 50);
				return reminder;
			}

			if (block.getType() == Material.CAULDRON)
				block.setType(Material.POWDER_SNOW_CAULDRON);

			if (block.getBlockData() instanceof Levelled levelled && block.getType() == Material.POWDER_SNOW_CAULDRON) {
				if (levelled.getLevel() < levelled.getMaximumLevel()) {
					levelled.setLevel(levelled.getMaximumLevel());
					block.setBlockData(levelled);
				}
				return reminder;
			}

			if (!block.isReplaceable()) {
				block = block.getRelative(BlockFace.UP);
				if (!block.isReplaceable()) return reminder;
			}

			block.setType(Material.POWDER_SNOW);

			return reminder;
		});

		UtilizerTags.SPILLABLE_MOB_BUCKETS.getValues().forEach(bucketMaterial -> {
			if (!(net.minecraft.world.item.ItemStack.fromBukkitCopy(ItemStack.of(bucketMaterial)).getItem() instanceof MobBucketItem))
				throw new IllegalArgumentException("Not a mob bucket: " + bucketMaterial);

			ItemFrameSpillables.addSpillable(bucketMaterial, (spiller, item, block) -> {
				net.minecraft.world.item.ItemStack itemStack = net.minecraft.world.item.ItemStack.fromBukkitCopy(item);
				if (itemStack.getItem() instanceof MobBucketItem mobBucketItem)
					mobBucketItem.checkExtraContent(spiller instanceof CraftLivingEntity living ? living.getHandle() : null, ((CraftWorld) block.getWorld()).getHandle(), itemStack, CraftLocation.toBlockPosition(block.getLocation()));

				if (UtilizerTags.HOT_BUCKETS.isTagged(item.getType())) {
					BucketModifier.BucketType bucketType = BucketModifier.BucketType.fromBucket(item);
					if (bucketType == null) return ItemStack.empty();

					if (bucketType == BucketModifier.BucketType.CLAY) {
						block.emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
						return crackedCeramic(null);
					}

					if (UtilizerTags.LAVA_BUCKETS.isTagged(item.getType()))
						return lavaSpillLogic.onSpill(spiller, item, block);

					return bucketType.save(item, ItemStack.of(Material.BUCKET));
				}

				return waterSpillLogic.onSpill(spiller, item, block);
			});
		});
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBurn(FurnaceBurnEvent event) {
		ItemStack fuel = event.getFuel();
		BucketModifier.BucketType bucketType = BucketModifier.BucketType.fromBucket(fuel);
		if (bucketType == null) return;
		if (!bucketType.isWooden()) return;

		event.setRemainingItem(ItemStack.of(TrappedNewbieItems.ASH));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onCook(BlockCookEvent event) {
		preserveBucketType(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onRemain(RemainingItemEvent event) {
		preserveBucketType(event);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEmpty(PlayerBucketEmptyEvent event) {
		preserveBucketType(event);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFill(PlayerBucketFillEvent event) {
		preserveBucketType(event);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBucket(PlayerBucketEntityEvent event) {
		preserveBucketType(event);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDispense(BlockDispenseEvent event) {
		preserveBucketType(event);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getPreviousSlot());
		if (!spillClayBucket(player, item)) return;

		player.swingMainHand();
		ItemStack result = UtilizerTags.HOT_BUCKETS.isTagged(item.getType()) ? crackedCeramic(null) : ItemStack.of(Material.CLAY_BALL);
		player.getInventory().setItem(event.getPreviousSlot(), result);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onSwap(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();

		if (spillClayBucket(player, event.getMainHandItem())) {
			player.swingMainHand();
			ItemStack result = UtilizerTags.HOT_BUCKETS.isTagged(event.getMainHandItem().getType()) ? crackedCeramic(null) : ItemStack.of(Material.CLAY_BALL);
			event.setMainHandItem(result);
		}
		if (spillClayBucket(player, event.getOffHandItem())) {
			player.swingOffHand();
			ItemStack result = UtilizerTags.HOT_BUCKETS.isTagged(event.getOffHandItem().getType()) ? crackedCeramic(null) : ItemStack.of(Material.CLAY_BALL);
			event.setOffHandItem(result);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;

		ItemStack item = event.getCurrentItem();
		if (!spillClayBucket(player, item)) return;

		player.swingMainHand();
		ItemStack result = UtilizerTags.HOT_BUCKETS.isTagged(item.getType()) ? crackedCeramic(null) : ItemStack.of(Material.CLAY_BALL);
		event.setCurrentItem(result);
	}

	@EventHandler(ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Item itemDrop = event.getItemDrop();
		ItemStack item = itemDrop.getItemStack();
		if (!spillClayBucket(player, item)) return;

		player.swingMainHand();
		if (UtilizerTags.HOT_BUCKETS.isTagged(item.getType()))
			itemDrop.setItemStack(crackedCeramic(null));
		else
			itemDrop.setItemStack(ItemStack.of(Material.CLAY_BALL));
	}

	@EventHandler(ignoreCancelled = true)
	public void onPickup(EntityPickupItemEvent event) {
		Item itemDrop = event.getItem();
		if (!itemDrop.isValid()) return;

		ItemStack item = itemDrop.getItemStack();
		if (!spillClayBucket(event.getEntity(), item)) return;

		event.setCancelled(true);
		itemDrop.setItemStack(ItemStack.of(Material.CLAY_BALL));
	}

	@Contract("_, null -> false")
	private boolean spillClayBucket(Entity entity, @Nullable ItemStack item) {
		if (ItemStack.isEmpty(item)) return false;
		if (item.getType() == Material.BUCKET) return false;
		if (BucketModifier.BucketType.fromBucket(item) != BucketModifier.BucketType.CLAY) return false;

		if (UtilizerTags.HOT_BUCKETS.isTagged(item.getType()))
			entity.emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);

		ItemFrameSpillables.spill(entity, item, entity.getLocation().getBlock());

		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		preserveBucketType(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onConsume(EntityItemConsumeEvent event) {
		preserveBucketType(event);
	}

	private void preserveBucketType(Event event) {
		ItemStack result;
		switch (event) {
			case RemainingItemEvent bucketEvent -> result = bucketEvent.getResult();
			case PlayerBucketEvent bucketEvent -> result = bucketEvent.getItemStack();
			case PlayerBucketEntityEvent bucketEvent -> result = bucketEvent.getEntityBucket();
			case BlockDispenseEvent bucketEvent -> result = bucketEvent.getLeftoverItem();
			case BlockCookEvent bucketEvent -> result = bucketEvent.getResult();
			case ItemConsumeEvent bucketEvent -> {
				result = bucketEvent.getReplacement();
				if (!ItemStack.isEmpty(result)) break;

				result = bucketEvent.getItem();
				if (!result.hasData(DataComponentTypes.USE_REMAINDER)) return;
				
				result = result.getData(DataComponentTypes.USE_REMAINDER).transformInto();
			}
			default -> {
				return;
			}
		}
		if (ItemStack.isEmpty(result)) return;

		ItemStack bucket;
		switch (event) {
			case RemainingItemEvent bucketEvent -> bucket = bucketEvent.getItem();
			case PlayerBucketEvent bucketEvent -> bucket = bucketEvent.getBucketItem();
			case PlayerBucketEntityEvent bucketEvent -> bucket = bucketEvent.getOriginalBucket();
			case BlockDispenseEvent bucketEvent -> bucket = bucketEvent.getItem();
			case BlockCookEvent bucketEvent -> bucket = bucketEvent.getSource();
			case ItemConsumeEvent bucketEvent -> bucket = bucketEvent.getItem();
			default -> {
				return;
			}
		}
		if (ItemStack.isEmpty(bucket)) return;

		BucketModifier.BucketType bucketType = BucketModifier.BucketType.fromBucket(bucket);
		if (bucketType == null) return;
		if (bucketType == BucketModifier.BucketType.VANILLA) return;

		BucketModifier.BucketOverlay overlay = BucketModifier.BucketOverlay.fromBucket(bucket);

		// Emptying clay buckets
		if (event instanceof PlayerBucketEmptyEvent bucketEvent && bucketType == BucketModifier.BucketType.CLAY) {
			Player player = bucketEvent.getPlayer();
			ItemFrameSpillables.spill(player, bucket, player.getLocation().getBlock());
			if (overlay != null && overlay.hasLava()) {
				result = crackedCeramic(null);
				bucketEvent.getPlayer().emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
			} else {
				bucketEvent.setItemStack(ItemStack.of(Material.CLAY_BALL));
				return;
			}
		// Consuming clay buckets
		} else if (event instanceof ItemConsumeEvent bucketEvent && bucketType == BucketModifier.BucketType.CLAY) {
			bucketEvent.setReplacement(ItemStack.of(Material.CLAY_BALL));
			// Delay in case the item clears effects (e.g., milk bucket)
			TrappedNewbie.scheduler().sync(() -> bucketEvent.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 60 * 20, 0)), 1L);
			return;
		// Filling burnable with hot
		} else if (overlay != null && overlay.isHot() && !bucketType.canHoldHeat()) {
			ItemStack leftOver = bucketType.isWooden() ? ItemStack.of(TrappedNewbieItems.ASH) : ItemStack.empty();
			switch (event) {
				case RemainingItemEvent bucketEvent -> {
					if (bucketEvent.getPlayer() != null)
						bucketEvent.getPlayer().emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
					bucketEvent.setResult(leftOver);
				}
				case PlayerBucketEvent bucketEvent -> {
					bucketEvent.getPlayer().emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
					bucketEvent.setItemStack(leftOver);
				}
				case PlayerBucketEntityEvent bucketEvent -> {
					bucketEvent.getPlayer().emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
					bucketEvent.setEntityBucket(leftOver);
				}
				case BlockDispenseEvent bucketEvent -> {
					bucketEvent.getBlock().emitSound(Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
					bucketEvent.setDispensedItem(leftOver);
				}
				default -> {}
			}
			return;
		// Preserve bucket type
		} else {
			result = bucketType.save(bucket, result);
		}

		// Preserve bucket color
		if (bucketType.isDyable() && bucket.hasData(DataComponentTypes.DYED_COLOR))
			result.setData(DataComponentTypes.DYED_COLOR, bucket.getData(DataComponentTypes.DYED_COLOR));

		// Filling more than 1 bucket or clay with lava without gloves
		if (event instanceof PlayerBucketFillEvent bucketEvent && bucketType == BucketModifier.BucketType.CLAY) {
			boolean spill = bucket.getAmount() > 1;
			Player player = bucketEvent.getPlayer();
			if (overlay != null && overlay.hasLava()) {
				VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
				player.setFireTicks(Math.max(6 * 20, player.getFireTicks()));
				spill = spill || visualArmor.canUseVisualArmor() && !visualArmor.hasNonBrokenGloves();
			}
			if (spill) {
				ItemFrameSpillables.spill(player, result, player.getLocation().getBlock());
				result = ItemStack.of(Material.CLAY_BALL);
			}
		}

		int damage = (bucketType == BucketModifier.BucketType.CERAMIC && overlay != null && overlay.hasLava()) ? 32 : 1;
		switch (event) {
			case RemainingItemEvent bucketEvent -> {
				if (bucketEvent.isConsume()) {
					if (bucketEvent.getPlayer() != null)
						result = result.damage(damage, bucketEvent.getPlayer());
					else
						result = DurabilityUtil.damageItem(result, damage);
				}
				if (bucketType == BucketModifier.BucketType.CLAY || (overlay != null && overlay.hasLava() && (bucketType == BucketModifier.BucketType.CERAMIC)) && result.isEmpty())
					result = crackedCeramic(bucket);
				bucketEvent.setResult(result);
			}
			case PlayerBucketEvent bucketEvent -> {
				if (event instanceof PlayerBucketEmptyEvent)
					result = result.damage(damage, bucketEvent.getPlayer());
				if ((bucketType == BucketModifier.BucketType.CLAY || bucketType == BucketModifier.BucketType.CERAMIC) && result.isEmpty())
					result = crackedCeramic(bucket);
				bucketEvent.setItemStack(result);
			}
			case PlayerBucketEntityEvent bucketEvent -> bucketEvent.setEntityBucket(result);
			case BlockDispenseEvent bucketEvent -> {
				result = DurabilityUtil.damageItem(result, damage);
				if ((bucketType == BucketModifier.BucketType.CLAY || bucketType == BucketModifier.BucketType.CERAMIC) && result.isEmpty())
					result = crackedCeramic(bucket);
				bucketEvent.setDispensedItem(result);
			}
			case BlockCookEvent bucketEvent -> bucketEvent.setResult(result);
			case ItemConsumeEvent bucketEvent -> {
				result = result.damage(1, bucketEvent.getEntity());
				if (bucketType == BucketModifier.BucketType.CERAMIC && result.isEmpty())
					result = crackedCeramic(bucket);
				bucketEvent.setReplacement(result);
			}
			default -> {}
		}
	}

	private ItemStack crackedCeramic(@Nullable ItemStack bucket) {
		ItemStack item = BucketModifier.BucketType.CERAMIC.save(ItemStack.of(Material.BUCKET));
		if (bucket != null && bucket.hasData(DataComponentTypes.DYED_COLOR))
			item.setData(DataComponentTypes.DYED_COLOR, bucket.getData(DataComponentTypes.DYED_COLOR));
		return ScrapModifier.makeScrap(item);
	}

}
