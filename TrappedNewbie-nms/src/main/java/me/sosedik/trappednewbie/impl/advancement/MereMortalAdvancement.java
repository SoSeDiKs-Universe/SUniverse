package me.sosedik.trappednewbie.impl.advancement;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import me.sosedik.packetadvancements.api.advancement.base.BaseAdvancementBuilder;
import me.sosedik.packetadvancements.api.display.IAdvancementDisplay;
import me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MereMortalAdvancement extends BaseAdvancement {

	public MereMortalAdvancement(BaseAdvancementBuilder advancementBuilder) {
		super(advancementBuilder);
	}

	@Override
	public IAdvancementDisplay getDisplay(Player player) {
		var headItem = new ItemStack(Material.PLAYER_HEAD);
		headItem.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
		return getDisplay().clone().icon(headItem);
	}

}
