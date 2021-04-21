package forestry.core.gui.elements.text;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;

class ComponentText extends AbstractTextElement<ITextComponent, ComponentText> {

	public ComponentText(ITextComponent component) {
		super(component);
	}

	public ComponentText(int xPos, int yPos, int width, int height, ITextComponent component, boolean fitText) {
		super(xPos, yPos, width, height, component, fitText);
	}

	@Override
	public LabelElement setValue(Object text) {
		if (text instanceof ITextComponent) {
			this.text = (ITextComponent) text;
		} else if (text instanceof String) {
			this.text = new StringTextComponent((String) text);
		}
		calculateWidth();
		return this;
	}

	@Override
	public void drawElement(MatrixStack transform, int mouseX, int mouseY) {
		if (shadow) {
			FONT_RENDERER.drawShadow(transform, text, 0, 0, 0);
		} else {
			FONT_RENDERER.draw(transform, text, 0, 0, 0);
		}
	}

	@Override
	protected int calcWidth(FontRenderer font) {
		return font.width(text);
	}

}
