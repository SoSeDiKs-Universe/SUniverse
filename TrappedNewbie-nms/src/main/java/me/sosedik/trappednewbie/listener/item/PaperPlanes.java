package me.sosedik.trappednewbie.listener.item;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.miscme.listener.item.ImmersiveDyes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.entity.api.PaperPlane;
import me.sosedik.utilizer.impl.item.modifier.GlowingItemModifier;
import me.sosedik.utilizer.util.GlowingUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Paper planes mechanics
 */
@NullMarked
public class PaperPlanes implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLoad(EntitiesLoadEvent event) {
		event.getEntities().forEach(entity -> {
			if (entity instanceof Item item)
				applyGlow(item);
		});
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDrop(ItemSpawnEvent event) {
		applyGlow(event.getEntity());
	}

	private void applyGlow(Item item) {
		ItemStack itemStack = item.getItemStack();
		if (itemStack.getType() != TrappedNewbieItems.PAPER_PLANE) return;
		if (!NBT.get(itemStack, nbt -> (boolean) nbt.getOrDefault(GlowingItemModifier.GLOW_MODIFIER_KEY, false))) return;

		item.setGlowing(true);
		if (itemStack.hasData(DataComponentTypes.DYED_COLOR)) {
			Color color = Objects.requireNonNull(itemStack.getData(DataComponentTypes.DYED_COLOR)).color();
			GlowingUtil.setGlowingColor(item, NamedTextColor.nearestTo(color));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLand(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof PaperPlane)) return;

		Block hitBlock = event.getHitBlock();
		BlockFace hitBlockFace = event.getHitBlockFace();
		if (hitBlock == null) return;
		if (hitBlockFace == null) return;

		Block block = hitBlock.getRelative(hitBlockFace);
		if (!Tag.WOODEN_BUTTONS.isTagged(block.getType())) return;

		pressButton(block);
	}

	private void pressButton(Block block) {
		if (!(block instanceof CraftBlock craftBlock)) return;
		if (!(craftBlock.getHandle() instanceof Level level)) return;

		BlockState nmsBlockState = craftBlock.getNMS();
		if (nmsBlockState == null) return;
		if (!(nmsBlockState.getBlock() instanceof ButtonBlock buttonBlock)) return;

		buttonBlock.press(nmsBlockState, level, craftBlock.getPosition(), null);
	}

	@EventHandler(ignoreCancelled = true)
	public void onModifierApply(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame itemFrame)) return;

		ItemStack item = itemFrame.getItem();
		if (item.getType() != TrappedNewbieItems.PAPER_PLANE) return;

		Player player = event.getPlayer();

		if (tryToApplyModifier(event, itemFrame, item, player, EquipmentSlot.HAND))
			tryToApplyModifier(event, itemFrame, item, player, EquipmentSlot.OFF_HAND);
	}

	private boolean tryToApplyModifier(PlayerInteractEntityEvent event, ItemFrame itemFrame, ItemStack item, Player player, EquipmentSlot hand) {
		ItemStack handItem = player.getInventory().getItem(hand);

		boolean modifiedItem = false;
		if (handItem.getType() == Material.GLOWSTONE_DUST) {
			if (!NBT.get(item, nbt -> (boolean) nbt.hasTag(GlowingItemModifier.GLOW_MODIFIER_KEY))) {
				modifiedItem = true;
				NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setBoolean(GlowingItemModifier.GLOW_MODIFIER_KEY, true));
			}
		} else if (handItem.getType() == Material.BLAZE_POWDER) {
			if (!NBT.get(item, nbt -> (boolean) nbt.hasTag(PaperPlane.BLAZIFIED_TAG))) {
				modifiedItem = true;
				NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setBoolean(PaperPlane.BLAZIFIED_TAG, true));
			}
		} else if (handItem.getType() == Material.GUNPOWDER) {
			if (!NBT.get(item, nbt -> (boolean) nbt.hasTag(PaperPlane.FRAGILE_TAG))) {
				modifiedItem = true;
				NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setBoolean(PaperPlane.FRAGILE_TAG, true));
			}
		} else {
			return false;
		}
		player.swingHand(hand);
		event.setCancelled(true);
		if (!modifiedItem) return false;

		itemFrame.setItem(item);

		ImmersiveDyes.playEffect(player, null, itemFrame.getLocation(), null);

		if (Math.random() < ImmersiveDyes.DYE_REDUCE_CHANCE)
			handItem.subtract();

		return true;
	}

}
