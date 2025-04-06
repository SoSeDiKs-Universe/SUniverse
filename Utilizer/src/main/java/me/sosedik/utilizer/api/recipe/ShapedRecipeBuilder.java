package me.sosedik.utilizer.api.recipe;

import com.google.common.base.Preconditions;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class ShapedRecipeBuilder<T extends ShapedRecipeBuilder<T>> extends CraftingRecipeBuilder<T> {

	private final String[] shape;
	private final int matrixSize;
	private final boolean symmetrical;

	protected ShapedRecipeBuilder(ItemStack result, NamespacedKey key, String... shape) {
		super(result, key);
		Preconditions.checkArgument(shape.length != 0, "Shaped recipes can't have an empty shape");

		this.shape = shape;
		int rows = this.shape.length;
		int columns = this.shape[0].length();
		this.matrixSize = rows * columns;
		this.symmetrical = isSymmetrical();
	}

	private boolean isSymmetrical() {
		int columns = this.shape[0].length();
		if (columns == 1) return true;

		int rows = this.shape.length;
		if (columns == 2) {
			if (shape[0].charAt(0) != shape[0].charAt(1)) return false;
			if (rows > 1 && shape[1].charAt(0) != shape[1].charAt(1)) return false;
			return rows <= 2 || shape[2].charAt(0) == shape[2].charAt(1);
		}

		if (columns == 3) {
			if (shape[0].charAt(0) != shape[0].charAt(2)) return false;
			if (rows > 1 && shape[1].charAt(0) != shape[1].charAt(2)) return false;
			return rows <= 2 || shape[2].charAt(0) == shape[2].charAt(2);
		}

		return false;
	}

	/**
	 * Gets the recipe shape
	 *
	 * @return the recipe shape
	 */
	public String[] getShape() {
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
	public boolean checkMatrix(@Nullable ItemStack[] items) {
		if (items.length != this.matrixSize) return false;

		return (!symmetrical && matches(items, true)) || matches(items, false);
	}

	private boolean matches(@Nullable ItemStack[] input, boolean mirrored) {
		int rows = this.shape.length;
		int columns = this.shape[0].length();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				char key;
				if (mirrored) {
					key = this.shape[i].charAt(columns - j - 1);
				} else {
					key = this.shape[i].charAt(j);
				}

				ItemStack item = input[j + i * columns];
				if (!findMatch(key, item))
					return false;
			}
		}

		return true;
	}

}
