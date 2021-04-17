package forestry.core.gui.elements;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import forestry.core.gui.elements.lib.ITextElement;

import static forestry.core.gui.elements.LabelElement.FONT_RENDERER;

public class SplitTextElement extends GuiElement implements ITextElement {

	private final List<IReorderingProcessor> lines = new ArrayList<>();

	public SplitTextElement(int xPos, int yPos, int width, IFormattableTextComponent component, Style style) {
		super(xPos, yPos, width, 0);
		setText(component.withStyle(style));
		setHeight(lines.size() * FONT_RENDERER.lineHeight);
	}

	@Override
	public ITextElement setText(ITextComponent text) {
		lines.clear();
		lines.addAll(FONT_RENDERER.split(text, width));
		setHeight(lines.size() * FONT_RENDERER.lineHeight);
		return this;
	}

	@Override
	public void drawElement(MatrixStack transform, int mouseX, int mouseY) {
		int posY = 0;
		for (IReorderingProcessor text : lines) {
			int posX = width - FONT_RENDERER.width(text);
			posX *= getAlign().getXOffset();
			FONT_RENDERER.draw(transform, text, posX, posY, 0);
			posY += FONT_RENDERER.lineHeight;
		}
		RenderSystem.color3f(1.0f, 1.0f, 1.0f);
	}
}
