package forestry.core.gui.elements.lib;

import forestry.core.gui.elements.IProviderElement;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import java.util.function.UnaryOperator;

/**
 * A label element that displays and contains one single line of text
 */
public interface ILabelElement extends ITextElement, IProviderElement<ITextComponent> {

    ILabelElement setFitText(boolean fitText);

    boolean isFitText();

    ILabelElement setShadow(boolean value);

    boolean hasShadow();

    Style getStyle();

    ILabelElement setStyle(Style style);

    default ILabelElement actStyle(UnaryOperator<Style> action) {
        return setStyle(action.apply(getStyle()));
    }

}
