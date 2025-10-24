package me.sosedik.utilizer.impl.recipe;

import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.recipe.OneItemRecipe;
import me.sosedik.utilizer.util.RecipeManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * Builder for fire burning recipes
 */
@NullMarked
public class FireCraft extends OneItemRecipe<FireCraft> {

	private @Nullable BiConsumer<@Nullable Player, Location> action;
	private double burningChance = 0;

	public FireCraft(ItemStack result, NamespacedKey key) {
		super(result, new NamespacedKey(key.getNamespace(), key.value() + "_from_burning"));
	}

	public FireCraft withAction(@Nullable BiConsumer<@Nullable Player, Location> action) {
		this.action = action;
		return builder();
	}

	public FireCraft withBurnChance(double burningChance) {
		this.burningChance = burningChance;
		return builder();
	}

	public void performAction(@Nullable Player player, Block block) {
		if (!getResult().isEmpty()) {
			block.emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1F);
			if (this.burningChance == 0 || Math.random() > this.burningChance) {
				if (Tag.FIRE.isTagged(block.getType()))
					block.setType(Material.AIR);
				block.getWorld().dropItemNaturally(block.getLocation().center(), getResult(), item -> {
					item.setImmuneToFire(true);
					Utilizer.scheduler().sync(() -> item.setImmuneToFire(false), 30L);
				});
			}
		}
		if (this.action != null)
			this.action.accept(player, block.getLocation().center());
	}

	@Override
	public FireCraft register() {
		RecipeManager.addRecipe(this);
		return this;
	}

}
