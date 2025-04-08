package me.sosedik.trappednewbie.dataset;

import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.advancement.root.IRootAdvancement;
import me.sosedik.packetadvancements.api.tab.AdvancementManager;
import me.sosedik.packetadvancements.api.tab.AdvancementTab;
import me.sosedik.packetadvancements.imlp.display.FancyAdvancementDisplay;
import me.sosedik.packetadvancements.util.storage.JsonStorage;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Material;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.alwaysDone;
import static me.sosedik.packetadvancements.api.tab.AdvancementTab.buildTab;
import static me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement.buildBase;
import static me.sosedik.packetadvancements.imlp.advancement.root.RootAdvancement.buildRoot;

@NullMarked
public class TrappedNewbieAdvancements {

	public static final AdvancementManager MANAGER = new AdvancementManager(new JsonStorage(TrappedNewbie.instance()));

	public static final AdvancementTab REQUIEM_TAB = buildTab("requiem", MANAGER).inverseY().backgroundBlock(Material.SOUL_SAND).icon(Material.SKELETON_SKULL).build();
	public static final IRootAdvancement REQUIEM_ROOT = buildRoot(REQUIEM_TAB).display(display().icon(Material.SUNFLOWER)).build();
	public static final IAdvancement OPENING_HOLDER = buildBase(REQUIEM_ROOT, "holder").display(display().x(-1.25F).icon(Material.BONE)).requiredProgress(alwaysDone()).build();

	public static FancyAdvancementDisplay.FancyAdvancementDisplayImpl display() {
		return FancyAdvancementDisplay.fancyDisplay().noAnnounceChat();
	}

	public static void setupAdvancements() {
		MANAGER.registerTabs(
			REQUIEM_TAB
		);

		REQUIEM_TAB.registerAdvancements(
			REQUIEM_ROOT, OPENING_HOLDER
		);
	}

}
