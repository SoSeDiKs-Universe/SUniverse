package me.sosedik.utilizer.api.recipe;

import me.sosedik.utilizer.api.event.recipe.ItemCraftEvent;
import me.sosedik.utilizer.api.event.recipe.ItemCraftPrepareEvent;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.listener.misc.CustomRecipeLeftovers;
import me.sosedik.utilizer.listener.misc.ExtraRecipeHandlers;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Allows adding extra recipe checks
 *
 * @param <T> recipe type
 */
@SuppressWarnings("unchecked")
public interface ExtraRecipeChecks<T> extends CustomRecipe {

	/**
	 * Adds a prepare item craft event check
	 *
	 * @param check check
	 */
	default @NotNull T withPreCheck(@NotNull Consumer<@NotNull ItemCraftPrepareEvent> check) {
		ExtraRecipeHandlers.addPreCraftCheck(getKey(), check);
		return (T) this;
	}

	/**
	 * Adds a leftover item check
	 *
	 * @param check check
	 */
	default @NotNull T withLeftoverCheck(@NotNull Consumer<@NotNull RemainingItemEvent> check) {
		ExtraRecipeHandlers.addLeftoverCheck(getKey(), check);
		return (T) this;
	}

	/**
	 * Adds a post-craft check
	 *
	 * @param check check
	 */
	default @NotNull T withCraftCheck(@NotNull Consumer<@NotNull ItemCraftEvent> check) {
		ExtraRecipeHandlers.addExtraCheck(getKey(), check);
		return (T) this;
	}

	/**
	 * Adds an exemption leftover rule for all ingredients
	 */
	default @NotNull T withExemptLeftovers() {
		return withExemptLeftovers(null);
	}

	/**
	 * Adds an exemption leftover rule
	 *
	 * @param check checks whether the item should be exempt from leaving a leftover
	 */
	default @NotNull T withExemptLeftovers(@Nullable BiPredicate<@NotNull RemainingItemEvent, @NotNull ItemStack> check) {
		CustomRecipeLeftovers.addExemptRule(getKey(), check);
		return (T) this;
	}

}
