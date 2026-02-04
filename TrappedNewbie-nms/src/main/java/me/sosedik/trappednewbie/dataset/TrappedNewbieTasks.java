package me.sosedik.trappednewbie.dataset;

import me.sosedik.trappednewbie.api.task.GainItemTask;
import me.sosedik.trappednewbie.api.task.ObtainAdvancementTask;
import me.sosedik.trappednewbie.api.task.TaskReference;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.trappednewbie.impl.task.tutorial.FirstPossessionTask;
import me.sosedik.trappednewbie.impl.task.tutorial.LimboWorldFallTask;
import me.sosedik.trappednewbie.impl.task.tutorial.MeetWanderingTraderTask;
import me.sosedik.trappednewbie.impl.task.tutorial.OpenAdvancementsTask;

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
	public static final TaskReference CRAFT_TWINE = new TaskReference("craft_twine",
		p -> new GainItemTask("craft_twine", p, TrappedNewbieAdvancements.GET_A_FIBER, TrappedNewbieItems.FIBER, 3)
	);
	public static final TaskReference FIND_GRAVEL = new TaskReference("find_gravel", p -> new ObtainAdvancementTask("find_gravel", TrappedNewbieAdvancements.FIND_GRAVEL, p));

	public static final TaskReference[] TUTORIAL_TREE = new TaskReference[]{
		MEET_WANDERING_TRADER, OPEN_ADVANCEMENTS, OBTAIN_FRIENDSHIP_LETTER, BEFRIEND_WANDERING_TRADER, LIMBO_VOID_FALL,
		FIRST_POSSESSION, COLLECT_FIBERS, CRAFT_TWINE, FIND_GRAVEL
	};

}
