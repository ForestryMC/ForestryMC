package forestry.database.gui.buttons;

import forestry.core.gui.buttons.GuiBetterButton;
import forestry.database.gui.GuiDatabase;

import javax.annotation.Nullable;

public class GuiDatabaseButton<V> extends GuiBetterButton {

    public final DatabaseButton type;
    public final GuiDatabase gui;
    @Nullable
    public V value;

    public GuiDatabaseButton(int x, int y, V value, GuiDatabase gui, DatabaseButton type, IPressable handler) {
        super(x, y, type.getDefaultTexture(), handler);
        this.type = type;
        this.gui = gui;
        setValue(value);
    }

    public void setValue(V value) {
        this.value = value;
        if (value instanceof String) {
            //TODO: check
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
