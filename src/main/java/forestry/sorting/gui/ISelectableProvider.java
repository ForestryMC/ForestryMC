package forestry.sorting.gui;

import java.util.Collection;

import forestry.core.gui.GuiForestry;

public interface ISelectableProvider<S> {
	Collection<S> getEntries();

	void onSelect(S selectable);

	void draw(GuiForestry gui, S selectable, int x, int y);

	String getName(S selectable);
}
