package forestry.core.gui.event;

import javax.annotation.Nullable;

import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.events.GuiElementEvent;

public class EventValueChanged<V> extends GuiElementEvent {
    @Nullable
    private final V value;

    public EventValueChanged(IGuiElement origin, V value) {
        super(origin);
        this.value = value;
    }

    @Nullable
    public V getValue() {
        return value;
    }
}
