package me.sosedik.trappednewbie.listener.player;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.event.player.PlayerTargetBlockEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.impl.blockstorage.TotemBaseBlockStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.listener.BlockStorage;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MiscUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

@NullMarked
public class TotemRituals implements Listener {

	private static final Map<Material, RitualInstrument> RITUAL_INSTRUMENTS = new HashMap<>();
	private static final NamespacedKey RITUAL_PROVIDER_KEY = TrappedNewbie.trappedNewbieKey("ritual");
	private static final Map<UUID, RitualData> ACTIVE_RITUALS = new HashMap<>();

	@EventHandler
	public void onJoin(PlayerClientLoadedWorldEvent event) {
		Player player = event.getPlayer();
		TaskManagement.bossBar(player).addProvider(RITUAL_PROVIDER_KEY, () -> {
			RitualData ritualData = ACTIVE_RITUALS.get(player.getUniqueId());
			if (ritualData == null) return null;

			var messenger = Messenger.messenger(player);
			Component title = messenger.getMessage("task.ritual." + ritualData.getRitual().getLocaleId());

			return new Component[]{title, ritualData.getProgressText(), ritualData.getTimerText()};
		});
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		abortActiveRitual(event.getPlayer());
	}

	@EventHandler
	public void onUse(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;
		if (event.useItemInHand() == Event.Result.DENY) return;

		ItemStack item = event.getItem();
		if (item == null) return;

		Player player = event.getPlayer();
		if (player.getInventory().getItemInMainHand().getType() != TrappedNewbieItems.TOTEMIC_STAFF) return;
		if (abortActiveRitual(player) == null) return;

		player.swingMainHand();
	}

	public static void playedInstrument(Player player, Material type, Location loc) {
		loc.getWorld().spawnParticle(Particle.NOTE, loc, 3, 0.5, 0.5, 0.5);

		RitualData ritualData = ACTIVE_RITUALS.get(player.getUniqueId());
		if (ritualData == null) {
			for (BlockState blockState : LocationUtil.findTileEntitiesNearby(loc, 8, block -> TrappedNewbieTags.TOTEM_BASES.isTagged(block.getType())).toList()) {
				if (!(BlockStorage.getByLoc(blockState.getLocation()) instanceof TotemBaseBlockStorage storage)) continue;

				ritualData = storage.getRitualData();
				if (ritualData != null) break;
			}
			if (ritualData == null) return;

			pickRitual(player, ritualData);
		}

		ritualData.playedInstrument(player, type);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTarget(PlayerTargetBlockEvent event) {
		Block block = event.getBlock();
		if (!TrappedNewbieTags.TOTEM_BASES.isTagged(block.getType())) return;

		Player player = event.getPlayer();
		if (player.getInventory().getItemInMainHand().getType() != TrappedNewbieItems.TOTEMIC_STAFF) return;

		event.setCancelled(true);
	}

	public static @Nullable RitualData abortActiveRitual(Player player) {
		return abortActiveRitual(player.getUniqueId());
	}

	public static @Nullable RitualData abortActiveRitual(UUID uuid) {
		RitualData ritualData = ACTIVE_RITUALS.remove(uuid);
		if (ritualData != null) {
			ritualData.getPerformers().remove(uuid);
			if (ritualData.getPerformers().isEmpty())
				ritualData.getTotem().stopRitual();
		}
		return ritualData;
	}

	public static void pickRitual(Player player, RitualData ritualData) {
		ritualData.addPerformer(player);
		ACTIVE_RITUALS.put(player.getUniqueId(), ritualData);
	}

	public static class RitualData {

		private static final int TOTAL_BARS = 120;

		private final Set<UUID> performers = new HashSet<>();
		private final TotemBaseBlockStorage totem;
		private final Ritual ritual;
		private final Map<RitualInstrument, Integer> progress = new HashMap<>();
		private int extraPoints = 0;
		private int timer = 0;
		private final List<Component> bars = new ArrayList<>();
		private Component progressText;
		private Component timerText;

		public RitualData(Ritual ritual, TotemBaseBlockStorage totem) {
			this.ritual = ritual;
			this.totem = totem;
			this.progressText = progressText();
			this.timerText = timerText();

			TrappedNewbie.scheduler().sync(task -> {
				if (this.performers.isEmpty()) return true;

				if (isSuccessful()) {
					this.totem.successRitual();
					abort();
					return true;
				}

				this.timer++;
				this.timerText = timerText();
				if (this.timer > this.ritual.timeToPerform) {
					this.totem.failRitual();
					abort();
					return true;
				}

				return false;
			}, 20L, 20L);
		}

		private Component progressText() {
			double points = calculatePoints();
			int totalPoints = this.ritual.getTotalPoints();
			int filledBars = (int) ((points * TOTAL_BARS) / totalPoints);

			this.bars.clear();
			for (int i = 0; i < filledBars; i++) this.bars.add(Component.text("|", NamedTextColor.AQUA));
			for (int i = 0; i < TOTAL_BARS - filledBars; i++) this.bars.add(Component.text("|", NamedTextColor.DARK_GREEN));
			Component text = combine(SpacingUtil.getNegativePixel(), this.bars);
			return Component.text("🎶 ").append(text);
		}

		private Component timerText() {
			double time = this.timer;
			int totalTime = this.ritual.getTimeToPerform();
			int filledBars = (int) ((time * TOTAL_BARS) / totalTime);

			this.bars.clear();
			for (int i = 0; i < filledBars; i++) this.bars.add(Component.text("|", NamedTextColor.DARK_PURPLE));
			for (int i = 0; i < TOTAL_BARS - filledBars; i++) this.bars.add(Component.text("|", NamedTextColor.DARK_GRAY));
			Component text = combine(SpacingUtil.getNegativePixel(), this.bars);
			return Component.text("⏳ ").append(text);
		}

		private void abort() {
			this.performers.forEach(ACTIVE_RITUALS::remove);
			this.performers.clear();
		}

		public Set<UUID> getPerformers() {
			return this.performers;
		}

		public void addPerformer(Player player) {
			this.performers.add(player.getUniqueId());
		}

		public TotemBaseBlockStorage getTotem() {
			return this.totem;
		}

		public Ritual getRitual() {
			return this.ritual;
		}

		public Component getProgressText() {
			return this.progressText;
		}

		public Component getTimerText() {
			return this.timerText;
		}

		public void playedInstrument(Player player, Material item) {
			RitualInstrument ritualInstrument = RitualInstrument.of(item);
			if (ritualInstrument == null) return;

			int req = this.ritual.getRequirement(ritualInstrument);
			if (req > 0) {
				int points = this.progress.getOrDefault(ritualInstrument, 0) + 1;
				this.progress.put(ritualInstrument, points);
				boolean reachedLimit = points >= req;
				this.totem.spawnMusic(reachedLimit);
			} else {
				this.extraPoints += ritualInstrument.getExtraPoints();
				boolean reachedLimit = this.extraPoints >= this.ritual.getRequiredExtraPoints();
				this.totem.spawnMusic(reachedLimit);
			}
			this.progressText = progressText();
		}

		public int calculatePoints() {
			int points = Math.min(this.extraPoints, this.ritual.extraPoints);
			for (Map.Entry<RitualInstrument, Integer> entry : this.progress.entrySet())
				points += Math.min(entry.getValue(), this.ritual.getRequirement(entry.getKey())) * entry.getKey().getExtraPoints();
			return Math.min(points, this.ritual.getTotalPoints());
		}

		public boolean isSuccessful() {
			if (this.extraPoints < this.ritual.extraPoints) return false;

			for (Map.Entry<RitualInstrument, Integer> entry : this.ritual.getRequirements().entrySet()) {
				if (this.progress.getOrDefault(entry.getKey(), 0) < entry.getValue())
					return false;
			}

			return true;
		}

	}

	public enum Ritual {

		RAIN_DANCE(26, 40,
			List.of(
				RitualSacrifice.of(
					player -> List.of(combined(
						ResourceLib.getItemIcon(Material.SALMON.key()),
						Component.space(),
						Messenger.messenger(player).getMessage("task.ritual.rain_dance.requirement")
					)),
					MiscUtil.combineToList(MaterialTags.RAW_FISH.getValues(), MaterialTags.FISH_BUCKETS.getValues())
				)
			),
			Map.entry(RitualInstrument.DRUM, 10), Map.entry(RitualInstrument.RATTLE, 5)
		),
		SOUL_MELANCHOLY(30, 50,
			List.of(),
			Map.entry(RitualInstrument.FLUTE, 6)
		);

		private final String localeId;
		private final List<RitualSacrifice> sacrifices;
		private final Map<RitualInstrument, Integer> requirements;
		private final int extraPoints;
		private final int timeToPerform;
		private final int totalPoints;

		@SafeVarargs
		Ritual(int timeToPerform, int extraPoints, List<RitualSacrifice> sacrifices, Map.Entry<RitualInstrument, Integer>... requirements) {
			this.localeId = name().toLowerCase(Locale.US);
			this.sacrifices = sacrifices;
			this.requirements = Map.ofEntries(requirements);
			this.extraPoints = extraPoints;
			this.timeToPerform = timeToPerform;
			
			int totalPoints = extraPoints;
			for (Map.Entry<RitualInstrument, Integer> requirement : requirements)
				totalPoints += requirement.getValue() * requirement.getKey().getExtraPoints();
			this.totalPoints = totalPoints;
		}

		public String getLocaleId() {
			return this.localeId;
		}

		public List<RitualSacrifice> getSacrifices() {
			return this.sacrifices;
		}

		public Map<RitualInstrument, Integer> getRequirements() {
			return this.requirements;
		}

		public int getRequirement(RitualInstrument instrument) {
			return this.requirements.getOrDefault(instrument, 0);
		}

		public int getRequiredExtraPoints() {
			return this.extraPoints;
		}

		public int getTimeToPerform() {
			return this.timeToPerform;
		}

		public int getTotalPoints() {
			return this.totalPoints;
		}

	}

	public record RitualSacrifice(Predicate<ItemStack> itemCheck, Function<Player, List<Component>> messageProvider) {

		public static RitualSacrifice of(Function<Player, List<Component>> messageProvider, Material... items) {
			return of(messageProvider, List.of(items));
		}

		public static RitualSacrifice of(Function<Player, List<Component>> messageProvider, Collection<Material> items) {
			Predicate<ItemStack> itemCheck = item -> items.contains(item.getType());
			return new RitualSacrifice(itemCheck, messageProvider);
		}

	}

	public enum RitualInstrument {

		RATTLE(TrappedNewbieItems.RATTLE, 8),
		FLUTE(TrappedNewbieItems.FLUTE, 10),
		DRUM(TrappedNewbieTags.DRUMS, 2),
		TRUMPET(TrappedNewbieItems.TRUMPET, 20);

		private final String localeId;
		private final Set<Material> items = new HashSet<>();
		private final int extraPoints;

		RitualInstrument(Material item, int extraPoints) {
			this.localeId = "ritual.instruments." + name().toLowerCase(Locale.US);
			this.items.add(item);
			this.extraPoints = extraPoints;
			RITUAL_INSTRUMENTS.put(item, this);
		}

		RitualInstrument(Tag<Material> items, int extraPoints) {
			this.localeId = "ritual.instruments." + name().toLowerCase(Locale.US);
			this.items.addAll(items.getValues());
			this.extraPoints = extraPoints;
			this.items.forEach(item -> RITUAL_INSTRUMENTS.put(item, this));
		}

		public String getLocaleId() {
			return this.localeId;
		}

		public int getExtraPoints() {
			return this.extraPoints;
		}

		public boolean is(Material item) {
			return this.items.contains(item);
		}

		public static @Nullable RitualInstrument of(Material type) {
			return RITUAL_INSTRUMENTS.get(type);
		}

	}

}
