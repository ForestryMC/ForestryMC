package forestry.core.gui.widgets;

/*public class WidgetElementProvider extends Widget implements IScrollable{
	protected ScrollableElement scrollable;

	public WidgetElementProvider(WidgetManager manager, int xPos, int yPos, int width, int height, ScrollableElement scrollable) {
		super(manager, xPos, yPos);
		this.scrollable = scrollable;
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(int startX, int startY) {
		scrollable.draw(startX + xPos, startY + yPos, 0, 0);
	}

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		mouseX-=xPos;
		mouseY-=yPos;
		List<String> tooltip = scrollable.getTooltip(mouseX, mouseY);
		if(tooltip.isEmpty()){
			return null;
		}
		ToolTip toolTip = new ToolTip();
		toolTip.add(tooltip);
		return toolTip;
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		mouseX-=xPos;
		mouseY-=yPos;
		return scrollable.isFocused(mouseX, mouseY);
	}

	@Override
	public void onScroll(int value) {
		scrollable.onScroll(value);
	}
}*/
