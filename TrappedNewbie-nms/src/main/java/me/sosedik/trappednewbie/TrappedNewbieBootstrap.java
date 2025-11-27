package me.sosedik.trappednewbie;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import me.sosedik.kiterino.util.KiterinoBootstrapEntityTypeInjectorImpl;
import me.sosedik.resourcelib.ResourceLibBootstrap;
import me.sosedik.resourcelib.util.BlockCreator;
import me.sosedik.resourcelib.util.ItemCreator;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntities;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntityTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.entity.api.Glider;
import me.sosedik.trappednewbie.entity.api.PaperPlane;
import me.sosedik.trappednewbie.entity.craft.CraftGlider;
import me.sosedik.trappednewbie.entity.craft.CraftPaperPlane;
import me.sosedik.trappednewbie.impl.block.nms.ClayKilnBlock;
import me.sosedik.trappednewbie.impl.block.nms.SleepingBagBlock;
import me.sosedik.trappednewbie.impl.block.nms.TotemBaseBlock;
import me.sosedik.trappednewbie.impl.effect.BoneBreakingEffect;
import me.sosedik.trappednewbie.impl.effect.BouncyEffect;
import me.sosedik.trappednewbie.impl.effect.ClimbingEffect;
import me.sosedik.trappednewbie.impl.effect.ComfortEffect;
import me.sosedik.trappednewbie.impl.effect.FirefingersEffect;
import me.sosedik.trappednewbie.impl.effect.HotPotatoEffect;
import me.sosedik.trappednewbie.impl.effect.LifeLeechEffect;
import me.sosedik.trappednewbie.impl.effect.ParalyzedEffect;
import me.sosedik.trappednewbie.impl.effect.QuenchedEffect;
import me.sosedik.trappednewbie.impl.effect.RottenBiteEffect;
import me.sosedik.trappednewbie.impl.effect.ScaryEffect;
import me.sosedik.trappednewbie.impl.effect.ThirstEffect;
import me.sosedik.trappednewbie.impl.effect.WaterboltEffect;
import me.sosedik.trappednewbie.impl.item.nms.HangGliderItem;
import me.sosedik.trappednewbie.impl.item.nms.KnifeItem;
import me.sosedik.trappednewbie.impl.item.nms.PaperPlaneItem;
import me.sosedik.trappednewbie.impl.item.nms.ThrowableRockItem;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntityTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.bukkit.craftbukkit.entity.CraftEntityTypes.createAndMoveEmptyRot;

@NullMarked
public class TrappedNewbieBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		Function<String, KiterinoMobEffectBehaviourWrapper> effectsProvider = key -> switch (key.substring("trapped_newbie:".length())) {
			case "bone_breaking" -> new BoneBreakingEffect();
			case "bouncy" -> new BouncyEffect();
			case "climbing" -> new ClimbingEffect();
			case "comfort" -> new ComfortEffect();
			case "firefingers" -> new FirefingersEffect();
			case "hot_potato" -> new HotPotatoEffect();
			case "life_leech" -> new LifeLeechEffect();
			case "paralyzed" -> new ParalyzedEffect();
			case "quenched" -> new QuenchedEffect();
			case "rotten_bite" -> new RottenBiteEffect();
			case "scary" -> new ScaryEffect();
			case "thirst" -> new ThirstEffect();
			case "waterbolt" -> new WaterboltEffect();
			default -> throw new RuntimeException("Unknown effect: %s".formatted(key));
		};
		ResourceLibBootstrap.parseResources(context, effectsProvider);
		ResourceLibBootstrap.setupBlocks(context, null, (key, properties) -> switch (key.substring("trapped_newbie:".length())) {
			case String k when k.endsWith("_branch") -> BlockCreator.vegetation(properties, key, Material::isSolid, Block.cube(14, 0.0625, 14));
			case String k when k.equals("pebble") || k.endsWith("_pebble") -> BlockCreator.vegetation(properties, key, Material::isSolid, Block.cube(14, 0.0625, 14));
			case String k when k.startsWith("destroy_stage_") -> BlockCreator.waterloggedBarrier(properties, key);
			case String k when k.endsWith("_work_station") -> BlockCreator.directionalBarrier(properties, key);
			case String k when k.endsWith("_drum") -> BlockCreator.barrier(properties, key);
			case String k when k.endsWith("_totem_base") -> new TotemBaseBlock(properties, NamespacedKey.fromString(key));
			case String k when k.endsWith("_chopping_block") -> BlockCreator.fakeSculk(properties, key);
			case "clay_kiln" -> new ClayKilnBlock(properties, key);
			case "sleeping_bag" -> new SleepingBagBlock(properties);
			default -> throw new IllegalArgumentException("Unknown blockstate: %s".formatted(key));
		});
		// Should be ignored internally due to ANVIL tag
		var dummyMaterial = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1, 2F, 0F, 0, ItemTags.ANVIL);
		ResourceLibBootstrap.setupItems(context, TrappedNewbieItems.class, null, (key, properties) -> switch (key.substring("trapped_newbie:".length())) {
			case "paper_plane" -> {
				var item = new PaperPlaneItem(properties);
				DispenserBlock.registerProjectileBehavior(item);
				yield item;
			}
			case String k when k.equals("rock") || k.equals("ball_of_mud") || k.endsWith("_rock") -> {
				var item = new ThrowableRockItem(properties);
				DispenserBlock.registerProjectileBehavior(item);
				yield item;
			}
			case String k when k.endsWith("hang_glider") -> new HangGliderItem(properties);
			case "firestriker", "trumpet", "canteen", "reinforced_canteen", "dragon_flask" -> ItemCreator.crossbowItem(properties, (item, entity, timeLeft) -> true);
			case String k when k.endsWith("glass_shard") || k.endsWith("goodie_bag") -> ItemCreator.crossbowItem(properties, (item, entity, timeLeft) -> true);
			case "flint_axe" -> new AxeItem(dummyMaterial, 6F, -3.2F, (Item.Properties) properties);
			case "flint_pickaxe" -> new Item(((Item.Properties) properties).pickaxe(dummyMaterial, 1F, -3F));
			case "flint_shovel" -> new ShovelItem(dummyMaterial, 1.5F, -3F, (Item.Properties) properties);
			case "flint_shears" -> ItemCreator.shearsItem(properties);
			case "flint_knife", "iron_knife" -> new KnifeItem(((Item.Properties) properties)
				.component(DataComponents.TOOL, KnifeItem.createToolProperties())
				.component(DataComponents.WEAPON, new Weapon(1)));
			case "slime_bucket" -> mobBucket((Item.Properties) properties, EntityType.SLIME, Fluids.EMPTY, Items.BUCKET,
				entity -> entity.getSize() == 1,
				null,
				(entity, tag) -> entity.setSize(1, false)
			);
			case "slime_bottle" -> mobBucket((Item.Properties) properties, EntityType.SLIME, Fluids.EMPTY, Items.GLASS_BOTTLE,
				entity -> entity.getSize() == 1,
				null,
				(entity, tag) -> entity.setSize(1, false)
			);
			case "magma_cube_bucket" -> mobBucket((Item.Properties) properties, EntityType.MAGMA_CUBE, Fluids.EMPTY, Items.BUCKET,
				entity -> entity.getSize() == 1,
				null,
				(entity, tag) -> entity.setSize(1, false)
			);
			case "magma_cube_bottle" -> mobBucket((Item.Properties) properties, EntityType.MAGMA_CUBE, Fluids.EMPTY, Items.GLASS_BOTTLE,
				entity -> entity.getSize() == 1,
				null,
				(entity, tag) -> entity.setSize(1, false)
			);
			case "frog_bucket" -> mobBucket((Item.Properties) properties, EntityType.FROG, Fluids.EMPTY, Items.BUCKET,
				null,
				(entity, stack) -> stack.copyFrom(DataComponents.FROG_VARIANT, entity),
				null
			);
			case "frog_water_bucket" -> mobBucket((Item.Properties) properties, EntityType.FROG, Fluids.WATER, Items.WATER_BUCKET,
				null,
				(entity, stack) -> stack.copyFrom(DataComponents.FROG_VARIANT, entity),
				null
			);
			case "turtle_bucket" -> mobBucket((Item.Properties) properties, EntityType.TURTLE, Fluids.WATER, Items.WATER_BUCKET,
				null,
				(entity, stack) -> {
					CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
						tag.putInt("Age", entity.getAge());
						tag.putBoolean("AgeLocked", entity.ageLocked);
						tag.putBoolean("has_egg", entity.hasEgg());
					});
				},
				(entity, tag) -> {
					entity.setAge(tag.getIntOr("Age", 0));
					entity.ageLocked = tag.getBooleanOr("AgeLocked", false);
					entity.setHasEgg(tag.getBooleanOr("has_egg", false));
				}
			);
			case "squid_bucket" -> mobBucket((Item.Properties) properties, EntityType.SQUID, Fluids.WATER, Items.WATER_BUCKET,
				null,
				null,
				null
			);
			case "glow_squid_bucket" -> mobBucket((Item.Properties) properties, EntityType.GLOW_SQUID, Fluids.WATER, Items.WATER_BUCKET,
				null,
				null,
				null
			);
			case "strider_bucket" -> mobBucket((Item.Properties) properties, EntityType.STRIDER, Fluids.LAVA, Items.LAVA_BUCKET,
				entity -> !entity.isVehicle(),
				(entity, stack) -> {
					CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
						tag.putInt("Age", entity.getAge());
						tag.putBoolean("AgeLocked", entity.ageLocked);
						ItemStack saddleItem = entity.getItemBySlot(EquipmentSlot.SADDLE);
						if (!saddleItem.isEmpty()) {
							CompoundTag equipmentTag = new CompoundTag();
							equipmentTag.store("saddle", ItemStack.CODEC, saddleItem);
							tag.put("equipment", equipmentTag);
						}
					});
				},
				(entity, tag) -> {
					entity.setAge(tag.getIntOr("Age", 0));
					entity.ageLocked = tag.getBooleanOr("AgeLocked", false);
					entity.setItemSlot(EquipmentSlot.SADDLE, tag.getCompoundOrEmpty("equipment").read("saddle", ItemStack.CODEC).orElse(ItemStack.EMPTY));
				}
			);
			case "bee_bottle" -> mobBucket((Item.Properties) properties, EntityType.BEE, Fluids.EMPTY, Items.GLASS_BOTTLE,
				null,
				(entity, stack) -> {
					CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
						tag.putInt("Age", entity.getAge());
						tag.putBoolean("AgeLocked", entity.ageLocked);
						tag.putBoolean("HasNectar", entity.hasNectar());
						tag.putBoolean("HasStung", entity.hasStung());
						tag.putInt("TicksSincePollination", entity.ticksWithoutNectarSinceExitingHive);
						tag.putInt("CannotEnterHiveTicks", entity.stayOutOfHiveCountdown);
						tag.putInt("CropsGrownSincePollination", entity.numCropsGrownSincePollination);
						tag.putInt("AngerTime", entity.getRemainingPersistentAngerTime());
						tag.storeNullable("AngryAt", UUIDUtil.CODEC, entity.getPersistentAngerTarget());
						tag.putInt("InLove", entity.inLove);
						if (entity.loveCause != null) tag.store("LoveCause", UUIDUtil.CODEC, entity.loveCause.getUUID());
					});
				},
				(entity, tag) -> {
					entity.setAge(tag.getIntOr("Age", 0));
					entity.ageLocked = tag.getBooleanOr("AgeLocked", false);
					entity.setHasNectar(tag.getBooleanOr("HasNectar", false));
					entity.setHasStung(tag.getBooleanOr("HasStung", false));
					entity.ticksWithoutNectarSinceExitingHive = tag.getIntOr("TicksSincePollination", 0);
					entity.stayOutOfHiveCountdown = tag.getIntOr("CannotEnterHiveTicks", 0);
					entity.numCropsGrownSincePollination = tag.getIntOr("CropsGrownSincePollination", 0);
					entity.setRemainingPersistentAngerTime(tag.getIntOr("AngerTime", 0));
					entity.setPersistentAngerTarget(tag.read("AngryAt", UUIDUtil.CODEC).orElse(null));
					entity.inLove = tag.getIntOr("InLove", 0);
					entity.loveCause = tag.read("LoveCause", EntityReference.<ServerPlayer>codec()).orElse(null);
				}
			);
			case "endermite_bottle" -> mobBucket((Item.Properties) properties, EntityType.ENDERMITE, Fluids.EMPTY, Items.GLASS_BOTTLE,
				null,
				(entity, stack) -> {
					CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
						tag.putInt("Lifetime", entity.life);
						tag.putBoolean("PlayerSpawned", entity.isPlayerSpawned());
					});
				},
				(entity, tag) -> {
					entity.life = tag.getIntOr("Lifetime", 0);
					entity.setPlayerSpawned(tag.getBooleanOr("PlayerSpawned", false));
				}
			);
			case "silverfish_bottle" -> mobBucket((Item.Properties) properties, EntityType.SILVERFISH, Fluids.EMPTY, Items.GLASS_BOTTLE,
				null,
				null,
				null
			);
			case "allay_book" -> mobBucket((Item.Properties) properties, EntityType.ALLAY, Fluids.EMPTY, Items.BOOK,
				null,
				(entity, stack) -> {
					CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
						tag.putLong("DuplicationCooldown", entity.duplicationCooldown);
						entity.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER).ifPresent(uuid -> tag.store("liked_player", UUIDUtil.CODEC, uuid));

						ItemStack handItem = entity.getMainHandItem();
						if (!handItem.isEmpty()) {
							CompoundTag equipmentTag = new CompoundTag();
							equipmentTag.store("mainhand", ItemStack.CODEC, handItem);
							tag.put("equipment", equipmentTag);
						}

						var inventoryTag = new ListTag();
						entity.getInventory().getItems().forEach(item -> {
							if (item.isEmpty()) return;

							CompoundTag itemTag = new CompoundTag();
							itemTag.store(ItemStack.MAP_CODEC, item);
							inventoryTag.add(itemTag);
						});
						if (!inventoryTag.isEmpty())
							tag.put("Inventory", inventoryTag);
					});
				},
				(entity, tag) -> {
					entity.duplicationCooldown = tag.getLongOr("DuplicationCooldown", 0L);
					tag.read("liked_player", UUIDUtil.CODEC).ifPresent(uuid -> entity.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, uuid));

					entity.setItemInHand(InteractionHand.MAIN_HAND, tag.getCompoundOrEmpty("equipment").read("mainhand", ItemStack.CODEC).orElse(ItemStack.EMPTY));

					ListTag inventory = tag.getListOrEmpty("Inventory");
					inventory.forEach(itemTag -> {
						ItemStack item = itemTag.asCompound().flatMap(compound -> compound.read(ItemStack.MAP_CODEC)).orElse(ItemStack.EMPTY);
						if (!item.isEmpty())
							entity.getInventory().addItem(item);
					});
				}
			);
			case "vex_book" -> mobBucket((Item.Properties) properties, EntityType.VEX, Fluids.EMPTY, Items.BOOK,
				null,
				(entity, stack) -> {
					CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
						ItemStack handItem = entity.getMainHandItem();
						if (!handItem.isEmpty()) {
							CompoundTag equipmentTag = new CompoundTag();
							equipmentTag.store("mainhand", ItemStack.CODEC, handItem);
							tag.put("equipment", equipmentTag);
						}
					});
				},
				(entity, tag) -> {
					entity.setItemInHand(InteractionHand.MAIN_HAND, tag.getCompoundOrEmpty("equipment").read("mainhand", ItemStack.CODEC).orElse(ItemStack.EMPTY));
				}
			);
			default -> null;
		});

		context.injectEntityTypes(TrappedNewbieEntityTypes.class, TrappedNewbieEntities.class,
			key -> switch (key.value()) {
				case "paper_plane" -> new CraftEntityTypes.EntityTypeData<>(TrappedNewbieEntityTypes.PAPER_PLANE, PaperPlane.class, CraftPaperPlane::new, createAndMoveEmptyRot(TrappedNewbieEntities.PAPER_PLANE));
				case "glider" -> new CraftEntityTypes.EntityTypeData<>(TrappedNewbieEntityTypes.GLIDER, Glider.class, CraftGlider::new, createAndMoveEmptyRot(TrappedNewbieEntities.GLIDER));
				default -> throw new IllegalArgumentException();
			},
			key -> {
				switch (key.value()) {
					case "paper_plane" -> KiterinoBootstrapEntityTypeInjectorImpl.ENTITY_TYPE_REPLACEMENTS.put(TrappedNewbieEntities.PAPER_PLANE, EntityType.SNOWBALL);
					case "glider" -> KiterinoBootstrapEntityTypeInjectorImpl.ENTITY_TYPE_REPLACEMENTS.put(TrappedNewbieEntities.GLIDER, EntityType.ITEM_DISPLAY);
					default -> throw new IllegalArgumentException();
				}
			}
		);
	}


	private <T extends Mob> Item mobBucket(Item.Properties properties, EntityType<T> entityType, Fluid fluid, Item pickupBucket, @Nullable Predicate<T> check, @Nullable BiConsumer<T, ItemStack> save, @Nullable BiConsumer<T, CompoundTag> load) {
		SoundEvent pickupSound = switch (pickupBucket) {
			case Item item when item == Items.GLASS_BOTTLE -> SoundEvents.BOTTLE_FILL;
			case Item item when item == Items.BOOK -> SoundEvents.BOOK_PUT;
			default -> fluid == Fluids.LAVA ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
		};
		SoundEvent emptySound = switch (pickupBucket) {
			case Item item when item == Items.GLASS_BOTTLE -> SoundEvents.BOTTLE_EMPTY;
			case Item item when item == Items.BOOK -> SoundEvents.BOOK_PAGE_TURN;
			default -> fluid == Fluids.LAVA ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
		};
		MobBucketItem mobBucketItem = new MobBucketItem(entityType, fluid, emptySound, properties.component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
		Bucketable bucketable = new Bucketable() {
			@Override
			public boolean fromBucket() {
				return false;
			}

			@Override
			public void setFromBucket(boolean fromBucket) {
			}

			@Override
			public void saveToBucketTag(ItemStack stack) {
			}

			@Override
			public void loadFromBucketTag(CompoundTag tag) {
			}

			@Override
			public boolean canPickup(LivingEntity entity) {
				return check == null || check.test((T) entity);
			}

			@Override
			public void saveToBucketTag(LivingEntity entity, ItemStack stack) {
				if (entity instanceof Mob mob)
					Bucketable.saveDefaultDataToBucketTag(mob, stack);
				if (save != null)
					save.accept((T) entity, stack);
			}

			@Override
			public void loadFromBucketTag(LivingEntity entity, CompoundTag tag) {
				loadFromBucketTag(tag);
				if (load != null)
					load.accept((T) entity, tag);
			}

			@Override
			public ItemStack getBucketItemStack() {
				return new ItemStack(mobBucketItem);
			}

			@Override
			public SoundEvent getPickupSound() {
				return pickupSound;
			}

			@Override
			public Item getPickBucket() {
				return pickupBucket;
			}
		};
		DispenserBlock.registerBehavior(mobBucketItem, MobBucketItem.BUCKET_DISPENSE_BEHAVIOR);
		return mobBucketItem.bucketable(bucketable);
	}

}
