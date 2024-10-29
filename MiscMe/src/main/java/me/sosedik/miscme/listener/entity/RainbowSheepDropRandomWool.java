package me.sosedik.miscme.listener.entity;

import me.sosedik.utilizer.util.ChatUtil;
import me.sosedik.utilizer.util.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Tag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Rainbow sheep (named jeb_) drop random wool
 */
public class RainbowSheepDropRandomWool implements Listener {

	private static final String RAINBOW_NAME = "jeb_";

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onShear(@NotNull PlayerShearEntityEvent event) {
		if (!isRainbowSheep(event.getEntity())) return;

		List<ItemStack> drops = new ArrayList<>(event.getDrops());
		replaceDrops(drops);
		event.setDrops(drops);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onShear(@NotNull BlockShearEntityEvent event) {
		if (!isRainbowSheep(event.getEntity())) return;

		List<ItemStack> drops = new ArrayList<>(event.getDrops());
		replaceDrops(drops);
		event.setDrops(drops);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(@NotNull EntityDeathEvent event) {
		if (!isRainbowSheep(event.getEntity())) return;

		replaceDrops(event.getDrops());
	}

	private void replaceDrops(@NotNull List<ItemStack> drops) {
		drops.replaceAll(item -> {
			if (!Tag.WOOL.isTagged(item.getType())) return item;
			return getRandomWool(item.getAmount());
		});
	}

	private boolean isRainbowSheep(@NotNull Entity entity) {
		if (!(entity instanceof Sheep)) return false;

		Component name = entity.customName();
		if (name == null) return false;

		// Vanilla allows component styling, hence check against the raw text content
		return RAINBOW_NAME.equals(ChatUtil.getPlainText(name));
	}

	private @NotNull ItemStack getRandomWool(int amount) {
		return ItemStack.of(MathUtil.getRandom(Tag.WOOL.getValues()), amount);
	}

}
