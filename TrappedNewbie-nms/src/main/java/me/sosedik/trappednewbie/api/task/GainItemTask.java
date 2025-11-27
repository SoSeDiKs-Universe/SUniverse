package me.sosedik.trappednewbie.api.task;

import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

@NullMarked
public class GainItemTask extends GainItemsTask {

	private final Predicate<ItemStack> checker;

	public GainItemTask(String taskId, Player player, IAdvancement advancement, Material type) {
		this(taskId, player, advancement, null, item -> item.getType() == type, 1);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, @Nullable String criterion, Material type) {
		this(taskId, player, advancement, criterion, item -> item.getType() == type, 1);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, Material type, int required) {
		this(taskId, player, advancement, null, item -> item.getType() == type, required);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, @Nullable String criterion, Material type, int required) {
		this(taskId, player, advancement, criterion, item -> item.getType() == type, required);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, Tag<Material> type) {
		this(taskId, player, advancement, null, item -> type.isTagged(item.getType()), 1);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, @Nullable String criterion, Tag<Material> type) {
		this(taskId, player, advancement, criterion, item -> type.isTagged(item.getType()), 1);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, Tag<Material> type, int required) {
		this(taskId, player, advancement, null, item -> type.isTagged(item.getType()), required);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, @Nullable String criterion, Tag<Material> type, int required) {
		this(taskId, player, advancement, criterion, item -> type.isTagged(item.getType()), required);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, Predicate<ItemStack> checker) {
		this(taskId, player, advancement, null, checker, 1);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, @Nullable String criterion, Predicate<ItemStack> checker) {
		this(taskId, player, advancement, criterion, checker, 1);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, Predicate<ItemStack> checker, int required) {
		this(taskId, player, advancement, null, checker, required);
	}

	public GainItemTask(String taskId, Player player, IAdvancement advancement, @Nullable String criterion, Predicate<ItemStack> checker, int required) {
		super(taskId, player, advancement, criterion, required);
		this.checker = checker;
	}

	@Override
	protected boolean checkItem(ItemStack item) {
		return this.checker.test(item);
	}

}
