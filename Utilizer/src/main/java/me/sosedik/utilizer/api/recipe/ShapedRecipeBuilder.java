package me.sosedik.utilizer.api.recipe;

import com.google.common.base.Preconditions;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ShapedRecipeBuilder<T extends ShapedRecipeBuilder<T>> extends CraftingRecipeBuilder<T> {

	private final String[] shape;
	private final int matrixSize;
	private final boolean symmetrical;

	protected ShapedRecipeBuilder(@NotNull ItemStack result, @NotNull NamespacedKey key, @NotNull String... shape) {
		super(result, key);
		Preconditions.checkArgument(shape.length != 0, "Shaped recipes can't have an empty shape");

		this.shape = shape;
		int columns = this.shape.length;
		int rows = this.shape[0].length();
		this.matrixSize = columns * rows;
		this.symmetrical = isSymmetrical();
	}

	private boolean isSymmetrical() { // TODO proper symmetrical check
		return this.shape[0].length() == 1;
	}

	/**
	 * Gets the recipe shape
	 *
	 * @return the recipe shape
	 */
	public @NotNull String[] getShape() {
		return this.shape;
	}

	/**
	 * Gets the matrix size (the minimum amount of items needed for {@link #checkMatrix(ItemStack[])})
	 *
	 * @return the matrix size
	 */
	public int getMatrixSize() {
		return this.matrixSize;
	}

	@Override
	public boolean checkMatrix(@Nullable ItemStack @NotNull [] items) {
		if (items.length != this.matrixSize) return false;

		return (!symmetrical && matches(items, true)) || matches(items, false);
	}

	private boolean matches(@Nullable ItemStack @NotNull [] input, boolean mirrored) {
		int columns = this.shape.length;
		int rows = this.shape[0].length();
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				char key;
				if (mirrored) {
					key = this.shape[i].charAt(rows - j - 1);
				} else {
					key = this.shape[i].charAt(j);
				}

				ItemStack item = input[j + i * rows];
				if (!findMatch(key, item))
					return false;
			}
		}

		return true;
	}

}
