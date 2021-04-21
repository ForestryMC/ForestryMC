package forestry.core.gui.elements.text;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;

import com.mojang.blaze3d.matrix.MatrixStack;

class ProcessorText extends AbstractTextElement<IReorderingProcessor, ProcessorText> {

	public ProcessorText(IReorderingProcessor component) {
		super(component);
	}

	public ProcessorText(int xPos, int yPos, int width, int height, IReorderingProcessor component, boolean fitText) {
		super(xPos, yPos, width, height, component, fitText);
	}

	@Override
	public LabelElement setValue(Object text) {
		if (text instanceof IReorderingProcessor) {
			this.text = (IReorderingProcessor) text;
		}
		calculateWidth();
		return this;
	}

	@Override
	protected int calcWidth(FontRenderer font) {
		return font.width(text);
	}

	@Override
	public void drawElement(MatrixStack transform, int mouseX, int mouseY) {
		if (shadow) {
			FONT_RENDERER.drawShadow(transform, text, 0, 0, 0);
		} else {
			FONT_RENDERER.draw(transform, text, 0, 0, 0);
		}
	}
}
