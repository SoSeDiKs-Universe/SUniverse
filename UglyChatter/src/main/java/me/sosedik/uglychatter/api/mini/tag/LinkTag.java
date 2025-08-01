package me.sosedik.uglychatter.api.mini.tag;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.uglychatter.UglyChatter;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.FileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.sosedik.uglychatter.UglyChatter.uglyChatterKey;
import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

@NullMarked
public record LinkTag(Messenger messenger) implements TagResolver {

	private static final List<KnownLink> KNOWN_LINKS = new ArrayList<>();
	private static KnownLink defaultLink;

	@Override
	public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) {
		if (!has(name)) return null;
		String url = arguments.popOr("Link tag requires url argument").value();
		String fullUrl = url.startsWith("http") ? url : "https://" + url;
		String display = URI.create(fullUrl).getHost(); // Shorten URL
		KnownLink knownLink = parseLink(display);
		Component link = Component.text()
			.content("")
			.hoverEvent(
				combined(
					knownLink.getLinkIcon(),
					Component.space(),
					messenger.getMessage("tag.link.visit"),
					Component.newline(),
					Component.newline(),
					Component.text(fullUrl, NamedTextColor.GRAY)
				)
			)
			.clickEvent(
				ClickEvent.openUrl(fullUrl)
			)
			.append(
				knownLink.getLinkIcon(),
				Component.text(display, knownLink.getColor()).decorate(TextDecoration.UNDERLINED)
			)
			.build();
		return Tag.selfClosingInserting(link);
	}

	@Override
	public boolean has(String name) {
		return name.equals("link");
	}

	public static KnownLink parseLink(String link) {
		for (KnownLink knownLink : KNOWN_LINKS) {
			if (knownLink.isParsable(link))
				return knownLink;
		}
		return defaultLink;
	}

	public static void reloadLinks(UglyChatter plugin) {
		KNOWN_LINKS.clear();
		var linksFile = new File(plugin.getDataFolder(), "links.json");
		if (!linksFile.exists())
			plugin.saveResource("links.json", true);
		var linksJson = FileUtil.readJsonObject(linksFile);
		for (Map.Entry<String, JsonElement> link : linksJson.entrySet()) {
			String name = link.getKey();
			var knownLink = new KnownLink(name, link.getValue().getAsJsonObject());
			if (name.equals("default"))
				defaultLink = knownLink;
			else
				KNOWN_LINKS.add(knownLink);
		}
	}

	public static class KnownLink {

		private static final Component LINK_TOP = ResourceLib.requireFontData(uglyChatterKey("link_top")).icon();
		private static final Component LINK_BOTTOM = ResourceLib.requireFontData(uglyChatterKey("link_bottom")).icon();

		private final String name;
		private final TextColor color;
		private final Component link;
		private final String[] urls;

		public KnownLink(String name, JsonObject jsonObject) {
			this.name = name;
			TextColor hex1 = TextColor.fromHexString(jsonObject.get("hex1").getAsString());
			TextColor hex2 = TextColor.fromHexString(jsonObject.get("hex2").getAsString());
			assert hex1 != null;
			assert hex2 != null;
			this.link = getLinkIcon(hex1, hex2);
			this.color = hex1;

			JsonArray links = jsonObject.getAsJsonArray("links");
			this.urls = new String[links.size()];
			for (int i = 0; i < urls.length; i++)
				urls[i] = links.get(i).getAsString();
		}

		public String getName() {
			return name;
		}

		public TextColor getColor() {
			return color;
		}

		public Component getLinkIcon() {
			return link;
		}

		public boolean isParsable(String link) {
			for (String url : urls) {
				if (link.endsWith(url))
					return true;
			}
			return false;
		}

		public static Component getLinkIcon(TextColor hex1, TextColor hex2) {
			return combine(SpacingUtil.getSpacing(-7),
				LINK_TOP.color(hex1),
				LINK_BOTTOM.color(hex2)
			);
		}

	}

}
