package forestry.core.gui.elements.lib.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.layouts.ElementGroup;

@OnlyIn(Dist.CLIENT)
public enum GuiEventDestination {
	//Only the current element
	SINGLE {
		@Override
		public void sendEvent(GuiElement element, GuiElementEvent event) {
			element.receiveEvent(event);
		}
	},
	//Only the origin element
	ORIGIN {
		@Override
		public void sendEvent(GuiElement element, GuiElementEvent event) {
			event.getOrigin().receiveEvent(event);
		}
	},
	ALL {
		@Override
		public void sendEvent(GuiElement element, GuiElementEvent event) {
			element.receiveEvent(event);
			if (!(element instanceof ElementGroup)) {
				return;
			}
			for (GuiElement child : ((ElementGroup) element).getElements()) {
				child.postEvent(event, ALL);
			}
		}
	};

	public abstract void sendEvent(GuiElement element, GuiElementEvent event);
}
