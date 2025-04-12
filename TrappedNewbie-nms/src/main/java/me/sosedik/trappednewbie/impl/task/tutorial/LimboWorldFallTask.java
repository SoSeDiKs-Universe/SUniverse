package me.sosedik.trappednewbie.impl.task.tutorial;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.sosedik.packetadvancements.util.ToastMessage;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.task.ObtainAdvancementTask;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LimboWorldFallTask extends ObtainAdvancementTask {

	public LimboWorldFallTask(String taskId, Player player) {
		super(taskId, TrappedNewbieAdvancements.BRAVE_NEW_WORLD, player);
	}

	@EventHandler
	public void onAttack(PrePlayerAttackEntityEvent event) { // TODO real friendship
		if (!(event.getAttacked() instanceof WanderingTrader)) return;

		Player player = event.getPlayer();
		if (player.getWorld() != TrappedNewbie.limboWorld()) return;

		TrappedNewbieAdvancements.BRAVE_NEW_WORLD.awardCriteria(player, "friendship");
		ToastMessage.showToast(player, TrappedNewbieAdvancements.WANDERING_TRADER_HEAD, Messenger.messenger(player).getMessage("limbo.friendship"));
	}

}
