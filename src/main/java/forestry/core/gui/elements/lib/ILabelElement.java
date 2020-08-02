package forestry.core.gui.elements.lib;

import java.util.function.UnaryOperator;

import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;

import forestry.core.gui.elements.IProviderElement;

/**
 * A label element that displays and contains one single line of text
 */
public interface ILabelElement extends ITextElement, IProviderElement<ITextProperties> {

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
