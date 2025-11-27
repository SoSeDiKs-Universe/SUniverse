package me.sosedik.trappednewbie.impl.blockstorage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.requiem.listener.player.possessed.PossessedUndeadZombieCuring;
import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.listener.player.TotemRituals;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.InventoryUtil;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

@NullMarked
public class TotemBaseBlockStorage extends DisplayBlockStorage {

	private final Set<UUID> ritualPickers = new HashSet<>();
	private TotemRituals.@Nullable RitualData ritualData;

	public TotemBaseBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
	}

	@Override
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (player.getInventory().getItemInMainHand().getType() != TrappedNewbieItems.TOTEMIC_STAFF) return;

		event.setCancelled(true);
		player.swingMainHand();

		if (this.ritualData == null) {
			pickRitual(player);
			return;
		}

		abortRitual();
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		abortRitual();
		abortPickers();
	}

	public TotemRituals.@Nullable RitualData getRitualData() {
		return this.ritualData;
	}

	public void pickRitual(Player player) {
		this.ritualPickers.add(player.getUniqueId());
		TotemRituals.abortActiveRitual(player);

		var messenger = Messenger.messenger(player);

		List<Component> texts = new ArrayList<>(TotemRituals.Ritual.values().length + 1);

		texts.add(messenger.getMessage("ritual.picker"));

		for (TotemRituals.Ritual ritual : TotemRituals.Ritual.values()) {
			Component ritualText = messenger.getMessage("task.ritual." + ritual.getLocaleId());
			Component hoverText = messenger.getMessage("task.ritual." + ritual.getLocaleId() + ".description");

			if (!ritual.getSacrifices().isEmpty()) {
				Component requirementsText = messenger.getMessage("ritual.required_items");
				List<Component> sacrificesTexts = new ArrayList<>();
				ritual.getSacrifices().forEach(sacrifice -> sacrificesTexts.add(Component.text("- ").append(sacrifice.messageProvider().apply(player))));
				hoverText = combined(hoverText, Component.newline(), Component.newline(), requirementsText, Component.newline(), combine(Component.newline(), sacrificesTexts));
			}

			if (!ritual.getRequirements().isEmpty()) {
				Component requirementsText = messenger.getMessage("ritual.required_instruments");
				List<Component> instrumentNames = new ArrayList<>();
				ritual.getRequirements().keySet().forEach(instrument -> instrumentNames.add(Component.text("- ").append(messenger.getMessage(instrument.getLocaleId()))));
				if (ritual.getRequiredExtraPoints() > 0) instrumentNames.add(Component.text("- ").append(messenger.getMessage("ritual.instruments.any")));
				hoverText = combined(hoverText, Component.newline(), Component.newline(), requirementsText, Component.newline(), combine(Component.newline(), instrumentNames));
			}

			ritualText = ritualText.hoverEvent(hoverText);
			ritualText = ritualText.clickEvent(ClickEvent.callback(audience -> {
				if (audience instanceof Player p)
					p.closeInventory();
				onRitualPick(player, ritual);
			}, ClickCallback.Options.builder().uses(1).lifetime(Duration.ofMinutes(10L)).build()));
			ritualText = Component.textOfChildren(Component.newline(), Component.text("・"), ritualText);
			texts.add(ritualText);
		}

		Book.Builder builder = Book.builder().title(Component.empty()).author(Component.empty())
			.addPage(combine(Component.newline(), texts));
		player.openBook(builder);
	}

	public void onRitualPick(Player player, TotemRituals.Ritual ritual) {
		if (this.ritualData != null)
			abortRitual();

		boolean sacrificed = false;
		List<TotemRituals.RitualSacrifice> sacrifices = ritual.getSacrifices();
		s:
		for (TotemRituals.RitualSacrifice sacrifice : sacrifices) {
			for (ItemStack item : player.getInventory()) {
				if (ItemStack.isEmpty(item)) continue;
				if (!sacrifice.itemCheck().test(item)) continue;

				var itemEvent = new RemainingItemEvent(null, player, null, TrappedNewbie.trappedNewbieKey("ritual_sacrifice"), item.asOne(), 1, true);
				Material craftingRemainingItem = item.getType().getCraftingRemainingItem();
				if (craftingRemainingItem != null)
					itemEvent.setResult(ItemStack.of(craftingRemainingItem));
				itemEvent.callEvent();

				item.subtract();

				ItemStack result = itemEvent.getResult();
				if (!ItemStack.isEmpty(result))
					InventoryUtil.replaceOrAdd(player, EquipmentSlot.HAND, result);

				sacrificed = true;
				break s;
			}
		}
		if (!sacrificed) {
			this.ritualData = null;
			return;
		}

		this.ritualData = new TotemRituals.RitualData(ritual, this);

		abortPickers();
	}
	
	public void spawnMusic(boolean saturated) {
		Location loc = getBlock().getLocation().center();
		loc.getWorld().spawnParticle(Particle.NOTE, loc, saturated ? 2 : 6, 0.5, 0.5, 0.5);
		if (saturated)
			loc.getWorld().spawnParticle(Particle.CLOUD, loc, 6, 0.5, 0.5, 0.5);
	}

	public void successRitual() {
		if (this.ritualData == null) return;

		this.ritualData.getPerformers().forEach(uuid -> {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null)
				TrappedNewbieAdvancements.PERFORM_A_RITUAL.awardAllCriteria(player);
		});

		getBlock().getWorld().strikeLightning(getBlock().getLocation().center());
		switch (this.ritualData.getRitual()) {
			case RAIN_DANCE -> getBlock().getWorld().setStorm(true);
			case SOUL_MELANCHOLY -> this.ritualData.getPerformers().forEach(uuid -> {
				Player player = Bukkit.getPlayer(uuid);
				if (player == null) return;
				LivingEntity possessed = PossessingPlayer.getPossessed(player);
				if (possessed != null && player.getLevel() < 1) {
					player.setLevel(1);
					player.setExp(0F);
				}

				if (player.hasPotionEffect(PotionEffectType.WEAKNESS)) {
					if (possessed == null) {
						PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.WEAKNESS);
						player.removePotionEffect(PotionEffectType.WEAKNESS);
						if (potionEffect != null)
							player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, potionEffect.getDuration(), potionEffect.getAmplifier(), potionEffect.isAmbient(), potionEffect.hasParticles(), potionEffect.hasIcon()));
					} else if (PossessedUndeadZombieCuring.canBeCured(possessed)) {
						player.removePotionEffect(PotionEffectType.WEAKNESS);
						PossessedUndeadZombieCuring.startCureTask(possessed);
					}
				}
			});
		}

		this.ritualData = null;
	}

	public void failRitual() {
		if (this.ritualData != null) {
			this.ritualData.getPerformers().forEach(uuid -> {
				Player player = Bukkit.getPlayer(uuid);
				if (player != null)
					HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("ritual.failed"));
			});
		}
		stopRitual();
	}

	public void stopRitual() {
		this.ritualData = null;
		Location loc = getBlock().getLocation().center(0.8);
		loc.getWorld().spawnParticle(Particle.SMOKE, loc, 18, 0.5, 0.5, 0.5);
	}

	public void abortRitual() {
		if (this.ritualData == null) return;

		new HashSet<>(this.ritualData.getPerformers()).forEach(TotemRituals::abortActiveRitual);
		stopRitual();
	}

	public void abortPickers() {
		this.ritualPickers.forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p == null) return;

			p.closeInventory();
			if (this.ritualData == null) TotemRituals.abortActiveRitual(uuid);
			else TotemRituals.pickRitual(p, this.ritualData);
		});
		this.ritualPickers.clear();
	}

}
