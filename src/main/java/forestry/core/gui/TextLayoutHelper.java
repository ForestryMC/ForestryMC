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

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import forestry.core.render.FontColour;

public class TextLayoutHelper {
	private static final int LINE_HEIGHT = 12;

	private final GuiForestry guiForestry;
	private final int defaultFontColor;
	private FontRenderer fontRendererObj;

	public int column0;
	public int column1;
	public int column2;
	public int line;

	public TextLayoutHelper(GuiForestry guiForestry, FontColour fontColour) {
		this.guiForestry = guiForestry;
		this.defaultFontColor = fontColour.get("gui.screen");
	}

	public void setFontRendererObj(FontRenderer fontRendererObj) {
		this.fontRendererObj = fontRendererObj;
	}
	
	public void startPage() {
		line = LINE_HEIGHT;
		GL11.glPushMatrix();
	}
	
	public void startPage(int column0, int column1, int column2) {

		this.column0 = column0;
		this.column1 = column1;
		this.column2 = column2;

		startPage();
	}
	
	public int getLineY() {
		return line;
	}
	
	public void newLine() {
		line += LINE_HEIGHT;
	}
	
	public void newLineCompressed() {
		line += (LINE_HEIGHT - 2);
	}
	
	public void newLine(int lineHeight) {
		line += lineHeight;
	}
	
	public void endPage() {
		GL11.glPopMatrix();
	}
	
	public void drawRow(String text0, String text1, String text2, int colour0, int colour1, int colour2) {
		drawLine(text0, column0, colour0);
		drawLine(text1, column1, colour1);
		drawLine(text2, column2, colour2);
	}
	
	public void drawLine(String text, int x) {
		drawLine(text, x, defaultFontColor);
	}
	
	public void drawSplitLine(String text, int x, int maxWidth) {
		drawSplitLine(text, x, maxWidth, defaultFontColor);
	}

	public void drawCenteredLine(String text, int x, int color) {
		drawCenteredLine(text, x, guiForestry.getSizeX(), color);
	}

	public void drawCenteredLine(String text, int x, int width, int color) {
		fontRendererObj.drawString(text, guiForestry.getGuiLeft() + x + getCenteredOffset(text, width), guiForestry.getGuiTop() + line, color);
	}

	public void drawLine(String text, int x, int color) {
		fontRendererObj.drawString(text, guiForestry.getGuiLeft() + x, guiForestry.getGuiTop() + line, color);
	}

	public void drawSplitLine(String text, int x, int maxWidth, int color) {
		fontRendererObj.drawSplitString(text, guiForestry.getGuiLeft() + x, guiForestry.getGuiTop() + line, maxWidth, color);
	}

	public int getCenteredOffset(String string) {
		return getCenteredOffset(string, guiForestry.getSizeX());
	}

	public int getCenteredOffset(String string, int xWidth) {
		return (xWidth - fontRendererObj.getStringWidth(string)) / 2;
	}
}
