package forestry.api.gui.events;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IGuiElement;

@SideOnly(Side.CLIENT)
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
