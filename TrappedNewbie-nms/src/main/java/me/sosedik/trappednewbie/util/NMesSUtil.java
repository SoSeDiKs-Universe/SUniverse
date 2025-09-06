package me.sosedik.trappednewbie.util;

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

/**
 * NMS is such a mess!
 */
@NullMarked
public class NMesSUtil {

	public static Map.@Nullable Entry<org.bukkit.inventory.ItemStack, Supplier<CraftItemEvent>> findCraftingRecipe(Player player, org.bukkit.inventory.@Nullable ItemStack[] matrix) { // TODO Switch to API (craft event is cancelled and handled manually, hence this hack)
		var nmsPlayer = ((CraftPlayer) player).getHandle();
		var playerInv = nmsPlayer.getInventory();
		var craftMenu = new CraftingMenu(Integer.MAX_VALUE, playerInv, ContainerLevelAccess.create(nmsPlayer.level(), nmsPlayer.blockPosition()));
		for (int i = 0; i < matrix.length; i++) {
			org.bukkit.inventory.ItemStack item = matrix[i];
			if (item != null)
				craftMenu.craftSlots.setItem(i, CraftItemStack.asNMSCopy(item));
		}
		craftMenu.slotsChanged(playerInv);

		RecipeHolder<CraftingRecipe> recipeHolder = ((CraftServer) Bukkit.getServer()).getHandle().getServer()
				.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftMenu.craftSlots.asCraftInput(), nmsPlayer.level()).orElse(null);
		if (recipeHolder == null) return null;

		ItemStack resultItem = craftMenu.resultSlots.getItem(0);
		if (resultItem.is(Items.AIR)) return null;

		return Map.entry(resultItem.asBukkitCopy(), () -> new CraftItemEvent(recipeHolder.toBukkitRecipe(), craftMenu.getBukkitView(), InventoryType.SlotType.RESULT, 0, ClickType.LEFT, InventoryAction.NOTHING));
	}

	public static @Nullable FurnaceRecipe getFurnaceRecipe(org.bukkit.inventory.ItemStack input) { // TODO This omits smelt events, but they require a furnace block, and I'm feeling lazy
		Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();
		while (recipeIterator.hasNext()) {
			if (!(recipeIterator.next() instanceof FurnaceRecipe furnaceRecipe)) continue;
			if (!furnaceRecipe.getInputChoice().test(input)) continue;

			return furnaceRecipe;
		}

		return null;
	}

	public static void sendBlockHighlight(Player player, Location loc, String text, Color color, int time) {
		if (!(player instanceof CraftPlayer craftPlayer)) return;
		ServerGamePacketListenerImpl connection = craftPlayer.getHandle().connection;
		if (connection == null) return;

		var payload = new GameTestAddMarkerDebugPayload(
			CraftLocation.toBlockPosition(loc),
			color.asARGB(),
			text,
			time
		);
		var packet = new ClientboundCustomPayloadPacket(payload);
		connection.send(packet);
	}

}
