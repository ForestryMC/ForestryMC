package forestry.database.gui.buttons;

import forestry.core.gui.buttons.GuiBetterButton;
import forestry.database.DatabaseManager;

public class GuiDatabaseButton<V> extends GuiBetterButton {

	public final DatabaseButton type;
	public final DatabaseManager manager;
	public V value;

	public GuiDatabaseButton(int id, int x, int y, V value, DatabaseManager manager, DatabaseButton type) {
		super(id, x, y, type.getDefaultTexture());
		this.type = type;
		this.manager = manager;
		setValue(value);
	}

	public void setValue(V value) {
		this.value = value;
		if(value instanceof String){
			setLabel((String) value);
		}
		type.onValueChange(this);
	}

	public void onPressed(){
		type.onPressed(this);
	}

	public V getValue() {
		return value;
	}
}
