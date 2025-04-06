package me.sosedik.uglychatter.api.markdown.ext;

import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext;
import org.commonmark.renderer.markdown.MarkdownWriter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class SimpleMiniMarkdownNodeRenderer implements NodeRenderer {

	protected final MarkdownNodeRendererContext context;
	protected final MarkdownWriter writer;

	protected SimpleMiniMarkdownNodeRenderer(MarkdownNodeRendererContext context) {
		this.context = context;
		this.writer = context.getWriter();
	}

	protected void renderChildren(Node parent) {
		Node node = parent.getFirstChild();
		while (node != null) {
			Node next = node.getNext();
			context.render(node);
			node = next;
		}
	}
	
}
