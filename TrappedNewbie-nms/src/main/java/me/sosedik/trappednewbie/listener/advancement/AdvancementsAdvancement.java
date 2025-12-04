package me.sosedik.trappednewbie.listener.advancement;

import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.event.TeamMadeAdvancementEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

/**
 * Advancements that track other advancements
 */
@NullMarked
public class AdvancementsAdvancement implements Listener {

	private static final Map<NamespacedKey, Map<IAdvancement, String>> ADVANCEMENTS = new HashMap<>();

	@EventHandler
	public void onAdvancement(TeamMadeAdvancementEvent event) {
		Map<IAdvancement, String> advancements = ADVANCEMENTS.get(event.getAdvancement().getKey());
		if (advancements == null) return;

		advancements.forEach((advancement, criterion) -> event.getTeam().awardCriteria(advancement, criterion));
	}

	public static void addAdvancement(IAdvancement advancement, Map<IAdvancement, String> advancements) {
		ADVANCEMENTS.put(advancement.getKey(), advancements);
	}

}
