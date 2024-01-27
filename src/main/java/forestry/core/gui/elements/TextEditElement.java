package forestry.core.gui.elements;

import javax.annotation.Nullable;
import java.awt.Rectangle;
import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;

public class TextEditElement extends GuiElement implements IValueElement<String> {

	@Nullable
	private EditBox field;
	private int maxLength;
	private Predicate<String> validator;

	public TextEditElement(int maxLength) {
		this(maxLength, null);
	}

	public TextEditElement(int maxLength, @Nullable Predicate<String> validator) {
		this.maxLength = maxLength;
		this.validator = validator;
	}

	public TextEditElement() {
		//field = new TextFieldWidget(Minecraft.getInstance().font, 0, 0, width, height, StringTextComponent.EMPTY);
		//field.setBordered(false);
		/*this.addSelfEventHandler(GuiEvent.KeyEvent.class, event -> {
			String oldText = field.getValue();
			this.field.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
			final String text = field.getValue();
			if (!text.equals(oldText)) {
				postEvent(new TextEditEvent(this, text, oldText), GuiEventDestination.ALL);
			}
		});
		this.addSelfEventHandler(GuiEvent.CharEvent.class, event -> {
			String oldText = field.getValue();
			this.field.charTyped(event.getCharacter(), event.getModifiers());
			final String text = field.getValue();
			if (!text.equals(oldText)) {
				postEvent(new TextEditEvent(this, text, oldText), GuiEventDestination.ALL);
			}
		});
		this.addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
			Window windowElement = getWindow();
			this.field.mouseClicked(windowElement.getRelativeMouseX(this), windowElement.getRelativeMouseY(this), event.getButton());
		});
		//TODO - method protected so maybe AT the field itself?
		this.addSelfEventHandler(ElementEvent.GainFocus.class, event -> this.field.setFocus(true));
		this.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> this.field.setFocus(false));*/
	}

	@Override
	public void setAssignedBounds(Rectangle bounds) {
		super.setAssignedBounds(bounds);
		field = new EditBox(Minecraft.getInstance().font, 0, 0, bounds.width, bounds.height, Component.empty());
		field.setBordered(false);
		if (maxLength > 0) {
			field.setMaxLength(maxLength);
		}
	}

	@Override
	public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
		if (field == null || !this.field.keyPressed(keyCode, scanCode, modifiers)) {
			return false;
		}
		//		String oldText = field.getValue();
		//		final String text = field.getValue();
		//		if (!text.equals(oldText)) {
		//			postEvent(new TextEditEvent(this, text, oldText), GuiEventDestination.ALL);
		//		}
		return true;
	}

	@Override
	public boolean onCharTyped(char keyCode, int modifiers) {
		if (field == null || !this.field.charTyped(keyCode, modifiers)) {
			return false;
		}
		//		String oldText = field.getValue();
		//		final String text = field.getValue();
		//		if (!text.equals(oldText)) {
		//			postEvent(new TextEditEvent(this, text, oldText), GuiEventDestination.ALL);
		//		}
		return true;
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		return field != null && field.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public TextEditElement setValidator(Predicate<String> validator) {
		if (field == null) {
			return this;
		}
		field.setFilter(validator);
		return this;
	}

	@Override
	public String getValue() {
		return field.getValue();
	}

	@Override
	public void setValue(String value) {
		if (!field.getValue().equals(value)) {
			field.setValue(value);
		}
	}

	//TODO - maybe need to supply start/end points now?
	//TODO third param probably partial ticks. Is it being 0 a problem?
	@Override
	public void drawElement(PoseStack transform, int mouseX, int mouseY) {
		if (field == null) {
			return;
		}
		field.render(transform, mouseY, mouseX, 0);
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
