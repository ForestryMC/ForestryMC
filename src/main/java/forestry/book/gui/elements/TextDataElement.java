package forestry.book.gui.elements;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.book.data.TextData;
import forestry.core.gui.elements.GuiElement;

@SideOnly(Side.CLIENT)
public class TextDataElement extends GuiElement {

	private final List<TextData> textElements = new ArrayList<>();

	public TextDataElement(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	@Override
	public int getHeight() {
		if (height >= 0) {
			return height;
		}
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		boolean unicode = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(true);
		boolean lastEmpty = false;
		for (TextData data : textElements) {
			if (data.text.equals("\n")) {
				if (lastEmpty) {
					height += fontRenderer.FONT_HEIGHT;
				}
				lastEmpty = true;
				continue;
			}
			lastEmpty = false;

			if (data.paragraph) {
				height += fontRenderer.FONT_HEIGHT * 1.6D;
			}

			String modifiers = "";

			modifiers += TextFormatting.getValueByName(data.color);

			if (data.bold) {
				modifiers += TextFormatting.BOLD;
			}
			if (data.italic) {
				modifiers += TextFormatting.ITALIC;
			}
			if (data.underlined) {
				modifiers += TextFormatting.UNDERLINE;
			}
			if (data.strikethrough) {
				modifiers += TextFormatting.STRIKETHROUGH;
			}
			if (data.obfuscated) {
				modifiers += TextFormatting.OBFUSCATED;
			}
			height += fontRenderer.getWordWrappedHeight(modifiers + data.text, width);
		}
		fontRenderer.setUnicodeFlag(unicode);
		return height;
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		boolean unicode = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(true);
		int x = 0;
		int y = 0;
		for (TextData data : textElements) {
			if (data.text.equals("\n")) {
				x = 0;
				y += fontRenderer.FONT_HEIGHT;
				continue;
			}

			if (data.paragraph) {
				x = 0;
				y += fontRenderer.FONT_HEIGHT * 1.6D;
			}

			String text = getFormattedString(data);
			List<String> split = fontRenderer.listFormattedStringToWidth(text, width);
			for (int i = 0; i < split.size(); i++) {
				String s = split.get(i);
				int textLength = fontRenderer.drawString(s, x, y, 0, data.dropshadow);
				if (i == split.size() - 1) {
					x += textLength;
				} else {
					y += fontRenderer.FONT_HEIGHT;
				}
			}
		}
		fontRenderer.setUnicodeFlag(unicode);
	}

	private String getFormattedString(TextData data) {
		StringBuilder modifiers = new StringBuilder();

		modifiers.append(TextFormatting.getValueByName(data.color));

		if (data.bold) {
			modifiers.append(TextFormatting.BOLD);
		}
		if (data.italic) {
			modifiers.append(TextFormatting.ITALIC);
		}
		if (data.underlined) {
			modifiers.append(TextFormatting.UNDERLINE);
		}
		if (data.strikethrough) {
			modifiers.append(TextFormatting.STRIKETHROUGH);
		}
		if (data.obfuscated) {
			modifiers.append(TextFormatting.OBFUSCATED);
		}
		modifiers.append(data.text);
		return modifiers.toString();
	}

	public void addData(TextData textData) {
		textElements.add(textData);
		setHeight(-1);
	}
}
