package forestry.core.gui.elements;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.GlStateManager;

import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.ITextElement;
import forestry.api.gui.style.ITextStyle;
import forestry.core.utils.GuiElementUtil;

import static forestry.core.gui.elements.LabelElement.FONT_RENDERER;

public class SplitTextElement extends GuiElement implements ITextElement {

	private List<String> lines = new ArrayList<>();
	private String rawText;
	private ITextStyle style;

	public SplitTextElement(int xPos, int yPos, int width, String rawText, GuiElementAlignment align, ITextStyle style) {
		super(xPos, yPos, width, 0);
		this.rawText = rawText;
		this.style = style;
		setAlign(align);
		boolean uni = FONT_RENDERER.getUnicodeFlag();
		FONT_RENDERER.setUnicodeFlag(style.isUnicode());
		this.lines.addAll(FONT_RENDERER.listFormattedStringToWidth(GuiElementUtil.getFormattedString(style, rawText), width));
		FONT_RENDERER.setUnicodeFlag(uni);
		setHeight(lines.size() * FONT_RENDERER.FONT_HEIGHT);
	}

	@Override
	public Collection<String> getLines() {
		return lines;
	}

	@Override
	public ITextElement setText(String text) {
		this.rawText = text;
		boolean uni = FONT_RENDERER.getUnicodeFlag();
		FONT_RENDERER.setUnicodeFlag(style.isUnicode());
		lines.clear();
		lines.addAll(FONT_RENDERER.listFormattedStringToWidth(GuiElementUtil.getFormattedString(style, rawText), width));
		FONT_RENDERER.setUnicodeFlag(uni);
		setHeight(lines.size() * FONT_RENDERER.FONT_HEIGHT);
		return this;
	}

	@Override
	public Map<ITextStyle, String> getRawLines() {
		return ImmutableMap.of(style, rawText);
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		boolean unicode = FONT_RENDERER.getUnicodeFlag();
		FONT_RENDERER.setUnicodeFlag(style.isUnicode());
		int posY = 0;
		for (String text : lines) {
			int posX = width - FONT_RENDERER.getStringWidth(text);
			posX *= getAlign().getXOffset();
			FONT_RENDERER.drawString(text, posX, posY, style.getColor());
			posY += FONT_RENDERER.FONT_HEIGHT;
		}
		FONT_RENDERER.setUnicodeFlag(unicode);
		GlStateManager.color(1.0f, 1.0f, 1.0f);
	}
}
