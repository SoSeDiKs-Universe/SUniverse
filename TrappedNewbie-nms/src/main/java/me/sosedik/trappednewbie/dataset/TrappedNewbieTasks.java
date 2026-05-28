package me.sosedik.trappednewbie.dataset;

import me.sosedik.trappednewbie.api.task.GainItemTask;
import me.sosedik.trappednewbie.api.task.ObtainAdvancementTask;
import me.sosedik.trappednewbie.api.task.TaskReference;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.trappednewbie.impl.task.tutorial.FirstPossessionTask;
import me.sosedik.trappednewbie.impl.task.tutorial.LimboWorldFallTask;
import me.sosedik.trappednewbie.impl.task.tutorial.MeetWanderingTraderTask;
import me.sosedik.trappednewbie.impl.task.tutorial.OpenAdvancementsTask;
import org.bukkit.Material;
import org.bukkit.Tag;

public class TrappedNewbieTasks {

	public static final TaskReference MEET_WANDERING_TRADER = new TaskReference("meet_wandering_trader", MeetWanderingTraderTask.class);
	public static final TaskReference OPEN_ADVANCEMENTS = new TaskReference("open_advancements", OpenAdvancementsTask.class);
	public static final TaskReference OBTAIN_FRIENDSHIP_LETTER = new TaskReference("obtain_friendship_letter",
		p -> new GainItemTask("obtain_friendship_letter", p, TrappedNewbieAdvancements.REQUIEM_ROOT, "letter",
			item -> item.getType() == TrappedNewbieItems.LETTER && LetterModifier.isUnboundFriendshipLetter(item)
		)
	);
	public static final TaskReference BEFRIEND_WANDERING_TRADER = new TaskReference("befriend_wandering_trader",
		p -> new ObtainAdvancementTask("befriend_wandering_trader", TrappedNewbieAdvancements.REQUIEM_ROOT, "friendship", p)
	);
	public static final TaskReference LIMBO_VOID_FALL = new TaskReference("limbo_void_fall", LimboWorldFallTask.class);
	public static final TaskReference FIRST_POSSESSION = new TaskReference("first_possession", FirstPossessionTask.class);
	public static final TaskReference COLLECT_FIBERS = new TaskReference("collect_fibers",
		p -> new GainItemTask("collect_fibers", p, TrappedNewbieAdvancements.GET_A_FIBER, TrappedNewbieItems.FIBER, 3)
	);
	public static final TaskReference GET_A_BRANCH = new TaskReference("get_a_branch",
		p -> new GainItemTask("get_a_branch", p, TrappedNewbieAdvancements.GET_A_BRANCH, TrappedNewbieTags.ITEMS_BRANCHES)
	);
	public static final TaskReference CRAFT_TWINE = new TaskReference("craft_twine",
		p -> new GainItemTask("craft_twine", p, TrappedNewbieAdvancements.MAKE_A_TWINE, TrappedNewbieItems.TWINE)
	);
	public static final TaskReference FIND_GRAVEL = new TaskReference("find_gravel", p -> new ObtainAdvancementTask("find_gravel", TrappedNewbieAdvancements.FIND_GRAVEL, p));
	public static final TaskReference GET_A_FLINT = new TaskReference("get_a_flint",
		p -> new GainItemTask("get_a_flint", p, TrappedNewbieAdvancements.GET_A_FLINT, Material.FLINT)
	);
	public static final TaskReference GET_A_FLAKED_FLINT = new TaskReference("get_a_flaked_flint",
		p -> new GainItemTask("get_a_flaked_flint", p, TrappedNewbieAdvancements.GET_A_FLAKED_FLINT, TrappedNewbieItems.FLAKED_FLINT)
	);
	public static final TaskReference CRAFT_FLINT_SHEARS = new TaskReference("craft_flint_shears",
		p -> new GainItemTask("craft_flint_shears", p, TrappedNewbieAdvancements.MAKE_FLINT_SHEARS, TrappedNewbieItems.FLINT_SHEARS)
	);
	public static final TaskReference CRAFT_A_FLINT_AXE = new TaskReference("craft_a_flint_axe",
		p -> new GainItemTask("craft_a_flint_axe", p, TrappedNewbieAdvancements.MAKE_A_FLINT_AXE, TrappedNewbieItems.FLINT_AXE)
	);
	public static final TaskReference GET_A_LOG = new TaskReference("get_a_log",
		p -> new GainItemTask("get_a_log", p, TrappedNewbieAdvancements.GET_A_LOG, Tag.LOGS)
	);
	public static final TaskReference CRAFT_A_FLOWER_BOUQUET = new TaskReference("craft_a_flower_bouquet",
		p -> new GainItemTask("craft_a_flower_bouquet", p, TrappedNewbieAdvancements.GET_A_LOG, Tag.LOGS)
	);

	public static final TaskReference[] TUTORIAL_TREE = new TaskReference[]{
		MEET_WANDERING_TRADER, OPEN_ADVANCEMENTS, OBTAIN_FRIENDSHIP_LETTER, BEFRIEND_WANDERING_TRADER, LIMBO_VOID_FALL,
		FIRST_POSSESSION, COLLECT_FIBERS, GET_A_BRANCH, CRAFT_TWINE, FIND_GRAVEL, GET_A_FLINT, GET_A_FLAKED_FLINT,
		CRAFT_FLINT_SHEARS, CRAFT_A_FLINT_AXE, GET_A_LOG, CRAFT_A_FLOWER_BOUQUET
	};

}
