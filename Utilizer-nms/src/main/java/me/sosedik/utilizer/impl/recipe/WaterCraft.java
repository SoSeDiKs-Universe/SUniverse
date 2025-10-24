package me.sosedik.utilizer.impl.recipe;

import me.sosedik.utilizer.api.recipe.OneItemRecipe;
import me.sosedik.utilizer.util.RecipeManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Builder for water submerging recipes
 */
@NullMarked
public class WaterCraft extends OneItemRecipe<WaterCraft> {

	private @Nullable Consumer<Item> action;

	public WaterCraft(ItemStack result, NamespacedKey key) {
		super(result, new NamespacedKey(key.namespace(), key.value() + "_from_submerging"));
	}

	public WaterCraft withAction(@Nullable Consumer<Item> action) {
		this.action = action;
		return builder();
	}

	public void performAction(Item item) {
		ItemStack currentItem = item.getItemStack();
		item.setItemStack(getResult().asQuantity(currentItem.getAmount()));
		item.emitSound(Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, 1F, 1F);

		Block block = item.getLocation().getBlock();
		if (block.getType() == Material.WATER_CAULDRON && block.getBlockData() instanceof Levelled levelled) {
			if (levelled.getLevel() == levelled.getMinimumLevel()) {
				block.setType(Material.CAULDRON);
			} else {
				levelled.setLevel(levelled.getLevel() - 1);
				block.setBlockData(levelled);
			}
		}

		if (this.action != null)
			this.action.accept(item);
	}

	@Override
	public WaterCraft register() {
		RecipeManager.addRecipe(this);
		return this;
	}

}
