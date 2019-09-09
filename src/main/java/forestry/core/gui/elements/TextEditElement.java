package forestry.core.gui.elements;

import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;

import forestry.api.gui.IValueElement;
import forestry.api.gui.IWindowElement;
import forestry.api.gui.events.ElementEvent;
import forestry.api.gui.events.GuiEvent;
import forestry.api.gui.events.GuiEventDestination;
import forestry.api.gui.events.TextEditEvent;

public class TextEditElement extends GuiElement implements IValueElement<String> {

	private final TextFieldWidget field;

	public TextEditElement(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
		//TODO - title
		field = new TextFieldWidget(Minecraft.getInstance().fontRenderer, 0, 0, width, height, "TEST_TITLE");
		field.setEnableBackgroundDrawing(false);
		this.addSelfEventHandler(GuiEvent.KeyEvent.class, event -> {
			String oldText = field.getText();
			//TODO - find a way to getComb the right char because this seems dodgy
			this.field.charTyped((char) event.getMouseKey().getKeyCode(), event.getMouseKey().getKeyCode());
			final String text = field.getText();
			if (!text.equals(oldText)) {
				postEvent(new TextEditEvent(this, text, oldText), GuiEventDestination.ALL);
			}
		});
		this.addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
			IWindowElement windowElement = getWindow();
			this.field.mouseClicked(windowElement.getRelativeMouseX(this), windowElement.getRelativeMouseY(this), event.getButton());
		});
		//TODO - method protected so maybe AT the field itself?
		this.addSelfEventHandler(ElementEvent.GainFocus.class, event -> this.field.focused = true);
		this.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> this.field.focused = false);
	}

	public TextEditElement setMaxLength(int maxLength) {
		field.setMaxStringLength(maxLength);
		return this;
	}

	public TextEditElement setValidator(Predicate<String> validator) {
		field.setValidator(validator::test);
		return this;
	}

	@Override
	public String getValue() {
		return field.getText();
	}

	@Override
	public void setValue(String value) {
		if (!field.getText().equals(value)) {
			field.setText(value);
		}
	}

	//TODO - maybe need to supply start/end points now?
	//TODO third param probably partial ticks. Is it being 0 a problem?
	@Override
	public void drawElement(int mouseX, int mouseY) {
		field.render(mouseX, mouseY, 0);
	}

	@Override
	public boolean canFocus() {
		return true;
	}

	@Override
	public boolean canMouseOver() {
		return true;
	}
}
