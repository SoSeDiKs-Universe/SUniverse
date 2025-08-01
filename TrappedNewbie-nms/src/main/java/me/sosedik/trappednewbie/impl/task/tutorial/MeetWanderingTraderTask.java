package me.sosedik.trappednewbie.impl.task.tutorial;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.task.ObtainAdvancementTask;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieFonts;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MeetWanderingTraderTask extends ObtainAdvancementTask implements Listener {

	public MeetWanderingTraderTask(String taskId, Player player) {
		super(taskId, TrappedNewbieAdvancements.REQUIEM_ROOT, "interact", player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PrePlayerAttackEntityEvent event) {
		if (!(event.getAttacked() instanceof WanderingTrader)) return;

		Player player = event.getPlayer();
		if (player.getWorld() != TrappedNewbie.limboWorld()) return;
		if (TrappedNewbieAdvancements.REQUIEM_ROOT.hasCriteria(player, "interact")) return;

		event.setCancelled(true);
		TrappedNewbieAdvancements.REQUIEM_ROOT.awardCriteria(true, player, "interact");
		player.sendMessage(Mini.combine(Component.space(), TrappedNewbieFonts.WANDERING_TRADER_HEAD.mapping(), Messenger.messenger(player).getMessage("limbo.welcome.interacted")));
		finish();
	}

}
