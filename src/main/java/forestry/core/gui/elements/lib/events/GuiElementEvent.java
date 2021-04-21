package forestry.core.gui.elements.lib.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.elements.GuiElement;

@OnlyIn(Dist.CLIENT)
public abstract class GuiElementEvent {
	private final GuiElement origin;

	public GuiElementEvent(GuiElement origin) {
		this.origin = origin;
	}

	public final GuiElement getOrigin() {
		return origin;
	}

	public final boolean isOrigin(GuiElement element) {
		return this.origin == element;
	}

}
