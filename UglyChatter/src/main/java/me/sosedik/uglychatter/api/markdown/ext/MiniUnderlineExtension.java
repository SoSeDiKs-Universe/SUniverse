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
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * Adds support for parsing {@code __underline__} to {@code <u>underline</u>}.
 */
@NullMarked
public class MiniUnderlineExtension implements Parser.ParserExtension, MarkdownRenderer.MarkdownRendererExtension {

	private MiniUnderlineExtension() {}

	public static MiniUnderlineExtension create() {
		return new MiniUnderlineExtension();
	}

	@Override
	public void extend(Parser.Builder parserBuilder) {
		parserBuilder.customDelimiterProcessor(new MiniUnderlineDelimiterProcessor());
	}

	@Override
	public void extend(MarkdownRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(new MarkdownNodeRendererFactory() {
			@Override
			public NodeRenderer create(MarkdownNodeRendererContext context) {
				return new MiniUnderlineMarkdownNodeRenderer(context);
			}

			@Override
			public Set<Character> getSpecialCharacters() {
				return Set.of('_');
			}
		});
	}

	/**
	 * An ins node containing text and other inline nodes as children.
	 */
	public static class Underline extends CustomNode implements Delimited {

		private static final String DELIMITER = "__";

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
			return '_';
		}

		@Override
		public char getClosingCharacter() {
			return '_';
		}

		@Override
		public int getMinLength() {
			return 2;
		}

		@Override
		public int process(DelimiterRun openingRun, DelimiterRun closingRun) {
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

		public MiniUnderlineMarkdownNodeRenderer(MarkdownNodeRendererContext context) {
			super(context);
		}

		@Override
		public Set<Class<? extends Node>> getNodeTypes() {
			return Set.of(Underline.class);
		}

		@Override
		public void render(Node node) {
			writer.raw("<u>");
			renderChildren(node);
			writer.raw("</u>");
		}

	}

}
