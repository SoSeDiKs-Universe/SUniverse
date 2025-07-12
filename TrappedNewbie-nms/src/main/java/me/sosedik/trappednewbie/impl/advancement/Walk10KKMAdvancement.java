package me.sosedik.trappednewbie.impl.advancement;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancementBuilderImpl;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.listener.advancement.AdvancementTrophies;
import me.sosedik.utilizer.impl.recipe.ShapelessCraft;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;
import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

@NullMarked
public class Walk10KKMAdvancement extends BaseAdvancement {

	public Walk10KKMAdvancement(BaseAdvancementBuilderImpl advancementBuilder) {
		super(advancementBuilder);
	}

	@Override
	public void onRegister() {
		super.onRegister();

		new ShapelessCraft(requireNonNull(AdvancementTrophies.getBaseTrophy(this)), trappedNewbieKey("trophy/" + TrappedNewbieAdvancements.WALK_250KM.getRawKey()))
			.special()
			.addIngredients(MaterialTags.BOOTS.getValues())
			.addIngredients(MaterialTags.BOOTS.getValues())
			.withPreCheck(event -> {
				Player player = event.getPlayer();
				if (player == null) {
					event.setResult(null);
					return;
				}

				ItemStack trophyBoots = null;
				ItemStack bootsToCopy = null;
				for (ItemStack matrixItem : event.getMatrix()) {
					if (ItemStack.isEmpty(matrixItem)) continue;

					if (trophyBoots == null && AdvancementTrophies.isUsableTrophy(TrappedNewbieAdvancements.WALK_250KM, player, matrixItem))
						trophyBoots = matrixItem;
					else
						bootsToCopy = matrixItem;
				}

				if (trophyBoots == null || bootsToCopy == null) {
					event.setResult(null);
					return;
				}

				ItemStack result = trophyBoots.withType(bootsToCopy.getType());
				if (bootsToCopy.hasData(DataComponentTypes.TRIM))
					result.setData(DataComponentTypes.TRIM, requireNonNull(bootsToCopy.getData(DataComponentTypes.TRIM)));
				else
					result.resetData(DataComponentTypes.TRIM);
				if (bootsToCopy.hasData(DataComponentTypes.DYED_COLOR))
					result.setData(DataComponentTypes.DYED_COLOR, requireNonNull(bootsToCopy.getData(DataComponentTypes.DYED_COLOR)));
				else
					result.resetData(DataComponentTypes.DYED_COLOR);

				if (result.isSimilar(trophyBoots)) {
					event.setResult(null);
					return;
				}

				event.setResult(result);
			})
			.withLeftoverCheck(event -> {
				ItemStack item = event.getItem();
				event.setResult(AdvancementTrophies.isTrophy(item) ? ItemStack.empty() : item);
				event.lockResult();
			})
			.register();
	}

}
