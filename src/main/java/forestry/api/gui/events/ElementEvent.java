package forestry.api.gui.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.gui.IGuiElement;

@OnlyIn(Dist.CLIENT)
public class ElementEvent extends GuiElementEvent {

	private ElementEvent(IGuiElement origin) {
		super(origin);
	}

	public static class Deletion extends ElementEvent {
		public Deletion(IGuiElement origin) {
			super(origin);
		}
	}

	public static class StartMouseOver extends ElementEvent {
		public StartMouseOver(IGuiElement origin) {
			super(origin);
		}
	}

	public static class EndMouseOver extends ElementEvent {
		public EndMouseOver(IGuiElement origin) {
			super(origin);
		}
	}

	public static class StartDrag extends ElementEvent {
		private final int button;

		public StartDrag(IGuiElement origin, int button) {
			super(origin);
			this.button = button;
		}

		public int getButton() {
			return this.button;
		}
	}

	public static class EndDrag extends ElementEvent {
		public EndDrag(IGuiElement origin) {
			super(origin);
		}
	}

	public static class GainFocus extends ElementEvent {
		public GainFocus(IGuiElement origin) {
			super(origin);
		}
	}

	public static class LoseFocus extends ElementEvent {
		public LoseFocus(IGuiElement origin) {
			super(origin);
		}
	}
}
