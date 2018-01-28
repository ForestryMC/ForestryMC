package forestry.api.book;

import javax.annotation.Nullable;

import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;

public abstract class BookContent<D> {
	public String type;
	public boolean forcePage = false;
	@Nullable
	public transient D data = null;

	@Nullable
	public abstract Class<? extends D> getDataClass();

	public void onDeserialization(){
	}

	public abstract boolean addElements(IElementGroup group, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement);
}
