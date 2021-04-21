package forestry.core.gui.elements.lib.events;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.Window;

@OnlyIn(Dist.CLIENT)
public class GuiEvent extends GuiElementEvent {
	private GuiEvent(GuiElement origin) {
		super(origin);
	}

	public static class KeyEvent extends GuiEvent {
		private final int keyCode;
		private final int scanCode;
		private final int modifiers;

		public KeyEvent(GuiElement origin, int keyCode, int scanCode, int modifiers) {
			super(origin);
			this.keyCode = keyCode;
			this.scanCode = scanCode;
			this.modifiers = modifiers;

		}

		public int getKeyCode() {
			return keyCode;
		}

		public int getScanCode() {
			return scanCode;
		}

		public int getModifiers() {
			return modifiers;
		}
	}

	public static class CharEvent extends GuiEvent {
		private final int keyCode;
		private final char character;
		private final int modifiers;

		public CharEvent(GuiElement origin, int keyCode, int modifiers) {
			super(origin);
			this.keyCode = keyCode;
			this.character = (char) keyCode;
			this.modifiers = modifiers;

		}

		public int getKeyCode() {
			return keyCode;
		}

		public char getCharacter() {
			return this.character;
		}

		public int getModifiers() {
			return modifiers;
		}
	}

	public static class ButtonEvent extends GuiEvent {
		private final double x;
		private final double y;
		private final int relativeX;
		private final int relativeY;
		private final int button;

		public ButtonEvent(GuiElement origin, double x, double y, int button) {
			super(origin);
			this.x = x;
			this.y = y;
			this.button = button;
			Window windowElement = origin.getWindow();
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
		public DownEvent(GuiElement origin, double x, double y, int button) {
			super(origin, x, y, button);
		}
	}

	public static class UpEvent extends ButtonEvent {
		public UpEvent(GuiElement origin, double x, double y, int button) {
			super(origin, x, y, button);
		}
	}

	public static class MoveEvent extends GuiEvent {
		private final float dx;
		private final float dy;

		public MoveEvent(GuiElement origin, float dx, float dy) {
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
		public DragEvent(GuiElement draggedWidget, float dx, float dy) {
			super(draggedWidget, dx, dy);
		}
	}

	public static class WheelEvent extends GuiEvent {
		private final double x;
		private final double y;
		private final double dWheel;

		public WheelEvent(GuiElement origin, double x, double y, double dWheel) {
			super(origin);
			this.x = x;
			this.y = y;
			this.dWheel = dWheel / 28;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getDWheel() {
			return this.dWheel;
		}
	}
}
