package me.sosedik.trappednewbie.dataset;

import de.tr7zw.nbtapi.NBT;
import me.sosedik.miscme.dataset.MoreMobHeads;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.advancement.root.IRootAdvancement;
import me.sosedik.packetadvancements.api.tab.AdvancementManager;
import me.sosedik.packetadvancements.api.tab.AdvancementTab;
import me.sosedik.packetadvancements.imlp.display.FancyAdvancementDisplay;
import me.sosedik.packetadvancements.util.storage.JsonStorage;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.impl.advancement.MereMortalAdvancement;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.alwaysDone;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.requirements;
import static me.sosedik.packetadvancements.api.tab.AdvancementTab.buildTab;
import static me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement.buildBase;
import static me.sosedik.packetadvancements.imlp.advancement.root.RootAdvancement.buildRoot;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.hidden;

@NullMarked
public class TrappedNewbieAdvancements {

	public static final ItemStack WANDERING_TRADER_HEAD = ItemUtil.texturedHead(MoreMobHeads.WANDERING_TRADER);
	public static final ItemStack ZOMBIE_VILLAGER_HEAD = ItemUtil.texturedHead(MoreMobHeads.ZOMBIE_VILLAGER);

	public static final AdvancementManager MANAGER = new AdvancementManager(new JsonStorage(TrappedNewbie.instance()));

	public static final AdvancementTab REQUIEM_TAB = buildTab("requiem", MANAGER).inverseY().backgroundPathBlock(Material.SOUL_SAND).icon(Material.SKELETON_SKULL).build();
	public static final IRootAdvancement REQUIEM_ROOT = buildRoot(REQUIEM_TAB).display(display().icon(Material.SUNFLOWER)).requiredProgress(requirements("interact", "open")).visibilityRule(hidden()).build();
	public static final IAdvancement OPENING_HOLDER = buildBase(REQUIEM_ROOT, "holder").display(display().x(-1.25F).icon(WANDERING_TRADER_HEAD)).requiredProgress(alwaysDone()).build();
	public static final IAdvancement BRAVE_NEW_WORLD = buildBase(REQUIEM_ROOT, "brave_new_world").display(display().x(1F).icon(braveNewWorldItem())).requiredProgress(requirements("friendship", "fall")).build();
	public static final IAdvancement FIRST_POSSESSION = buildBase(BRAVE_NEW_WORLD, "first_possession").display(display().x(1.25F).icon(RequiemItems.HOST_REVOCATOR)).build();
	public static final IAdvancement GOOD_AS_NEW = buildBase(FIRST_POSSESSION, "good_as_new").display(display().x(1F).icon(ZOMBIE_VILLAGER_HEAD)).build();
	public static final IAdvancement MERE_MORTAL = new MereMortalAdvancement(buildBase(GOOD_AS_NEW, "mere_mortal").display(display().x(1F).icon(Material.PLAYER_HEAD)).visibilityRule(hidden())); // TODO
	public static final IAdvancement I_HATE_SAND = buildBase(FIRST_POSSESSION, "i_hate_sand").display(display().xy(1F, 1F).icon(Material.SAND).goalFrame()).build();
	public static final IAdvancement KUNG_FU_PANDA = buildBase(FIRST_POSSESSION, "kung_fu_panda").display(display().xy(1F, -1F).icon(Material.BAMBOO).goalFrame()).build();

	private static ItemStack braveNewWorldItem() {
		var item = new ItemStack(TrappedNewbieItems.LETTER);
		NBT.modify(item, nbt -> {
			nbt.setItemStackArray(LetterModifier.CONTENTS_TAG, new ItemStack[]{WANDERING_TRADER_HEAD});
			nbt.setEnum(LetterModifier.TYPE_TAG, LetterModifier.LetterType.STAR);
		});
		return item;
	}

	private static FancyAdvancementDisplay.FancyAdvancementDisplayImpl display() {
		return FancyAdvancementDisplay.fancyDisplay().noAnnounceChat().fancyDescriptionParent(NamedTextColor.GRAY);
	}

	public static void setupAdvancements() {
		MANAGER.registerTabs(
			REQUIEM_TAB
		);

		REQUIEM_TAB.registerAdvancements(
			REQUIEM_ROOT, OPENING_HOLDER, BRAVE_NEW_WORLD, FIRST_POSSESSION, GOOD_AS_NEW, MERE_MORTAL, I_HATE_SAND, KUNG_FU_PANDA
		);
	}

}
