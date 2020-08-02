package forestry.core.gui.elements.lib.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.elements.lib.IGuiElement;

@OnlyIn(Dist.CLIENT)
public class TextEditEvent extends ValueChangedEvent<String> {
    public TextEditEvent(IGuiElement origin, String newValue, String oldValue) {
        super(origin, newValue, oldValue);
    }
}
