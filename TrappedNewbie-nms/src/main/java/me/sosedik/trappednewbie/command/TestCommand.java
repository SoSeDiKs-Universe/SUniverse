package me.sosedik.trappednewbie.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;

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
//			var item = ItemStack.of(Material.ENCHANTED_BOOK);
//			ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();
//			RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).iterator().forEachRemaining(enchantment -> builder.add(enchantment, enchantment.getMaxLevel()));
//			item.setData(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
//			player.getInventory().addItem(item);
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
