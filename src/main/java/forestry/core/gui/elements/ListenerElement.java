package forestry.core.gui.elements;


/**
 * Element that wraps around an other element, and listens to events.
 */
/*public class ListenerElement extends WrapperElement<GuiElement> {

	private final Multimap<KeyEvent, IKeyListener> keyListeners = HashMultimap.create();
	private final Multimap<MouseEvent, IMouseListener> mouseListeners = HashMultimap.create();

	public ListenerElement(GuiElement child) {
		super(child);
	}

	@Override
	public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
		if(child.onKeyPressed(keyCode, scanCode, modifiers)){
			return true;
		}
		return keyListeners.get(KeyEvent.KEY_PRESSED)
				.stream()
				.anyMatch((listener -> listener.onKeyPressed(keyCode, scanCode, modifiers)));
	}

	@Override
	public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
		if(child.onKeyReleased(keyCode, scanCode, modifiers)){
			return true;
		}
		return keyListeners.get(KeyEvent.KEY_RELEASED)
				.stream()
				.anyMatch((listener -> listener.onKeyReleased(keyCode, scanCode, modifiers)));
	}

	@Override
	public boolean onCharTyped(char keyCode, int modifiers) {
		if(child.onCharTyped(keyCode, modifiers)){
			return true;
		}
		return keyListeners.get(KeyEvent.CHAR_TYPED)
				.stream()
				.anyMatch((listener -> listener.onKeyReleased(keyCode, -1, modifiers)));
	}

	@Override
	public boolean onButton(double mouseX, double mouseY, int mouseButton) {
		if(child.onButton(mouseX, mouseY, mouseButton)){
			return true;
		}
		return mouseListeners.get(MouseEvent.)
				.stream()
				.anyMatch((listener -> listener.onKeyReleased(keyCode, scanCode, modifiers)));
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		return false;
	}

	@Override
	public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton) {
		return false;
	}

	@Override
	public boolean onMouseScrolled(double mouseX, double mouseY, double dWheel) {
		return false;
	}

	@Override
	public void onMouseMove(double mouseX, double mouseY) {

	}

	@Override
	public void onDrag(double mouseX, double mouseY) {

	}

	@Override
	public void onMouseEnter(double mouseX, double mouseY) {

	}

	@Override
	public void onMouseExit(double mouseX, double mouseY) {

	}

	protected boolean callListener(MouseEvent event, double mouseX, double mouseY, int mouseButton) {

	}
}*/
