package forestry.database.gui.buttons;

import java.util.Collections;
import java.util.List;

import forestry.core.gui.buttons.IButtonTextureSet;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.database.DatabaseHelper;

public enum DatabaseButton {
	SORT_DIRECTION_BUTTON {
		@Override
		public void onValueChange(GuiDatabaseButton button) {
			boolean ascending = (boolean) button.getValue();
			button.setTexture(ascending ? StandardButtonTextureSets.ARROW_UP_BUTTON : StandardButtonTextureSets.ARROW_DOWN_BUTTON);
		}

		@Override
		public void onPressed(GuiDatabaseButton button) {
			//Client side only button
			button.setValue(DatabaseHelper.ascending = !DatabaseHelper.ascending);
			button.gui.markForSorting();
		}
	};

	public List<String> getTooltip(GuiDatabaseButton button) {
		return Collections.emptyList();
	}

	public IButtonTextureSet getDefaultTexture() {
		return StandardButtonTextureSets.SMALL_BLANK_BUTTON;
	}

	public void onValueChange(GuiDatabaseButton button) {

	}

	public abstract void onPressed(GuiDatabaseButton button);
}
