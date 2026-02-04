package me.sosedik.resourcelib.impl.message.tag;

import me.sosedik.resourcelib.util.SpacingUtil;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SpaceTag {

	private SpaceTag() {}

	public static final TagResolver SPACE = TagResolver.resolver("space", SpaceTag::create);

	static Tag create(ArgumentQueue args, Context ctx) {
		if (!args.hasNext())
			throw ctx.newException("Can't turn " + args + " into icon", args);

		int space = args.pop().asInt().orElseThrow();
		return Tag.selfClosingInserting(SpacingUtil.getSpacing(space));
	}

}
