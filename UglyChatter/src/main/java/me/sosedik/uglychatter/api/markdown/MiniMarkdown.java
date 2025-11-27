package me.sosedik.uglychatter.api.markdown;

import me.sosedik.uglychatter.api.markdown.ext.MiniSpoilerExtension;
import me.sosedik.uglychatter.api.markdown.ext.MiniStrikethroughExtension;
import me.sosedik.uglychatter.api.markdown.ext.MiniUnderlineExtension;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext;
import org.commonmark.renderer.markdown.MarkdownNodeRendererFactory;
import org.commonmark.renderer.markdown.MarkdownRenderer;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * Parses markdown into minimessage string
 */
@NullMarked
public class MiniMarkdown {

	private MiniMarkdown() {
		throw new IllegalStateException("Utility class");
	}

	private static final List<Extension> EXTENSIONS = List.of(
		AutolinkExtension.create(),
		MiniSpoilerExtension.create(),
		MiniUnderlineExtension.create(),
		MiniStrikethroughExtension.create()
	);
	private static final Parser PARSER = Parser.builder()
		.extensions(EXTENSIONS)
		.build();
	private static final MarkdownRenderer RENDERER = MarkdownRenderer.builder()
		.nodeRendererFactory(new MarkdownNodeRendererFactory() {
			@Override
			public NodeRenderer create(MarkdownNodeRendererContext context) {
				return new MiniNodeRenderer(context);
			}

			@Override
			public Set<Character> getSpecialCharacters() {
				return Set.of();
			}
		})
		.extensions(EXTENSIONS)
		.build();

	static {
		// Override the default markdown renderer
		try {
			Field field = RENDERER.getClass().getDeclaredField("nodeRendererFactories");
			field.setAccessible(true);
			List<?> nodeRendererFactories = (List<?>) field.get(RENDERER);
			nodeRendererFactories.removeLast();
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Couldn't override the default markdown renderer", e);
		}
	}

	/**
	 * Converts markdown into minimessage
	 *
	 * @param markdown markdown string
	 * @return minimessage string
	 */
	public static String markdownToMini(String markdown) {
		Node document = PARSER.parse(markdown);
		return RENDERER.render(document).replace("\n", "<br>");
	}

}
