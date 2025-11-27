package me.sosedik.miscme.listener.block;

import com.destroystokyo.paper.MaterialTags;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTType;
import io.papermc.paper.event.entity.TameableDeathMessageEvent;
import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.util.LocationUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Coal ore explodes on contact
 */
public class ExplosiveCoal implements Listener {

	private static final String EXPLOSION_MARKER_TAG = "coal_explosion";
	private static final String EXPLODER_TAG = "exploder";
	private static final String USED_ITEM_TAG = "used_item";

	private static final Tag<Material> EXTRA_TOOL_TRIGGERS = Objects.requireNonNull(Bukkit.getTag(Tag.REGISTRY_ITEMS, MiscMe.miscMeKey("coal_explosion_extra_tool_triggers"), Material.class));

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!isCoalOre(block.getType())) return;

		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (!isExplosiveTrigger(item, false)) return;

		triggerExplosion(block, player, item);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMine(BlockDamageEvent event) {
		if (event.getInstaBreak()) return;

		Block block = event.getBlock();
		if (!isCoalOre(block.getType())) return;

		ItemStack item = event.getItemInHand();
		if (!isExplosiveTrigger(item, true)) return;

		triggerExplosion(block, event.getPlayer(), item);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		Block blockAgainst = event.getBlockAgainst();
		if (!isCoalOre(blockAgainst.getType())) return;

		Block placedBlock = event.getBlockPlaced();
		if (!MaterialTags.TORCHES.isTagged(placedBlock)) return;

		Player player = event.getPlayer();
		ItemStack item = event.getItemInHand().clone(); // Preserve item a tick later
		MiscMe.scheduler().sync(() -> triggerExplosion(blockAgainst, player, item), 1L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFirePlace(BlockIgniteEvent event) {
		checkAroundFire(event.getBlock(), event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFireSpread(BlockSpreadEvent event) {
		if (!Tag.FIRE.isTagged(event.getNewState().getType())) return;

		checkAroundFire(event.getBlock(), null);
	}

	private void checkAroundFire(Block block, @Nullable Player player) {
		List<BlockFace> blockFaces = new ArrayList<>(LocationUtil.SURROUNDING_BLOCKS_UD);
		Collections.shuffle(blockFaces);
		for (BlockFace blockFace : blockFaces) {
			Block relativeBlock = block.getRelative(blockFace);
			if (!isCoalOre(relativeBlock.getType())) continue;

			ItemStack item = player == null ? null : player.getInventory().getItemInMainHand().clone(); // Preserve item a tick later
			MiscMe.scheduler().sync(() -> triggerExplosion(relativeBlock, player, item), 1L);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Component deathMessage = formDeathMessage(event.getEntity());
		if (deathMessage != null)
			event.deathMessage(deathMessage);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(TameableDeathMessageEvent event) {
		Component deathMessage = formDeathMessage(event.getEntity());
		if (deathMessage != null)
			event.deathMessage(deathMessage);
	}

	private @Nullable Component formDeathMessage(Entity entity) {
		if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent)) return null;
		if (!(damageEvent.getDamager() instanceof TNTPrimed tnt)) return null;
		if (!NBT.getPersistentData(tnt, nbt -> nbt.hasTag(EXPLOSION_MARKER_TAG))) return null;

		return NBT.getPersistentData(tnt, nbt -> {
			if (!nbt.hasTag(EXPLOSION_MARKER_TAG)) return null;
			if (!nbt.hasTag(EXPLOSION_MARKER_TAG, NBTType.NBTTagCompound)) return Component.translatable("death.attack.coal_explosion", entity.teamDisplayName());

			nbt = nbt.getCompound(EXPLOSION_MARKER_TAG);
			if (nbt == null) return Component.translatable("death.attack.coal_explosion", entity.teamDisplayName());

			UUID damagerUuid = nbt.getOrNull(EXPLODER_TAG, UUID.class);
			Player damager = damagerUuid == null ? null : Bukkit.getPlayer(damagerUuid);
			if (damager == null || damager == entity) return Component.translatable("death.attack.coal_explosion", entity.teamDisplayName());

			ItemStack item = nbt.hasTag(USED_ITEM_TAG) ? nbt.getItemStack(USED_ITEM_TAG) : null;
			if (item == null) return Component.translatable("death.attack.coal_explosion.player", entity.teamDisplayName(), damager.teamDisplayName());

			return Component.translatable("death.attack.coal_explosion.player.item", entity.teamDisplayName(), damager.teamDisplayName(), item.effectiveName().hoverEvent(item));
		});
	}

	private void triggerExplosion(Block block, @Nullable Player causer, @Nullable ItemStack item) {
		Entity source = Bukkit.getEntityFactory().createEntitySnapshot(EntityType.TNT).createEntity(block.getWorld());
		NBT.modifyPersistentData(source, nbt -> {
			if (causer == null && ItemStack.isEmpty(item)) {
				nbt.setBoolean(EXPLOSION_MARKER_TAG, true);
			} else {
				nbt = nbt.getOrCreateCompound(EXPLOSION_MARKER_TAG);
				if (causer != null) nbt.setUUID(EXPLODER_TAG, causer.getUniqueId());
				if (!ItemStack.isEmpty(item)) nbt.setItemStack(USED_ITEM_TAG, item);
			}
		});
		block.getWorld().createExplosion(source, block.getLocation(), 5, true);
	}

	private boolean isExplosiveTrigger(ItemStack item, boolean countTools) {
		Material type = item.getType();
		if (MaterialTags.TORCHES.isTagged(type)) return true;
		if (item.hasEnchant(Enchantment.FIRE_ASPECT)) return true;

		if (Tag.CAMPFIRES.isTagged(type)) {
			if (!item.hasBlockData()) return false;

			BlockData blockData = item.getBlockData(type);
			return blockData instanceof Campfire campfire && campfire.isLit();
		}

		if (!countTools) return false;
		if (EXTRA_TOOL_TRIGGERS.isTagged(type)) return true;

		return MaterialTags.IRON_TOOLS.isTagged(type)
			|| MaterialTags.GOLDEN_TOOLS.isTagged(type);
	}

	private boolean isCoalOre(Material type) {
		return Tag.COAL_ORES.isTagged(type);
	}

}
