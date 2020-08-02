package forestry.core.gui.elements.lib;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IValueElement<V> extends IGuiElement {

    V getValue();

    void setValue(V value);
}
