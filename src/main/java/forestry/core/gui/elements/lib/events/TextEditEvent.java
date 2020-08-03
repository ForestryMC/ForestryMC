package forestry.core.gui.elements.lib.events;


import forestry.core.gui.elements.lib.IGuiElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextEditEvent extends ValueChangedEvent<String> {
    public TextEditEvent(IGuiElement origin, String newValue, String oldValue) {
        super(origin, newValue, oldValue);
    }
}
