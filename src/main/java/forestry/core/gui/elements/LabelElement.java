package forestry.core.gui.elements;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.ILabelElement;
import forestry.api.gui.style.ITextStyle;
import forestry.core.utils.GuiElementUtil;

public class LabelElement extends GuiElement implements ILabelElement {
	/* Constants */
	public static final FontRenderer FONT_RENDERER = Minecraft.getInstance().fontRenderer;

	/* Attributes - State */
	protected ITextStyle style;
	protected String text;
	protected String rawText;
	protected boolean textLength = false;

	public LabelElement(String text, GuiElementAlignment align, ITextStyle style) {
		this(0, 0, -1, FONT_RENDERER.FONT_HEIGHT, text, align, style);
	}

	//TODO reduce code duplication in this class
	public LabelElement(int xPos, int yPos, int width, int height, String text, GuiElementAlignment align, ITextStyle style) {
		super(xPos, yPos, width, height);
		textLength = width < 0;
		this.style = style;
		this.rawText = text;
		this.text = GuiElementUtil.getFormattedString(style, text);
		setAlign(align);
		if (textLength) {
			//TODO - think this is bidi flag
			boolean uni = FONT_RENDERER.getBidiFlag();
			FONT_RENDERER.setBidiFlag(style.isUnicode());
			setWidth(FONT_RENDERER.getStringWidth(this.text));
			FONT_RENDERER.setBidiFlag(uni);
		}
	}

	@Override
	public ILabelElement setStyle(ITextStyle style) {
		this.style = style;
		this.text = GuiElementUtil.getFormattedString(style, rawText);
		if (textLength) {
			//TODO - think this is bidi now
			boolean uni = FONT_RENDERER.getBidiFlag();
			FONT_RENDERER.setBidiFlag(style.isUnicode());
			setWidth(FONT_RENDERER.getStringWidth(this.text));
			FONT_RENDERER.setBidiFlag(uni);
		}
		return this;
	}

	@Override
	public ITextStyle getStyle() {
		return style;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public ILabelElement setText(String text) {
		this.rawText = text;
		this.text = GuiElementUtil.getFormattedString(style, text);
		if (textLength) {
			//TODO think this is bidi now
			boolean uni = FONT_RENDERER.getBidiFlag();
			FONT_RENDERER.setBidiFlag(style.isUnicode());
			setWidth(FONT_RENDERER.getStringWidth(this.text));
			FONT_RENDERER.setBidiFlag(uni);
		}
		return this;
	}

	@Override
	public String getRawText() {
		return rawText;
	}

	@Override
	public Collection<String> getLines() {
		return Collections.singletonList(text);
	}

	@Override
	public Map<ITextStyle, String> getRawLines() {
		return ImmutableMap.of(style, rawText);
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		//TODO think this is bidi now
		boolean unicode = FONT_RENDERER.getBidiFlag();
		FONT_RENDERER.setBidiFlag(style.isUnicode());
		FONT_RENDERER.drawString(text, 0, 0, style.getColor());
		FONT_RENDERER.setBidiFlag(unicode);
	}
}
