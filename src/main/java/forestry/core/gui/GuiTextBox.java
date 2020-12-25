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

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class GuiTextBox extends TextFieldWidget {
    private static final int enabledColor = 14737632;
    private static final int disabledColor = 7368816;

    private final FontRenderer fontRenderer;
    private final int startX, startY, width, height;

    private int lineScroll = 0;
    private int maxLines = 0;

    public GuiTextBox(FontRenderer fontRenderer, int startX, int startY, int width, int height) {
        super(fontRenderer, startX, startY, width, height, null);
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
        return fontRenderer.trimStringToWidth(new StringTextComponent(getCursoredText()), width)
                           .size() * fontRenderer.FONT_HEIGHT < height;
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

    private void drawScrolledSplitString(
            MatrixStack transform,
            ITextComponent text,
            int startX,
            int startY,
            int width,
            int textColour
    ) {
        List<IReorderingProcessor> lines = fontRenderer.trimStringToWidth(text, width);
        maxLines = lines.size();

        int count = 0;
        int lineY = startY;

        for (IReorderingProcessor line : lines) {
            if (count < lineScroll) {
                count++;
                continue;
            } else if (lineY + fontRenderer.FONT_HEIGHT - startY > height) {
                break;
            }

            fontRenderer.func_238422_b_(transform, line, startX, lineY, textColour);
            lineY += fontRenderer.FONT_HEIGHT;

            count++;
        }
    }

    //TODO gui, rendering, I have no idea where these methods have gone
    //	@Override
    //	public void drawTextBox() {
    //		if (!getVisible()) {
    //			return;
    //		}
    //
    //		if (getEnableBackgroundDrawing()) {	//TODO AT
    //			drawRect(startX - 1, startY - 1, startX + this.width + 1, startY + this.height + 1, -6250336);
    //			drawRect(startX, startY, startX + this.width, startY + this.height, -16777216);
    //		}
    //
    //		int textColour = isFocused() ? enabledColor : disabledColor;
    //
    //		drawScrolledSplitString(getCursoredText(), startX + 2, startY + 2, width - 4, textColour);
    //	}

}
