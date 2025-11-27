package me.sosedik.trappednewbie.api.task;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.sosedik.utilizer.api.message.Mini.component;
import static me.sosedik.utilizer.api.message.Mini.raw;

@NullMarked
public abstract class GainItemsTask extends Task implements Listener {

	private final IAdvancement advancement;
	private final @Nullable String criterion;
	private final int required;
	private int collected = 0;

	protected GainItemsTask(String taskId, Player player, IAdvancement advancement) {
		this(taskId, player, advancement, null, 1);
	}

	protected GainItemsTask(String taskId, Player player, IAdvancement advancement, @Nullable String criterion) {
		this(taskId, player, advancement, criterion, 1);
	}

	protected GainItemsTask(String taskId, Player player, IAdvancement advancement, int required) {
		this(taskId, player, advancement, null, required);
	}

	protected GainItemsTask(String taskId, Player player, IAdvancement advancement, @Nullable String criterion, int required) {
		super(taskId, player);
		this.advancement = advancement;
		this.criterion = criterion;
		this.required = required;
	}

	@Override
	public boolean canBeSkipped() {
		return this.criterion == null ? this.advancement.isDone(getPlayer()) : this.advancement.hasCriteria(getPlayer(), this.criterion);
	}

	@Override
	public void finish() {
		super.finish();
		if (this.criterion != null)
			this.advancement.awardCriteria(getPlayer(), this.criterion);
	}

	@Override
	public @Nullable Component @Nullable [] getDisplay() {
		if (this.required == 1) return super.getDisplay();

		var messenger = Messenger.messenger(getPlayer());
		return messenger.getMessages("task." + getTaskId(),
				component("collectable", messenger.getMessage("task.collectable",
					raw("collected", this.collected),
					raw("required", this.required))
				)
			);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSkip(PlayerAdvancementDoneEvent event) {
		if (event.getPlayer() != getPlayer()) return;
		if (event.getAdvancement() != this.advancement) return;

		finish();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemEnquire(PlayerInventorySlotChangeEvent event) {
		Player player = event.getPlayer();
		if (player != getPlayer()) return;

		if (this.required == 1) {
			if (checkItem(event.getNewItemStack()))
				finish();
			return;
		}

		TrappedNewbie.scheduler().sync(this::checkItems, 1L);
	}

	private void checkItems() {
		this.collected = calcItems();
		if (this.collected == this.required)
			finish();
	}

	private int calcItems() {
		int found = 0;
		for (ItemStack item : getPlayer().getInventory().getStorageContents()) {
			if (item == null) continue;

			if (checkItem(item))
				found += item.getAmount();

			if (found >= this.required)
				return this.required;
		}

		ItemStack item = getPlayer().getInventory().getItemInOffHand();
		if (checkItem(item))
			found += item.getAmount();

		// Vanilla does not check cursor item for advancements, so neither do we in here

		return Math.min(found, this.required);
	}

	protected abstract boolean checkItem(ItemStack item);

}
