package forestry.api.gui.events;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;

@SideOnly(Side.CLIENT)
public enum GuiEventOrigin {
	ANY {
		@Override
		public boolean isOrigin(IGuiElement origin, @Nullable IGuiElement element) {
			return true;
		}
	},
	SELF {
		@Override
		public boolean isOrigin(IGuiElement origin, @Nullable IGuiElement element) {
			return element == origin;
		}
	},
	PARENT {
		@Override
		public boolean isOrigin(IGuiElement origin, @Nullable IGuiElement element) {
			return element != null && element.getParent() == origin;
		}
	},
	DIRECT_CHILD {
		@Override
		public boolean isOrigin(IGuiElement origin, @Nullable IGuiElement element) {
			if (element == null || !(element instanceof IElementGroup)) {
				return false;
			}
			Collection<IGuiElement> elements = ((IElementGroup) element).getElements();
			return elements.contains(origin);
		}
	};

	public abstract boolean isOrigin(IGuiElement origin, @Nullable IGuiElement element);
}
