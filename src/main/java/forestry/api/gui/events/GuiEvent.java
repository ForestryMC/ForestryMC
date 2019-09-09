package forestry.api.gui.events;


import net.minecraft.client.util.InputMappings;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.gui.IGuiElement;
import forestry.api.gui.IWindowElement;

@OnlyIn(Dist.CLIENT)
public class GuiEvent extends GuiElementEvent {
	private GuiEvent(IGuiElement origin) {
		super(origin);
	}

	public static class KeyEvent extends GuiEvent {
		private final InputMappings.Input mouseKey;

		//TODO - better parameter names
		public KeyEvent(IGuiElement origin, int pressed_1, int pressed_2) {
			super(origin);
			this.mouseKey = InputMappings.getInputByCode(pressed_1, pressed_2);

		}

		public InputMappings.Input getMouseKey() {
			return mouseKey;
		}
	}

	public static class ButtonEvent extends GuiEvent {
		private final double x;
		private final double y;
		private final int relativeX;
		private final int relativeY;
		private final int button;

		public ButtonEvent(IGuiElement origin, double x, double y, int button) {
			super(origin);
			this.x = x;
			this.y = y;
			this.button = button;
			IWindowElement windowElement = origin.getWindow();
			this.relativeX = windowElement.getRelativeMouseX(origin);
			this.relativeY = windowElement.getRelativeMouseY(origin);
		}

		public int getRelativeX() {
			return relativeX;
		}

		public int getRelativeY() {
			return relativeY;
		}

		public double getX() {
			return this.x;
		}

		public double getY() {
			return this.y;
		}

		public int getButton() {
			return this.button;
		}
	}

	public static class DownEvent extends ButtonEvent {
		public DownEvent(IGuiElement origin, double x, double y, int button) {
			super(origin, x, y, button);
		}
	}

	public static class UpEvent extends ButtonEvent {
		public UpEvent(IGuiElement origin, double x, double y, int button) {
			super(origin, x, y, button);
		}
	}

	public static class MoveEvent extends GuiEvent {
		private final float dx;
		private final float dy;

		public MoveEvent(IGuiElement origin, float dx, float dy) {
			super(origin);
			this.dx = dx;
			this.dy = dy;
		}

		public float getDx() {
			return this.dx;
		}

		public float getDy() {
			return this.dy;
		}
	}

	public static class DragEvent extends MoveEvent {
		public DragEvent(IGuiElement draggedWidget, float dx, float dy) {
			super(draggedWidget, dx, dy);
		}
	}

	public static class WheelEvent extends GuiEvent {
		private double dWheel;

		public WheelEvent(IGuiElement origin, double dWheel) {
			super(origin);
			this.dWheel = 0;    //TODO why is this line needed?
			this.dWheel = dWheel / 28;
		}

		public double getDWheel() {
			return this.dWheel;
		}
	}
}
