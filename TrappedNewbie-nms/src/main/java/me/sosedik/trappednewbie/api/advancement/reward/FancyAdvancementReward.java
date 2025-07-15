package me.sosedik.trappednewbie.api.advancement.reward;

import com.google.common.base.Preconditions;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.imlp.reward.SimpleAdvancementRewardBuilder;
import me.sosedik.trappednewbie.impl.item.modifier.AdvancementTrophyModifier;
import me.sosedik.trappednewbie.listener.advancement.AdvancementTrophies;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.sosedik.utilizer.api.message.Mini.raw;

@NullMarked
public class FancyAdvancementReward extends SimpleAdvancementRewardBuilder<FancyAdvancementReward> {

	protected @Nullable ItemStack trophyItem;
	protected @Nullable NamespacedKey trophyModel;
	protected @Nullable List<Function<Player, @Nullable Component>> extraMessages;

	public FancyAdvancementReward() {
		super();
		withExtraAction(data -> {
			Player completer = data.completer();
			if (completer != null)
				sendAwardsInfo(completer);
		});
	}

	@Override
	public void onInit(IAdvancement advancement) {
		if (this.trophyItem != null) {
			String trophyId = advancement.getRawKey();
			AdvancementTrophies.addTrophy(trophyId, advancement, this.trophyItem);
			if (this.trophyModel != null)
				AdvancementTrophyModifier.addModelMapping(trophyId, this.trophyModel);
		}
	}

	public FancyAdvancementReward withTrophy(Supplier<ItemStack> item) {
		return withTrophy(item, false);
	}

	public FancyAdvancementReward withTrophy(Supplier<ItemStack> item, boolean noDrop) {
		return withTrophy(item.get(), noDrop);
	}

	public FancyAdvancementReward withTrophy(ItemStack item) {
		return withTrophy(item, false);
	}

	public FancyAdvancementReward withTrophy(ItemStack item, boolean noDrop) {
		Preconditions.checkArgument(this.trophyItem == null, "Can't add more than one trophy!");

		this.trophyItem = item;

		if (noDrop) return this;

		return withExtraAction(data -> {
			Player completer = data.completer();
			if (completer == null) return;

			ItemStack trophyItem = AdvancementTrophies.produceTrophy(data.advancement(), completer);
			if (trophyItem == null) return;

			giveItems(completer, trophyItem);
		});
	}

	public FancyAdvancementReward withTrophyModel(NamespacedKey trophyModel) {
		Preconditions.checkArgument(this.trophyItem != null, "Must have a trophy to apply a model");
		this.trophyModel = trophyModel;
		return this;
	}

	public FancyAdvancementReward withExtraMessage(Function<Player, @Nullable Component> message) {
		if (this.extraMessages == null) this.extraMessages = new ArrayList<>();
		this.extraMessages.add(message);
		return this;
	}

	public FancyAdvancementReward withDeathExp(int exp) {
		return withExtraAction(action -> {
			Player completer = action.completer();
			if (completer == null) return;

			Location loc = completer.getLocation();
			loc.getWorld().spawn(loc, ExperienceOrb.class, orb -> orb.setExperience(exp));
		}).withExtraMessage(player -> FancyAdvancementReward.getExpMessage(player, exp));
	}

	public FancyAdvancementReward withDeathTrophy(Supplier<ItemStack> item) {
		return withTrophy(item, true).withExtraAction(data -> {
			Player completer = data.completer();
			if (completer == null) return;

			Location loc = completer.getLocation();
			ItemStack drop = AdvancementTrophies.produceTrophy(data.advancement(), completer);
			if (drop != null)
				loc.getWorld().dropItemNaturally(loc, drop, i -> i.setInvulnerable(true));
		});
	}

	public @Nullable ItemStack getTrophyItem() {
		return this.trophyItem;
	}

	public @Nullable List<Function<Player, @Nullable Component>> getExtraMessages() {
		return this.extraMessages;
	}

	public void sendAwardsInfo(Player player) {
		sendExpMessage(player, getExp());
		if (this.items != null)
			this.items.forEach(item -> sendItemMessage(player, item, NamedTextColor.GREEN));
		if (this.trophyItem != null)
			sendItemMessage(player, this.trophyItem, NamedTextColor.GOLD);
		if (this.extraMessages != null) {
			this.extraMessages.forEach(m -> {
				Component message = m.apply(player);
				if (message != null)
					player.sendMessage(Component.space().append(message));
			});
		}
	}

	public static void sendExpMessage(Player player, int exp) {
		if (exp != 0)
			player.sendMessage(Component.space().append(getExpMessage(player, exp)));
	}

	public static Component getExpMessage(Player player, int exp) {
		String expMessage = (exp > 0 ? "+" : "") + String.format("%,d", exp).replace(",", ".");
		return Messenger.messenger(player).getMessage("advancement.reward.exp", raw("exp", expMessage), raw("exp_raw", exp));
	}

	public static void sendItemMessage(Player player, ItemStack item, TextColor color) {
		player.sendMessage(getItemMessage(item, color));
		if (item.hasData(DataComponentTypes.BUNDLE_CONTENTS)) {
			BundleContents data = item.getData(DataComponentTypes.BUNDLE_CONTENTS);
			if (data == null) return;

			for (ItemStack storedItem : data.contents())
				player.sendMessage(Component.space().append(getItemMessage(storedItem, NamedTextColor.GREEN)));
		} else if (item.hasData(DataComponentTypes.CONTAINER)) {
			ItemContainerContents data = item.getData(DataComponentTypes.CONTAINER);
			if (data == null) return;

			for (ItemStack storedItem : data.contents())
				player.sendMessage(Component.space().append(getItemMessage(storedItem, NamedTextColor.GREEN)));
		}
	}

	public static Component getItemMessage(ItemStack item, TextColor color) {
		int amount = item.getAmount();
		if (amount > item.getMaxStackSize()) item = item.asOne();
		return Component.text("+" + amount + " ", color).append(item.effectiveName().hoverEvent(item));
	}

}
