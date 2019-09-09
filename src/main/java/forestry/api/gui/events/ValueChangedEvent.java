package forestry.api.gui.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.gui.IGuiElement;

@OnlyIn(Dist.CLIENT)
public class ValueChangedEvent<V> extends GuiElementEvent {
	private final V newValue;
	private final V oldValue;

	public ValueChangedEvent(IGuiElement origin, V newValue, V oldValue) {
		super(origin);
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	public V getNewValue() {
		return newValue;
	}

	public V getOldValue() {
		return oldValue;
	}
}
