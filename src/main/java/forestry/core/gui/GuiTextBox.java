/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextBox extends GuiTextField {
	private static final int enabledColor = 14737632;
	private static final int disabledColor = 7368816;

	private final FontRenderer fontRenderer;
	private final int startX, startY, width, height;

	private int lineScroll = 0;
	private int maxLines = 0;

	public GuiTextBox(int ID, FontRenderer fontRenderer, int startX, int startY, int width, int height) {
		super(ID, fontRenderer, startX, startY, width, height);
		this.fontRenderer = fontRenderer;
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
	}

	private int getLineScrollOffset() {
		return 0;
	}

	public void advanceLine() {
		if (lineScroll < maxLines - 1) {
			lineScroll++;
		}
	}

	public void regressLine() {
		if (lineScroll > 0) {
			lineScroll--;
		}
	}

	public boolean moreLinesAllowed() {
		return fontRenderer.listFormattedStringToWidth(getCursoredText(), width).size() * fontRenderer.FONT_HEIGHT < height;
	}

	private String getCursoredText() {
		if (!isFocused()) {
			return getText();
		}

		int cursorPos = getCursorPosition() - getLineScrollOffset();
		String text = getText();
		if (cursorPos < 0) {
			return text;
		}
		if (cursorPos >= text.length()) {
			return text + "_";
		}
		return text.substring(0, cursorPos) + "_" + text.substring(cursorPos);
	}

	private void drawScrolledSplitString(String text, int startX, int startY, int width, int textColour) {
		List<String> lines = fontRenderer.listFormattedStringToWidth(text, width);
		maxLines = lines.size();

		int count = 0;
		int lineY = startY;

		for (String line : lines) {
			if (count < lineScroll) {
				count++;
				continue;
			} else if (lineY + fontRenderer.FONT_HEIGHT - startY > height) {
				break;
			}

			fontRenderer.drawString(line, startX, lineY, textColour);
			lineY += fontRenderer.FONT_HEIGHT;

			count++;
		}

	}

	@Override
	public void drawTextBox() {
		if (!getVisible()) {
			return;
		}

		if (getEnableBackgroundDrawing()) {
			drawRect(startX - 1, startY - 1, startX + this.width + 1, startY + this.height + 1, -6250336);
			drawRect(startX, startY, startX + this.width, startY + this.height, -16777216);
		}

		int textColour = isFocused() ? enabledColor : disabledColor;

		drawScrolledSplitString(getCursoredText(), startX + 2, startY + 2, width - 4, textColour);
	}

}
