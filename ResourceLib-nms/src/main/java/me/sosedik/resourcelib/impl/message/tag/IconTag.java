package me.sosedik.resourcelib.impl.message.tag;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IconTag {

	private IconTag() {}

	public static final TagResolver ICON = TagResolver.resolver("icon", IconTag::create);

	static Tag create(ArgumentQueue args, Context ctx) {
		if (!args.hasNext())
			throw ctx.newException("Can't turn " + args + " into icon", args);

		String namespace = args.pop().lowerValue();
		if (!args.hasNext())
			throw ctx.newException("Can't turn " + args + " into icon", args);

		String value = args.pop().lowerValue();

		FontData data = ResourceLib.storage().getFontData(new NamespacedKey(namespace, value));
		return Tag.selfClosingInserting(data == null ? Component.text("#") : data.icon());
	}

}
