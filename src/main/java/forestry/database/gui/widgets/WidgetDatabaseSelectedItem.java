package forestry.database.gui.widgets;

import net.minecraft.item.ItemStack;

import forestry.core.gui.widgets.ItemStackWidgetBase;
import forestry.core.gui.widgets.WidgetManager;
import forestry.database.DatabaseManager;

public class WidgetDatabaseSelectedItem extends ItemStackWidgetBase {
	private final DatabaseManager manager;

	public WidgetDatabaseSelectedItem(WidgetManager widgetManager, int xPos, int yPos, DatabaseManager manager) {
		super(widgetManager, xPos, yPos);
		this.manager = manager;
	}

	@Override
	protected ItemStack getItemStack() {
		return manager.getSelectedItemStack();
	}
}
