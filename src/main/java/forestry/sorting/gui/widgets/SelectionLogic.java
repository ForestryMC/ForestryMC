package forestry.sorting.gui.widgets;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import forestry.core.gui.GuiForestry;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.IScrollable;
import forestry.sorting.gui.ISelectableProvider;

public class SelectionLogic<S> implements IScrollable {
	private static final int SELECTABLE_PER_ROW = 11;

	private final ISelectableProvider<S> provider;
	private final Comparator<S> comparator;
	private final SelectionWidget widget;
	private final Collection<S> entries;
	private ArrayList<S> sorted = new ArrayList<>();
	private Set<SelectableWidget> visible = new HashSet<>();

	public SelectionLogic(SelectionWidget widget, ISelectableProvider<S> provider) {
		this.widget = widget;
		this.provider = provider;
		this.entries = provider.getEntries();
		this.comparator = (S f, S s) -> provider.getName(f).compareToIgnoreCase(provider.getName(s));

	}

	public boolean isSame(ISelectableProvider provider) {
		return this.provider == provider;
	}

	@Override
	public void onScroll(int value) {
		visible.clear();
		int startIndex = value * SELECTABLE_PER_ROW;
		Y:
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < SELECTABLE_PER_ROW; x++) {
				int index = startIndex + y * SELECTABLE_PER_ROW + x;
				if (index >= sorted.size()) {
					break Y;
				}
				visible.add(new SelectableWidget(sorted.get(index), widget.getX() + 12 + x * 16, widget.getY() + 16 + y * 16));
			}
		}
	}

	public Set<SelectableWidget> getVisible() {
		return visible;
	}

	public void filterEntries(String searchText) {
		sorted.clear();
		sorted.ensureCapacity(entries.size());

		Pattern pattern;
		try {
			pattern = Pattern.compile(searchText.toLowerCase(Locale.ENGLISH), Pattern.CASE_INSENSITIVE);
		} catch (Throwable ignore) {
			try {
				pattern = Pattern.compile(Pattern.quote(searchText.toLowerCase(Locale.ENGLISH)), Pattern.CASE_INSENSITIVE);
			} catch (Throwable e) {
				return;
			}
		}

		for (S entry : entries) {
			String name = provider.getName(entry);
			if (pattern.matcher(name.toLowerCase(Locale.ENGLISH)).find()) {
				sorted.add(entry);
			}
		}
		sorted.sort(comparator);

		int elements = sorted.size() / SELECTABLE_PER_ROW - 4;
		if (elements > 0) {
			widget.scrollBar.setParameters(this, 0, elements, 1);
		} else {
			onScroll(0);
		}
		widget.scrollBar.setVisible(elements > 0);

	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return widget.isMouseOver(mouseX, mouseY);
	}

	public void draw() {
		for (SelectableWidget selectable : visible) {
			selectable.draw(widget.gui);
		}
	}

	@Nullable
	public ToolTip getToolTip(int mouseX, int mouseY) {
		for (SelectableWidget selectable : visible) {
			if (selectable.isMouseOver(mouseX, mouseY)) {
				return selectable.getToolTip();
			}
		}
		return null;
	}

	public void select(int mouseX, int mouseY) {
		mouseX -= widget.gui.getGuiLeft();
		mouseY -= widget.gui.getGuiTop();
		for (SelectableWidget selectable : visible) {
			if (selectable.isMouseOver(mouseX, mouseY)) {
				provider.onSelect(selectable.selectable);
				break;
			}
		}
	}

	private class SelectableWidget {
		private final S selectable;
		private final int xPos;
		private final int yPos;

		public SelectableWidget(S entry, int xPos, int yPos) {
			this.selectable = entry;
			this.xPos = xPos;
			this.yPos = yPos;
		}

		public void draw(GuiForestry gui) {
			provider.draw(gui, selectable, xPos, yPos);
		}

		public boolean isMouseOver(int mouseX, int mouseY) {
			return mouseX >= xPos && mouseX <= xPos + 16 && mouseY >= yPos && mouseY <= yPos + 16;
		}

		public ToolTip getToolTip() {
			ToolTip toolTip = new ToolTip();
			toolTip.add(provider.getName(selectable));
			return toolTip;
		}
	}
}
