package forestry.api.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IValueElement<V> extends IGuiElement {

	V getValue();

	void setValue(V value);
}
