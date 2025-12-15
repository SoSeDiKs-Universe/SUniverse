package me.sosedik.trappednewbie.misc;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.registry.tag.TagKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.utilizer.util.MiscUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import org.bukkit.Bukkit;
import org.bukkit.FeatureFlag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class VillagerTradesHack {

	public static void addTrades() {
		addVillagerTrades(Villager.Profession.FARMER, 3,
			tradeBookForEmeralds(5, TrappedNewbieTags.TRADES_FARMER)
		);
	}

	private static VillagerTrades.EnchantBookForEmeralds tradeBookForEmeralds(int exp, TagKey<Enchantment> tag) {
		return new VillagerTrades.EnchantBookForEmeralds(exp, net.minecraft.tags.TagKey.create(Registries.ENCHANTMENT, PaperAdventure.asVanilla(tag.key())));
	}

	private static void addVillagerTrades(Villager.Profession profession, int level, VillagerTrades.ItemListing... newListings) {
		ResourceKey<VillagerProfession> resourceKey = ResourceKey.create(Registries.VILLAGER_PROFESSION, PaperAdventure.asVanilla(profession.key()));
		Int2ObjectMap<VillagerTrades.ItemListing[]> tradesMap;
		if (Bukkit.getWorlds().getFirst().getFeatureFlags().contains(FeatureFlag.TRADE_REBALANCE)) {
			Int2ObjectMap<VillagerTrades.ItemListing[]> map = VillagerTrades.EXPERIMENTAL_TRADES.get(resourceKey);
			tradesMap = map == null ? VillagerTrades.TRADES.get(resourceKey) : map;
		} else {
			tradesMap = VillagerTrades.TRADES.get(resourceKey);
		}
		VillagerTrades.ItemListing[] itemListings = tradesMap.get(level);
		itemListings = MiscUtil.combineArrays(itemListings, newListings);
		tradesMap.put(level, itemListings);
	}

}
