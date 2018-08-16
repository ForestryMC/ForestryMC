package forestry.api.gui.events;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IGuiElement;

@SideOnly(Side.CLIENT)
public abstract class GuiElementEvent {
	private final IGuiElement origin;

	public GuiElementEvent(IGuiElement origin) {
		this.origin = origin;
	}

	public final IGuiElement getOrigin() {
		return origin;
	}

	public final boolean isOrigin(IGuiElement element) {
		return this.origin == element;
	}
}
