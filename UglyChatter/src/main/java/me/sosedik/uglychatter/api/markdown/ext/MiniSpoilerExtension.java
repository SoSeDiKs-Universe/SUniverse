package me.sosedik.uglychatter.api.markdown.ext;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;
import org.commonmark.node.Node;
import org.commonmark.node.Nodes;
import org.commonmark.node.SourceSpans;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext;
import org.commonmark.renderer.markdown.MarkdownNodeRendererFactory;
import org.commonmark.renderer.markdown.MarkdownRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Adds support for parsing {@code ||spoiler||} to {@code <sp>spoiler</sp>}.
 */
public class MiniSpoilerExtension implements Parser.ParserExtension, MarkdownRenderer.MarkdownRendererExtension {

	private MiniSpoilerExtension() {}

	public static @NotNull MiniSpoilerExtension create() {
		return new MiniSpoilerExtension();
	}

	@Override
	public void extend(@NotNull Parser.Builder parserBuilder) {
		parserBuilder.customDelimiterProcessor(new MiniUnderlineDelimiterProcessor());
	}

	@Override
	public void extend(@NotNull MarkdownRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(new MarkdownNodeRendererFactory() {
			@Override
			public NodeRenderer create(@NotNull MarkdownNodeRendererContext context) {
				return new MiniUnderlineMarkdownNodeRenderer(context);
			}

			@Override
			public @NotNull Set<Character> getSpecialCharacters() {
				return Set.of('|');
			}
		});
	}

	/**
	 * An ins node containing text and other inline nodes as children.
	 */
	public static class Underline extends CustomNode implements Delimited {

		private static final String DELIMITER = "||";

		@Override
		public String getOpeningDelimiter() {
			return DELIMITER;
		}

		@Override
		public String getClosingDelimiter() {
			return DELIMITER;
		}

	}

	public static class MiniUnderlineDelimiterProcessor implements DelimiterProcessor {

		@Override
		public char getOpeningCharacter() {
			return '|';
		}

		@Override
		public char getClosingCharacter() {
			return '|';
		}

		@Override
		public int getMinLength() {
			return 2;
		}

		@Override
		public int process(@NotNull DelimiterRun openingRun, @NotNull DelimiterRun closingRun) {
			if (openingRun.length() >= 2 && closingRun.length() >= 2) {
				Text opener = openingRun.getOpener();

				Node underline = new Underline();

				var sourceSpans = new SourceSpans();
				sourceSpans.addAllFrom(openingRun.getOpeners(2));

				for (Node node : Nodes.between(opener, closingRun.getCloser())) {
					underline.appendChild(node);
					sourceSpans.addAll(node.getSourceSpans());
				}

				sourceSpans.addAllFrom(closingRun.getClosers(2));
				underline.setSourceSpans(sourceSpans.getSourceSpans());

				opener.insertAfter(underline);

				return 2;
			} else {
				return 0;
			}
		}
	}

	public static class MiniUnderlineMarkdownNodeRenderer extends SimpleMiniMarkdownNodeRenderer {

		public MiniUnderlineMarkdownNodeRenderer(@NotNull MarkdownNodeRendererContext context) {
			super(context);
		}

		@Override
		public Set<Class<? extends Node>> getNodeTypes() {
			return Set.of(Underline.class);
		}

		@Override
		public void render(@NotNull Node node) {
			writer.raw("<sp>");
			renderChildren(node);
			writer.raw("</sp>");
		}

	}

}
