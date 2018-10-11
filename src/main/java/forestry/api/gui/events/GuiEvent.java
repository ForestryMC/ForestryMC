package forestry.api.gui.events;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IGuiElement;
import forestry.api.gui.IWindowElement;

@SideOnly(Side.CLIENT)
public class GuiEvent extends GuiElementEvent {
	private GuiEvent(IGuiElement origin) {
		super(origin);
	}

	public static class KeyEvent extends GuiEvent {
		private final char character;
		private final int key;

		public KeyEvent(IGuiElement origin, char character, int key) {
			super(origin);
			this.character = character;
			this.key = key;
		}

		public char getCharacter() {
			return this.character;
		}

		public int getKey() {
			return this.key;
		}
	}

	public static class ButtonEvent extends GuiEvent {
		private final int x;
		private final int y;
		private final int relativeX;
		private final int relativeY;
		private final int button;

		public ButtonEvent(IGuiElement origin, int x, int y, int button) {
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

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getButton() {
			return this.button;
		}
	}

	public static class DownEvent extends ButtonEvent {
		public DownEvent(IGuiElement origin, int x, int y, int button) {
			super(origin, x, y, button);
		}
	}

	public static class UpEvent extends ButtonEvent {
		public UpEvent(IGuiElement origin, int x, int y, int button) {
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
		private int dWheel;

		public WheelEvent(IGuiElement origin, int dWheel) {
			super(origin);
			this.dWheel = 0;
			this.dWheel = dWheel / 28;
		}

		public int getDWheel() {
			return this.dWheel;
		}
	}
}
