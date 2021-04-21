package forestry.core.gui.elements.lib.events;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.layouts.ElementGroup;

@OnlyIn(Dist.CLIENT)
public enum GuiEventOrigin {
	ANY {
		@Override
		public boolean isOrigin(GuiElement origin, @Nullable GuiElement element) {
			return true;
		}
	},
	SELF {
		@Override
		public boolean isOrigin(GuiElement origin, @Nullable GuiElement element) {
			return element == origin;
		}
	},
	PARENT {
		@Override
		public boolean isOrigin(GuiElement origin, @Nullable GuiElement element) {
			return element != null && element.getParent() == origin;
		}
	},
	DIRECT_CHILD {
		@Override
		public boolean isOrigin(GuiElement origin, @Nullable GuiElement element) {
			if (!(element instanceof ElementGroup)) {
				return false;
			}
			Collection<GuiElement> elements = ((ElementGroup) element).getElements();
			return elements.contains(origin);
		}
	};

	public abstract boolean isOrigin(GuiElement origin, @Nullable GuiElement element);
}
