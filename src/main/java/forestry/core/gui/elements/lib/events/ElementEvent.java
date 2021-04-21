package forestry.core.gui.elements.lib.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.elements.GuiElement;

@OnlyIn(Dist.CLIENT)
public class ElementEvent extends GuiElementEvent {

	private ElementEvent(GuiElement origin) {
		super(origin);
	}

	public static class Deletion extends ElementEvent {
		public Deletion(GuiElement origin) {
			super(origin);
		}
	}

	public static class StartMouseOver extends ElementEvent {
		public StartMouseOver(GuiElement origin) {
			super(origin);
		}
	}

	public static class EndMouseOver extends ElementEvent {
		public EndMouseOver(GuiElement origin) {
			super(origin);
		}
	}

	public static class StartDrag extends ElementEvent {
		private final int button;

		public StartDrag(GuiElement origin, int button) {
			super(origin);
			this.button = button;
		}

		public int getButton() {
			return this.button;
		}
	}

	public static class EndDrag extends ElementEvent {
		public EndDrag(GuiElement origin) {
			super(origin);
		}
	}

	public static class GainFocus extends ElementEvent {
		public GainFocus(GuiElement origin) {
			super(origin);
		}
	}

	public static class LoseFocus extends ElementEvent {
		public LoseFocus(GuiElement origin) {
			super(origin);
		}
	}
}
