package forestry.api.gui.events;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IGuiElement;

@SideOnly(Side.CLIENT)
public class TextEditEvent extends ValueChangedEvent<String> {
	public TextEditEvent(IGuiElement origin, String newValue, String oldValue) {
		super(origin, newValue, oldValue);
	}
}
