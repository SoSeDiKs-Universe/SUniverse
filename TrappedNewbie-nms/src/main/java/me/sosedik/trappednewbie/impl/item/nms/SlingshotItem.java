package me.sosedik.trappednewbie.impl.item.nms;

import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieSoundKeys;
import net.kyori.adventure.sound.Sound;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@NullMarked
public class SlingshotItem extends ProjectileWeaponItem {

	private static final List<Item> PROJECTILES = new ArrayList<>();

	static {
		PROJECTILES.addAll(List.of(
			Items.IRON_NUGGET,
			Items.GOLD_NUGGET,
			Items.COPPER_NUGGET,
			Items.AMETHYST_SHARD,
			Items.ENDER_PEARL,
			Items.FIRE_CHARGE
		));
	}

	private @Nullable ItemStack weapon;

	public SlingshotItem(Properties properties) {
		super(properties);
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return x -> PROJECTILES.contains(x.getItem());
//		return x -> !x.isEmpty();
	}

	@Override
	public int getDefaultProjectileRange() {
		return 13;
	}

	@Override
	protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
		if (ammo.isEmpty()) ammo = Items.IRON_NUGGET.getDefaultInstance();

		boolean forceItem = false;
		if (!forceItem) {
			if (ammo.getItem() instanceof ArrowItem arrowItem) {
				var projectile = arrowItem.createArrow(level, ammo, shooter, weapon);
				projectile.setCritArrow(isCrit);
				return projectile;
			}

			if (ammo.getItem() instanceof ProjectileItem projectileItem) {
				Vec3 pos = shooter.getEyePosition().subtract(0, 0.1, 0);
				Projectile projectile = projectileItem.asProjectile(level, pos, ammo, shooter.getNearestViewDirection());
				projectile.setOwner(shooter);
				return projectile;
			}

			if (ammo.is(Items.ENDER_PEARL)) {
				if (shooter instanceof ServerPlayer serverPlayer && ammo.has(DataComponents.USE_COOLDOWN)) {
					int cooldown = Objects.requireNonNull(ammo.get(DataComponents.USE_COOLDOWN)).ticks() / 2;
					serverPlayer.getCooldowns().addCooldown(weapon, cooldown);
					serverPlayer.getCooldowns().addCooldown(ammo, cooldown);
				}
				return new ThrownEnderpearl(level, shooter, ammo);
			}
		}

		return super.createProjectile(level, shooter, weapon, ammo, isCrit);
	}

	@Override
	protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
		projectile.shootFromRotation(shooter, shooter.getXRot() - (projectile instanceof Fireball ? 0 : 5), shooter.getYRot() + yaw, 0.0F, speed, divergence);
//		if (projectile instanceof ItemProjectileEntity entity && index != 0) {
//			entity.setReal(false);
//		}
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		ItemStack weaponItem = user.getItemInHand(hand);
		this.weapon = weaponItem;
		ItemStack projectileItem = user.getProjectile(weaponItem);
		this.weapon = null;
		if (projectileItem.isEmpty())
			return InteractionResult.FAIL;
//		ProjectileStack projectileStack = getProjectileTypeSource(user, user.getItemInHand(hand), hand);
//		if (projectileStack.isEmpty())
//			return InteractionResult.FAIL;

		user.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.onUseTick(world, user, stack, remainingUseTicks);

		if (!(user instanceof ServerPlayer player)) return;
		if (!(world instanceof ServerLevel serverWorld)) return;

//		var projectileSource = getProjectileTypeSource(player, stack, user.getUsedItemHand());
//		if (projectileSource.isEmpty()) return;

		int useTime = this.getUseDuration(stack, user) - remainingUseTicks;

		if (useTime == 8) {
			world.getWorld().playSound(Sound.sound(TrappedNewbieSoundKeys.SLINGSHOT_LOAD, Sound.Source.PLAYER, 1F, 1 + world.getRandom().nextFloat() * 0.2F), player.getX(), player.getEyeY(), player.getZ());
		}
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
		if (!(world instanceof ServerLevel serverWorld)) return false;

		this.weapon = stack;
		ItemStack projectileItem = user.getProjectile(stack);
		this.weapon = null;
		if (projectileItem.isEmpty())
			return false;

		int useTime = this.getUseDuration(stack, user) - remainingUseTicks;
		float progress = getPullProgress(useTime, stack, user);
		if (progress < 0.3F)
			return false;

		List<ItemStack> projectiles = draw(stack, projectileItem, user, true);
		shoot(serverWorld, user, user.getUsedItemHand(), stack, projectiles, getSpeed(stack, serverWorld, user, progress), 1F, false, null, progress);

		world.getWorld().playSound(Sound.sound(TrappedNewbieSoundKeys.SLINGSHOT_SHOOT, Sound.Source.PLAYER, 1F, 1 / (world.getRandom().nextFloat() * 0.4F + 1.2F) + progress * 0.5F), user.getX(), user.getEyeY(), user.getZ());

		if (user instanceof ServerPlayer serverPlayer)
			serverPlayer.awardStat(Stats.ITEM_USED.get(this));

		return true;
	}

	private float getSpeed(ItemStack stack, ServerLevel world, LivingEntity user, float progress) {
		boolean extraSpeed = false; // stack.is(SlingshotItemTags.EXTRA_PROJECTILE_SPEED)
		var val = new MutableFloat(progress * (1.4F + (extraSpeed ? 0.5F : 0)));
//		for (var ench : EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) {
//			ench.getKey().value().modifyEntityFilteredValue(SlingshotEnchantmentComponents.SLINGSHOT_STRENGTH, world, ench.getIntValue(), stack, user, val);
//		}
		return val.floatValue();
	}

	public static float getPullProgress(int useTicks, ItemStack stack, LivingEntity user) {
		float f = useTicks / 20F;
		f = (f * f + f * 2F) / 3F;
		f /= EnchantmentHelper.modifyCrossbowChargingTime(stack, user, 1);

		if (f > 1F) f = 1F;

		return f;
	}

	public ProjectileStack getProjectileTypeSource(Player player, ItemStack weapon, InteractionHand hand) {
		if (hand == InteractionHand.OFF_HAND) {
			ItemStack projectileItem = player.getMainHandItem();
			if (getAllSupportedProjectiles().test(projectileItem))
				return new ProjectileStack(projectileItem, player.getInventory().getSelectedSlot());
		}

		ItemStack projectileItem = player.getOffhandItem();
		if (getAllSupportedProjectiles().test(projectileItem))
			return new ProjectileStack(projectileItem, InventorySlotHelper.OFF_HAND);

		for (int i = 0; i < 9; i++) {
			var itemStack = player.getInventory().getItem(i);
			if (getAllSupportedProjectiles().test(itemStack))
				return new ProjectileStack(itemStack, i);
		}

		if (player.isCreative()) {
			return new ProjectileStack(ItemStack.fromBukkitCopy(org.bukkit.inventory.ItemStack.of(TrappedNewbieItems.ROCK)), -1);
		}

		return ProjectileStack.EMPTY;
	}

	public record ProjectileStack(ItemStack stack, int slot) {

		public static ProjectileStack EMPTY = new ProjectileStack(ItemStack.EMPTY, -1);

		public boolean isEmpty() {
			return this.stack.isEmpty();
		}

	}

	public static void addProjectile(Item item) {
		PROJECTILES.add(item);
	}

}
