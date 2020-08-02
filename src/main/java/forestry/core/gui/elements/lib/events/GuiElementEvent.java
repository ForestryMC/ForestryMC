package forestry.core.gui.elements.lib.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.elements.lib.IGuiElement;

@OnlyIn(Dist.CLIENT)
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
