package forestry.book.gui.elements;

import java.util.List;

import forestry.core.gui.elements.layouts.ElementGroup;

public class PageLinkIndex extends ElementGroup {

	private List<PageLink> links;

	public PageLinkIndex(int xPos, int yPos, int width, int height, List<PageLink> links) {
		super(xPos, yPos, width, height);
		this.links = links;
	}

	public void init(){

	}

	public class PageLink {
		public int pageIndex;
		public final String title;

		public PageLink(String title) {
			this.title = title;
		}
	}
}
