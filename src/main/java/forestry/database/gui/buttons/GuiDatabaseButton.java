package forestry.database.gui.buttons;

import forestry.core.gui.buttons.GuiBetterButton;
import forestry.database.gui.GuiDatabase;

public class GuiDatabaseButton<V> extends GuiBetterButton {

	public final DatabaseButton type;
	public final GuiDatabase gui;
	public V value;

	public GuiDatabaseButton(int id, int x, int y, V value, GuiDatabase gui, DatabaseButton type) {
		super(id, x, y, type.getDefaultTexture());
		this.type = type;
		this.gui = gui;
		setValue(value);
	}

	public void setValue(V value) {
		this.value = value;
		if (value instanceof String) {
			setLabel((String) value);
		}
		type.onValueChange(this);
	}

	public void onPressed() {
		type.onPressed(this);
	}

	public V getValue() {
		return value;
	}
}
