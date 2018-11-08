package forestry.core.gui.elements;

import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

import forestry.api.gui.IValueElement;
import forestry.api.gui.IWindowElement;
import forestry.api.gui.events.ElementEvent;
import forestry.api.gui.events.GuiEvent;
import forestry.api.gui.events.GuiEventDestination;
import forestry.api.gui.events.TextEditEvent;

public class TextEditElement extends GuiElement implements IValueElement<String> {

	private final GuiTextField field;

	public TextEditElement(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
		field = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, 0, 0, width, height);
		field.setEnableBackgroundDrawing(false);
		this.addSelfEventHandler(GuiEvent.KeyEvent.class, event -> {
			String oldText = field.getText();
			this.field.textboxKeyTyped(event.getCharacter(), event.getKey());
			final String text = field.getText();
			if (!text.equals(oldText)) {
				postEvent(new TextEditEvent(this, text, oldText), GuiEventDestination.ALL);
			}
		});
		this.addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
			IWindowElement windowElement = getWindow();
			this.field.mouseClicked(windowElement.getRelativeMouseX(this), windowElement.getRelativeMouseY(this), event.getButton());
		});
		this.addSelfEventHandler(ElementEvent.GainFocus.class, event -> {
			this.field.setFocused(true);
		});
		this.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> {
			this.field.setFocused(false);
		});
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

	@Override
	public void drawElement(int mouseX, int mouseY) {
		field.drawTextBox();
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
