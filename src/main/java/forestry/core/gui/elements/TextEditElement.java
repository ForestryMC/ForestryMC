package forestry.core.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.gui.elements.lib.IValueElement;
import forestry.core.gui.elements.lib.IWindowElement;
import forestry.core.gui.elements.lib.events.ElementEvent;
import forestry.core.gui.elements.lib.events.GuiEvent;
import forestry.core.gui.elements.lib.events.GuiEventDestination;
import forestry.core.gui.elements.lib.events.TextEditEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.function.Predicate;

public class TextEditElement extends GuiElement implements IValueElement<String> {

    private final TextFieldWidget field;

    public TextEditElement(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
        //TODO - title
        field = new TextFieldWidget(Minecraft.getInstance().fontRenderer, 0, 0, width, height, null);
        field.setEnableBackgroundDrawing(false);
        this.addSelfEventHandler(GuiEvent.KeyEvent.class, event -> {
            String oldText = field.getText();
            this.field.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
            final String text = field.getText();
            if (!text.equals(oldText)) {
                postEvent(new TextEditEvent(this, text, oldText), GuiEventDestination.ALL);
            }
        });
        this.addSelfEventHandler(GuiEvent.CharEvent.class, event -> {
            String oldText = field.getText();
            this.field.charTyped(event.getCharacter(), event.getModifiers());
            final String text = field.getText();
            if (!text.equals(oldText)) {
                postEvent(new TextEditEvent(this, text, oldText), GuiEventDestination.ALL);
            }
        });
        this.addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
            IWindowElement windowElement = getWindow();
            this.field.mouseClicked(
                    windowElement.getRelativeMouseX(this),
                    windowElement.getRelativeMouseY(this),
                    event.getButton()
            );
        });
        //TODO - method protected so maybe AT the field itself?
        this.addSelfEventHandler(ElementEvent.GainFocus.class, event -> this.field.setFocused2(true));
        this.addSelfEventHandler(ElementEvent.LoseFocus.class, event -> this.field.setFocused2(false));
    }

    public TextEditElement setMaxLength(int maxLength) {
        field.setMaxStringLength(maxLength);
        return this;
    }

    public TextEditElement setValidator(Predicate<String> validator) {
        field.setValidator(validator);
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
    public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
        field.render(transform, mouseX, mouseY, 0);
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
