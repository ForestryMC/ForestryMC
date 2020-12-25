/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.core.render.ColourProperties;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextLayoutHelper {
    private static final int LINE_HEIGHT = 12;

    private final GuiForestry guiForestry;
    private final int defaultFontColor;

    public int column0;
    public int column1;
    public int column2;
    public int line;

    public TextLayoutHelper(GuiForestry guiForestry, ColourProperties fontColour) {
        this.guiForestry = guiForestry;
        this.defaultFontColor = fontColour.get("gui.screen");
    }

    public void startPage() {
        line = LINE_HEIGHT;
        RenderSystem.pushMatrix();
    }

    public void startPage(int column0, int column1) {
        startPage(column0, column1, 0);
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
        line += LINE_HEIGHT - 2;
    }

    public void newLine(int lineHeight) {
        line += lineHeight;
    }

    public void endPage() {
        RenderSystem.popMatrix();
    }

    public void drawRow(
            MatrixStack transform,
            ITextComponent text0,
            ITextComponent text1,
            ITextComponent text2,
            int colour0,
            int colour1,
            int colour2
    ) {
        drawLine(transform, text0, column0, colour0);
        drawLine(transform, text1, column1, colour1);
        drawLine(transform, text2, column2, colour2);
    }

    public void drawLine(MatrixStack transform, ITextComponent text, int x) {
        drawLine(transform, text, x, defaultFontColor);
    }

    public void drawSplitLine(ITextComponent text, int x, int maxWidth) {
        drawSplitLine(text, x, maxWidth, defaultFontColor);
    }

    public void drawCenteredLine(MatrixStack transform, ITextComponent text, int x, int color) {
        drawCenteredLine(transform, text, x, guiForestry.getSizeX(), color);
    }

    public void drawCenteredLine(MatrixStack transform, ITextComponent text, int x, int width, int color) {
        drawCenteredLine(transform, text, x, 0, width, color);
    }

    public void drawCenteredLine(MatrixStack transform, ITextComponent text, int x, int y, int width, int color) {
        guiForestry.getFontRenderer().func_243248_b(
                transform,
                text,
                guiForestry.getGuiLeft() + x + getCenteredOffset(text, width),
                guiForestry.getGuiTop() + y + line,
                color
        );
    }

    public void drawLine(MatrixStack transform, ITextComponent text, int x, int color) {
        drawLine(transform, text, x, 0, color);
    }

    public void drawLine(MatrixStack transform, ITextComponent text, int x, int y, int color) {
        guiForestry.getFontRenderer().func_243248_b(
                transform,
                text,
                guiForestry.getGuiLeft() + x,
                guiForestry.getGuiTop() + y + line,
                color
        );
    }

    public void drawSplitLine(ITextComponent text, int x, int maxWidth, int color) {
        guiForestry.getFontRenderer().func_238418_a_(
                text,
                guiForestry.getGuiLeft() + x,
                guiForestry.getGuiTop() + line,
                maxWidth,
                color
        );
    }

    public int getCenteredOffset(ITextComponent string) {
        return getCenteredOffset(string, guiForestry.getSizeX());
    }

    public int getCenteredOffset(ITextComponent string, int xWidth) {
        return (xWidth - guiForestry.getFontRenderer().getStringWidth(string.getString())) / 2;
    }
}
