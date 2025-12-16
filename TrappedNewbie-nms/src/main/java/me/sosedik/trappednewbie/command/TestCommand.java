package me.sosedik.trappednewbie.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Just testing stuff
 */
@NullMarked
@Permission("trapped_newbie.commands.test")
public class TestCommand {

	@Command("test")
	public void onCommand(
		CommandSourceStack stack
	) {
		if (!(stack.getExecutor() instanceof Player player)) {
			// Console (probably), yay!
			return;
		}

		TrappedNewbie.scheduler().sync(() -> {
			// Stuff!
//			TrappedNewbieAdvancements.IGNITE_A_CREEPER.showToast(player);
			List.of(Material.BOW, Material.CROSSBOW).forEach(axe -> {
				var item = ItemStack.of(axe);
				ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();
				RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).iterator().forEachRemaining(enchantment -> {
					if (enchantment.getSupportedItems().contains(TypedKey.create(RegistryKey.ITEM, item.getType().key())))
						builder.add(enchantment, enchantment.getMaxLevel());
				});
				item.setData(DataComponentTypes.ENCHANTMENTS, builder.build());
				item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
				player.getInventory().addItem(item);
			});
//			item.setData(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
//			player.getInventory().addItem(SoulboundNecronomicon.getNecronomicon(player));
//			Block block = player.getLocation().getBlock();
//			block.setType(Material.CHERRY_SIGN);
//			if (block.getState() instanceof Sign sign) {
//				sign.getSide(Side.FRONT).line(0, Component.text("text"));
//				sign.getSide(Side.FRONT).line(1, Component.text("text, but red", NamedTextColor.RED).hoverEvent(Component.text("smh")));
//				sign.update();
//			}
//			player.getWorld().spawn(player.getLocation(), Husk.class, e1 -> {
//				e1.setCanPickupItems(true);
//				e1.getEquipment().setHelmet(ItemStack.of(Material.JACK_O_LANTERN));
//			});
//			player.getInventory().addItem(AdvancementTrophies.produceTrophy(TrappedNewbieAdvancements.KILL_ALL_MOBS_WEARING_A_JACK_O_LANTERN, player));
		});
	}

}
