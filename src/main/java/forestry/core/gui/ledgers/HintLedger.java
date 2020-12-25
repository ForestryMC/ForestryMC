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
package forestry.core.gui.ledgers;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.render.TextureManagerForestry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.Random;

public class HintLedger extends Ledger {

    private final ITextComponent hintString;
    private final ITextComponent hintTooltip;

    public HintLedger(LedgerManager manager, List<String> hints) {
        super(manager, "hint");
        int position = new Random().nextInt(hints.size());
        String hint = hints.get(position);

        hintString = new TranslationTextComponent("for.hints." + hint + ".desc");
        hintTooltip = new TranslationTextComponent("for.hints." + hint + ".tag");

        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontRenderer = minecraft.fontRenderer;

        int lineCount = fontRenderer.trimStringToWidth(hintString, maxTextWidth).size();
        maxHeight = (lineCount + 1) * fontRenderer.FONT_HEIGHT + 20;
    }

    @Override
    public void draw(MatrixStack transform, int y, int x) {

        // Draw background
        drawBackground(transform, y, x);

        // Draw icon
        drawSprite(transform, TextureManagerForestry.getInstance().getDefault("misc/hint"), x + 3, y + 4);

        if (!isFullyOpened()) {
            return;
        }

        drawHeader(transform, new TranslationTextComponent("for.gui.didyouknow").appendString("?"), x + 22, y + 8);
        drawSplitText(transform, hintString, x + 12, y + 20, maxTextWidth);
    }

    @Override
    public ITextComponent getTooltip() {
        return hintTooltip;
    }
}
