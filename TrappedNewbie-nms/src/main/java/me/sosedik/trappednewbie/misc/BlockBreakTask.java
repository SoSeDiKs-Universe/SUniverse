package me.sosedik.trappednewbie.misc;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.resourcelib.impl.block.nms.BarrierNMSBlock;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.listener.block.CustomBlockBreaking;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.utilizer.api.math.WorldChunkPosition;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@NullMarked
public class BlockBreakTask extends BukkitRunnable {

	private static final Map<WorldChunkPosition, Map<BlockPosition, BlockBreakState>> DESTROYS = new HashMap<>();
	private static final List<BiFunction<BlockBreakTask, Float, @Nullable Float>> BREAKING_RULES = new ArrayList<>();
	private static final int REFRESH_DELAY = 2 * 20;
	private static final int DECAY_DELAY = 3 * 60 * 20;
	private static final int DECAY_RATE = 5 * 20;

	private final Block block;
	private final DestroyState destroyState;
	private final Player player;
	private ItemStack tool;
	private @Nullable ItemStack hammer;
	private boolean properTool;
	private boolean brokenTool;
	private int failures = -5;

	public BlockBreakTask(Block block, Player player) {
		// Init
		this.block = block;
		this.player = player;
		this.tool = player.getInventory().getItemInMainHand();

		// Register block as currently breakable
		BlockBreakState blockBreakState = getState(block);
		this.destroyState = blockBreakState.destroyState();
		BlockBreakTask oldTask = blockBreakState.blockBreakers().get(player.getUniqueId());
		if (oldTask != null && oldTask.isScheduled()) return;

		// Make sure the player has up-to-date crack state
		sendBreak();

		// Store needed values
		this.properTool = GameModeSwitcherTask.isApplicableForBreak(player, block, this.tool);
		this.brokenTool = DurabilityUtil.isBroken(this.tool);

		// Hammer block replacing
		if (!this.properTool && !this.brokenTool && TrappedNewbieTags.HAMMERS.isTagged(this.tool.getType()) && canHammerBreak()) {
			this.properTool = true;
			ItemStack bestProperTool = null;
			float bestDestroyTime = Float.MAX_VALUE;
			for (ItemStack invItem : player.getInventory().getContents()) {
				if (ItemStack.isEmpty(invItem)) continue;
				if (!GameModeSwitcherTask.isApplicableForBreak(player, block, this.tool)) continue;
				if (DurabilityUtil.isBroken(invItem)) continue;

				this.tool = invItem;
				float destroySpeed = getDestroySpeed();
				if (destroySpeed < bestDestroyTime) {
					bestProperTool = invItem;
					bestDestroyTime = destroySpeed;
				}
			}
			if (bestProperTool == null) {
				this.properTool = false;
				this.tool = player.getInventory().getItemInMainHand();
			} else {
				this.tool = bestProperTool;
				this.hammer = player.getInventory().getItemInMainHand();
			}
		}

		// Unbreakable
		if (isUnbreakable(block.getType())) {
			clearBlock(block);
			return;
		}

		// Insta-break
		if (!this.brokenTool && canSee()) {
			if (getDestroySpeed() == 0) {
				if (this.properTool) {
					clearBlock(block);
					breakBlock();
				}
				return;
			}
		}

		runTaskTimer(TrappedNewbie.instance(), 5L, 1L);

		blockBreakState.blockBreakers().put(player.getUniqueId(), this);
	}

	/**
	 * Gets the block being broken
	 *
	 * @return block
	 */
	public Block getBlock() {
		return this.block;
	}

	/**
	 * Gets the player breaking the block
	 *
	 * @return player
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Gets the tool used to break the block
	 *
	 * @return the tool used to break the block
	 */
	public ItemStack getTool() {
		return this.tool;
	}

	private boolean canHammerBreak() {
		ItemStack offHandItem = this.player.getInventory().getItemInOffHand();
		if (!offHandItem.getType().isBlock()) return false;
		if (offHandItem.getType() == this.block.getType()) return false;
		return offHandItem.getType() != Material.AIR;
	}

	private boolean isUnbreakable(Material type) {
		return type == Material.BEDROCK || type == Material.BARRIER || type == Material.COMMAND_BLOCK;
	}

	private boolean canSee() {
		return EntityUtil.canSee(this.player);
	}

	@Override
	public void run() {
		// Check if block breaking should still be allowed
		if (shouldBeAborted()) {
			abort();
			return;
		}

		// No breaking in darkness
		if (!canSee()) {
			this.failures++;
			if (this.failures == 25) {
//				this.player.showTitle(DARKNESS_TITLE);
				HudMessenger.of(this.player).displayMessage(Messenger.messenger(this.player).getMessage("break.light"));
				this.failures = 0;
			}
			return;
		}

		// Send proper tools
		if (!this.properTool) {
			this.failures++;
			if (this.failures == 25) {
//				this.player.showTitle(properToolTitle);
				this.failures = 0;
			}
			return;
		}

		// Can't break with a broken tool :(
		if (this.brokenTool) {
			this.failures++;
			if (this.failures == 25) {
//				this.player.showTitle(BROKEN_TOOL_TITLE);
				this.failures = 0;
			}
			return;
		}

		// Yay, we are breaking the block!
		tryToCrack();
	}

	private boolean shouldBeAborted() {
		return !this.player.isOnline()
			|| this.player.getGameMode() != GameMode.SURVIVAL
			|| !this.player.getInventory().getItemInMainHand().isSimilar(this.hammer == null ? this.tool : this.hammer)
			|| !checkBlockExistence()
			|| !isPlayerTargetingThisBlock();
	}

	private void tryToCrack() {
		if (this.player.hasCooldown(this.tool.getType())) return;

		int next = getDestroyTick();
		if (this.destroyState.tick(next)) {
			if (this.destroyState.crack()) {
				abort();
				if (this.destroyState.crack <= 10) // No need to break twice in case multiple players are breaking
					breakBlock();
				return;
			}
			if (next < 0) this.destroyState.crack = -next;
			sendBreak();
		}
	}

	private int getDestroyTick() {
		if (this.destroyState.crack > 9) return 0;
		if (this.destroyState.crack < 0) return 0;

		int[] ticks = getDestroyTicks(getDestroyTime(), 10);
		if (ticks.length == 10) return ticks[this.destroyState.crack];
		for (int next : ticks) {
			if (next > this.destroyState.crack)
				return -next;
		}

		return 0;
	}

	private int[] getDestroyTicks(int ticks, int count) {
		if (ticks < 9) return getDestroyTicks(9, ticks);
		if (count < 0) return new int[]{5};
		int[] result = new int[count];
		float ones = (float) ticks / count;
		float res = 0;
		for (int i = 0; i < count; i++) {
			res += ones;
			result[i] = i == count - 1 ? Math.round(res) : (int) res;
		}
		if (count != 10) return result;
		for (int i = count - 1; i > 0; i--)
			result[i] -= result[i - 1];
		return result;
	}

	private int getDestroyTime() {
		return (int) (getDestroySpeed() * 20);
	}

	private float getDestroySpeed() {
		float seconds = getHardness();
		// Client-side insta-breakable block, can't do custom rules
		if (seconds == 0) return 0;

		float bonus = getDestroyDamage();
		if (bonus >= seconds * 30) {
			for (var rule : BREAKING_RULES) {
				Float s = rule.apply(this, seconds);
				if (s != null)
					return s;
			}
			return 0;
		}

		seconds *= this.properTool ? 1.5F : 5F; // Vanilla: multiply by 5 if not a proper tool
		if (bonus != 0) seconds /= bonus;

		if (this.player.isUnderWater()) {
			ItemStack helmet = this.player.getInventory().getHelmet();
			if (ItemStack.isEmpty(helmet) || !helmet.containsEnchantment(Enchantment.AQUA_AFFINITY)) {
				VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(this.player);
				helmet = visualArmor.canUseVisualArmor() && visualArmor.hasHelmet() ? visualArmor.getHelmet() : null;
			}
			if (ItemStack.isEmpty(helmet) || !helmet.containsEnchantment(Enchantment.AQUA_AFFINITY))
				seconds *= 5F;
		}
		if (!this.player.isOnGround() && !this.player.isFlying()) {
			LivingEntity possessed = PossessingPlayer.getPossessed(this.player);
			if (possessed != null) {
				if (!UtilizerTags.MOBS_WITH_HANDS.isTagged(possessed.getType()))
					seconds *= 2F;
			} else {
				seconds *= 5F;
			}
		}

		seconds = (float) (Math.ceil(seconds * 20) / 20D);

		for (var rule : BREAKING_RULES) {
			Float s = rule.apply(this, seconds);
			if (s != null)
				return s;
		}

		return seconds;
	}

	private float getHardness() {
		return this.block.getType().getHardness();
	}

	private boolean isPlayerTargetingThisBlock() {
		Block targetBlock = this.player.getTargetBlockExact(5);
		return this.block.equals(targetBlock);
	}

	private void abort() {
		if (isScheduled())
			cancel();

		Map<BlockPosition, BlockBreakState> breakTaskMap = DESTROYS.get(WorldChunkPosition.of(this.block.getChunk()));
		if (breakTaskMap != null) {
			BlockBreakState blockBreakState = breakTaskMap.get(Position.block(this.block.getLocation()));
			if (blockBreakState != null)
				blockBreakState.blockBreakers().remove(this.player.getUniqueId());
		}
		if (this.destroyState.crack < 0 || this.destroyState.crack >= 10) {
			clearBlock(this.block);
		} else {
			sendBreak();
		}
	}

	private void breakBlock() {
		BlockData blockData = this.block.getBlockData();
		SoundGroup soundGroup = this.block.getBlockSoundGroup();

		BlockState blockState = this.block.getState();
		if (this.hammer != null) {
			ItemStack tempStack = this.hammer.clone();
			this.player.getInventory().setItemInMainHand(this.tool.asOne());
			this.tool.damage(1, this.player);
			boolean broke = this.player.breakBlock(this.block);
			this.player.getInventory().setItemInMainHand(tempStack);
			if (!broke) return;
		} else {
			if (!this.player.breakBlock(this.block)) return;
		}

		if (Tag.FIRE.isTagged(this.block.getType())) {
			this.block.emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 1F, 0.9F + (float) Math.random() * 0.2F);
		} else {
			(((CraftWorld) this.block.getWorld()).getHandle()).levelEvent(net.minecraft.world.level.block.LevelEvent.PARTICLES_DESTROY_BLOCK, CraftLocation.toBlockPosition(this.block.getLocation()), net.minecraft.world.level.block.Block.getId(((CraftBlockState) blockState).getHandle()));
			this.block.emitSound(soundGroup.getBreakSound(), 1F, (float) Math.random() * 0.4F + 0.8F);
		}

		if (this.hammer != null) {
			if (this.player.getInventory().getItemInOffHand().isEmpty()) return;
			if (!this.player.placeBlock(EquipmentSlot.OFF_HAND, this.block.getLocation(), BlockFace.UP, true)) return;

			migrateBlockData(blockData);
		}
	}

	private void migrateBlockData(BlockData preData) {
		BlockData blockData = this.block.getBlockData();
		preData.copyTo(blockData);
		this.block.setBlockData(blockData);
	}

	private boolean checkBlockExistence() {
		if (this.block.isEmpty()) {
			this.destroyState.crack = -1;
			this.destroyState.updateDisplay();
			return false;
		}
		return true;
	}

	private float getDestroyDamage() {
		if (this.player.hasCooldown(this.tool.getType())) return 0F;

		float damage = this.block.getDestroySpeed(this.tool);

		AttributeInstance attribute = this.player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
		if (attribute != null) {
			attribute.removeModifier(CustomBlockBreaking.MODIFIER);
			damage *= (float) attribute.getValue();
			attribute.addTransientModifier(CustomBlockBreaking.MODIFIER);
		}

		if (this.properTool) {
			int enchantment = this.tool.getEnchantmentLevel(Enchantment.EFFICIENCY);
			if (enchantment != 0) damage += enchantment * enchantment + 1;
		}

		PotionEffect effect = this.player.getPotionEffect(PotionEffectType.HASTE);
		if (effect != null) damage *= (float) (1 + (0.2 * effect.getAmplifier()));

		effect = this.player.getPotionEffect(PotionEffectType.MINING_FATIGUE);
		if (effect != null) damage /= (float) Math.pow(3, effect.getAmplifier());

		return damage;
	}

	private static BlockBreakState getState(Block block) {
		return DESTROYS.computeIfAbsent(WorldChunkPosition.of(block.getChunk()), k -> new HashMap<>()).computeIfAbsent(Position.block(block.getLocation()), k -> BlockBreakState.buildEmptyState(block));
	}

	private void sendBreak() {
		sendCrack(this.destroyState.counter, this.block, this.destroyState.crack);

		BlockData blockData = this.block.getBlockData();
		if (blockData instanceof Door door) {
			setCracks(this.block.getRelative(door.getHalf() == Bisected.Half.BOTTOM ? BlockFace.UP : BlockFace.DOWN), this.destroyState.crack, false);
		} else if (blockData instanceof Bed bed) {
			Block secondBlock = this.block.getRelative(bed.getFacing());
			if (secondBlock.getBlockData() instanceof Bed) {
				setCracks(secondBlock, this.destroyState.crack, false);
			} else {
				secondBlock = this.block.getRelative(bed.getFacing().getOppositeFace());
				if (secondBlock.getBlockData() instanceof Bed)
					setCracks(secondBlock, this.destroyState.crack, false);
			}
		}
	}

	private static class DestroyState {

		int counter;
		int crack;
		int tick;
		@Nullable ItemDisplay display;

		DestroyState() {
			this.crack = -1;
			this.tick = 0;
			this.counter = Bukkit.getUnsafe().nextEntityId();
		}

		boolean tick(int value) {
			if (++this.tick >= value) {
				this.tick = 0;
				return true;
			}
			return false;
		}

		boolean crack() {
			this.crack++;
			updateDisplay();
			return this.crack >= 10;
		}

		void updateDisplay() {
			if (this.display == null) return;

			Material type = switch (this.crack) {
				case 0 -> TrappedNewbieItems.DESTROY_STAGE_0;
				case 1 -> TrappedNewbieItems.DESTROY_STAGE_1;
				case 2 -> TrappedNewbieItems.DESTROY_STAGE_2;
				case 3 -> TrappedNewbieItems.DESTROY_STAGE_3;
				case 4 -> TrappedNewbieItems.DESTROY_STAGE_4;
				case 5 -> TrappedNewbieItems.DESTROY_STAGE_5;
				case 6 -> TrappedNewbieItems.DESTROY_STAGE_6;
				case 7 -> TrappedNewbieItems.DESTROY_STAGE_7;
				case 8 -> TrappedNewbieItems.DESTROY_STAGE_8;
				case 9 -> TrappedNewbieItems.DESTROY_STAGE_9;
				default -> null;
			};
			if (type == null)
				this.display.remove();
			else
				this.display.setItemStack(ItemStack.of(type));
		}

		public void spawnDisplay(Location loc) {
			this.display = loc.getWorld().spawn(loc, ItemDisplay.class, itemDisplay -> {
				itemDisplay.setPersistent(false);
				itemDisplay.setItemStack(ItemStack.of(TrappedNewbieItems.MATERIAL_AIR));
				Transformation transformation = itemDisplay.getTransformation();
				transformation.getScale().set(1.01);
				itemDisplay.setTransformation(transformation);
			});
		}

	}

	private record BlockBreakState(DestroyState destroyState, Map<UUID, BlockBreakTask> blockBreakers) {

		static BlockBreakState buildEmptyState(Block block) {
			var destroyState = new DestroyState();
			if (BarrierNMSBlock.isBarrierBlock(block.getType()))
				destroyState.spawnDisplay(block.getLocation().center());
			keepBlock(block, destroyState);
			decayBlock(block, destroyState);
			return new BlockBreakState(destroyState, new HashMap<>());
		}

		/**
		 * Resends cracks to players because
		 * they are cleared up client-side
		 * after some time
		 */
		private static void keepBlock(Block block, DestroyState destroyState) {
			TrappedNewbie.scheduler().async(task -> {
				int crack = destroyState.crack;
				if (crack < 1) return true;
				if (crack > 9) return true;

				sendCrack(destroyState.counter, block, destroyState.crack);
				return false;
			}, REFRESH_DELAY, REFRESH_DELAY);
		}

		/**
		 * Automatically recovers block from cracks
		 */
		private static void decayBlock(Block block, DestroyState destroyState) {
			int crack = destroyState.crack;
			if (crack < 1 || crack > 9) return;

			TrappedNewbie.scheduler().async(() -> {
				if (crack != destroyState.crack) {
					decayBlock(block, destroyState);
					return;
				}

				int[] lastCrack = {crack};
				TrappedNewbie.scheduler().async(task -> {
					if (lastCrack[0] > destroyState.crack) {
						decayBlock(block, destroyState);
						return true;
					}
					lastCrack[0] = --destroyState.crack;
					if (destroyState.crack <= 1) {
						clearBlock(block);
						return true;
					}
					sendCrack(destroyState.counter, block, destroyState.crack);
					return false;
				}, DECAY_RATE, DECAY_RATE);
			}, DECAY_DELAY);
		}

	}

	public static boolean clearBlock(Block block) {
		var chunkLocation = WorldChunkPosition.of(block.getChunk());
		Map<BlockPosition, BlockBreakState> blockStates = DESTROYS.get(chunkLocation);
		if (blockStates == null) return false;

		BlockBreakState state = DESTROYS.get(chunkLocation).remove(Position.block(block.getLocation()));
		if (state == null) return false;

		int crack = state.destroyState().counter;
		state.blockBreakers().values().forEach(BlockBreakTask::cancel);
		state.destroyState().crack = -1;
		state.destroyState().updateDisplay();
		if (state.destroyState().display == null)
			sendCrack(state.destroyState().counter, block, 10);

		if (crack < 10) {
			BlockData blockData = block.getBlockData();
			if (blockData instanceof Door door) {
				clearBlock(block.getRelative(door.getHalf() == Bisected.Half.BOTTOM ? BlockFace.UP : BlockFace.DOWN));
			} else if (blockData instanceof Bed bed) {
				Block secondBlock = block.getRelative(bed.getFacing());
				if (secondBlock.getBlockData() instanceof Bed) {
					clearBlock(secondBlock);
				} else {
					secondBlock = block.getRelative(bed.getFacing().getOppositeFace());
					if (secondBlock.getBlockData() instanceof Bed)
						clearBlock(block.getRelative(bed.getFacing().getOppositeFace()));
				}
			}
		}
		// Don't consider single crack as cleared
		return crack > 0 && crack < 10;
	}

	public static boolean isCracked(Block block) {
		var chunkLocation = WorldChunkPosition.of(block.getChunk());
		Map<BlockPosition, BlockBreakState> blockStates = DESTROYS.get(chunkLocation);
		if (blockStates == null) return false;

		BlockPosition BlockPosition = Position.block(block.getLocation());
		BlockBreakState state = blockStates.get(BlockPosition);
		if (state == null) return false;

		int crack = state.destroyState().crack;
		return crack >= 0 && crack < 10;
	}

	public static void setCracks(Block block, int cracks) {
		setCracks(block, cracks, true);
	}

	public static void setCracks(Block block, int cracks, boolean checkExtra) {
		DestroyState state = getState(block).destroyState();
		state.crack = cracks;
		state.updateDisplay();
		sendCrack(state.counter, block, state.crack);
		if (!checkExtra) return;

		BlockData blockData = block.getBlockData();
		if (blockData instanceof Door door) {
			setCracks(block.getRelative(door.getHalf() == Bisected.Half.BOTTOM ? BlockFace.UP : BlockFace.DOWN), cracks, false);
		} else if (blockData instanceof Bed bed) {
			Block secondBlock = block.getRelative(bed.getFacing());
			if (secondBlock.getBlockData() instanceof Bed) {
				setCracks(secondBlock, cracks, false);
			} else {
				secondBlock = block.getRelative(bed.getFacing().getOppositeFace());
				if (secondBlock.getBlockData() instanceof Bed)
					setCracks(block.getRelative(bed.getFacing().getOppositeFace()), cracks, false);
			}
		}
	}

	private static void sendCrack(int entityId, Block block, int cracks) {
		TrappedNewbie.scheduler().sync(() -> {
			Location loc = block.getLocation().center();
			loc.getNearbyPlayers(30).forEach(p -> p.sendBlockDamage(loc, cracks < 0 || cracks > 9 ? 0F : Math.clamp((cracks + 1) / 10F, 0F, 1F), entityId));
		});
	}

	public static void clearChunk(Chunk chunk) {
		var chunkLocation = WorldChunkPosition.of(chunk);
		if (!DESTROYS.containsKey(chunkLocation)) return;

		TrappedNewbie.scheduler().async(() -> {
			if (chunk.isLoaded()) return;

			Map<BlockPosition, BlockBreakState> blockStates = DESTROYS.remove(chunkLocation);
			if (blockStates == null) return;

			blockStates.values().forEach(blockBreakState -> {
				blockBreakState.destroyState().crack = -1;
				blockBreakState.destroyState().updateDisplay();
				blockBreakState.blockBreakers().values().forEach(BlockBreakTask::cancel);
			});
		}, 2 * 60 * 20L);
	}

	public static boolean stopBreaks(Player player, Block block) {
		Map<BlockPosition, BlockBreakState> blockBreakTaskMap = DESTROYS.get(WorldChunkPosition.of(block.getChunk()));
		if (blockBreakTaskMap == null) return false;

		BlockBreakState blockBreakState = blockBreakTaskMap.get(Position.block(block.getLocation()));
		if (blockBreakState == null) return false;

		BlockBreakTask breakTask = blockBreakState.blockBreakers().get(player.getUniqueId());
		if (breakTask == null) return false;

		breakTask.abort();

		return true;
	}

	public static void addBreakingRule(BiFunction<BlockBreakTask, Float, @Nullable Float> rule) {
		BREAKING_RULES.add(rule);
	}

}
