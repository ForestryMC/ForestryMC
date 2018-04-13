package forestry.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import forestry.api.gui.GuiElementAlignment;

public class TextElement extends GuiElement {
	/* Constants */
	public static final FontRenderer FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;

	/* Attributes - Final */
	protected final String text;
	protected final int color;
	protected final boolean unicode;

	public TextElement(int width, int height, String text, GuiElementAlignment align, int color, boolean unicode) {
		this(0, 0, width, height, text, align, color, unicode);
	}

	public TextElement(int xPos, int yPos, int width, int height, String text, GuiElementAlignment align, int color, boolean unicode) {
		super(xPos, yPos, width, height);
		this.text = text;
		setAlign(align);
		this.color = color;
		this.unicode = unicode;
		if (width < 0) {
			boolean uni = FONT_RENDERER.getUnicodeFlag();
			FONT_RENDERER.setUnicodeFlag(this.unicode);
			this.width = FONT_RENDERER.getStringWidth(text);
			FONT_RENDERER.setUnicodeFlag(uni);
		}
	}

	public String getText() {
		return text;
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		boolean unicode = FONT_RENDERER.getUnicodeFlag();
		FONT_RENDERER.setUnicodeFlag(this.unicode);
		FONT_RENDERER.drawString(text, 0, 0, color);
		FONT_RENDERER.setUnicodeFlag(unicode);
	}
}
