package forestry.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import forestry.api.core.GuiElementAlignment;
import forestry.core.render.ColourProperties;

public class GuiElementText extends GuiElement {
	public static final FontRenderer FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;

	protected final String text;
	protected final int color;
	protected final GuiElementAlignment align;

	public GuiElementText(int xPos, int yPos, int height, String text) {
		this(xPos, yPos, FONT_RENDERER.getStringWidth(text), height, text, GuiElementAlignment.LEFT);
	}

	public GuiElementText(int xPos, int yPos, int height, String text, int color) {
		this(xPos, yPos, FONT_RENDERER.getStringWidth(text), height, text, GuiElementAlignment.LEFT, color);
	}

	public GuiElementText(int xPos, int yPos, int width, int height, String text) {
		this(xPos, yPos, width, height, text, GuiElementAlignment.LEFT);
	}

	public GuiElementText(int xPos, int yPos, int width, int height, String text, GuiElementAlignment align) {
		this(xPos, yPos, width, height, text, align, ColourProperties.INSTANCE.get("gui.screen"));
	}

	public GuiElementText(String text, GuiElementAlignment align, int color) {
		this(0, 0, FONT_RENDERER.getStringWidth(text), 12, text, align, color);
	}

	public GuiElementText(int xPos, int yPos, int width, int height, String text, int color) {
		this(xPos, yPos, width, height, text, GuiElementAlignment.LEFT, color);
	}

	public GuiElementText(int xPos, int yPos, int width, int height, String text, GuiElementAlignment align, int color) {
		super(xPos, yPos, width, height);
		this.text = text;
		this.align = align;
		this.color = color;
	}

	public String getText() {
		return text;
	}

	@Override
	public void draw(int startX, int startY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		FONT_RENDERER.drawString(text, getX() + getOffset() + startX, getY() + startY, color);
	}

	public int getOffset() {
		switch (align) {
			case RIGHT:
				return width - FONT_RENDERER.getStringWidth(text);
			case CENTER:
				return (width - FONT_RENDERER.getStringWidth(text)) / 2;
			default:
				return 0;
		}
	}
}
