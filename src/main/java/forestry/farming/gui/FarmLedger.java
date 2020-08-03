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
package forestry.farming.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.utils.ResourceUtil;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FarmLedger extends Ledger {
    private final IFarmLedgerDelegate delegate;

    public FarmLedger(LedgerManager ledgerManager, IFarmLedgerDelegate delegate) {
        super(ledgerManager, "farm");
        this.delegate = delegate;

        //TODO textcomponent
        int titleHeight = StringUtil.getLineHeight(maxTextWidth, getTooltip());
        this.maxHeight = titleHeight + 110;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(MatrixStack transform, int y, int x) {

        // Draw background
        drawBackground(transform, y, x);
        y += 4;

        int xIcon = x + 3;
        int xBody = x + 10;
        int xHeader = x + 22;

        // Draw icon
        TextureAtlasSprite textureAtlasSprite = ResourceUtil.getBlockSprite("item/water_bucket");
        drawSprite(transform, textureAtlasSprite, xIcon, y, AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        y += 4;

        if (!isFullyOpened()) {
            return;
        }

        y += drawHeader(transform, Translator.translateToLocal("for.gui.hydration"), xHeader, y);
        y += 4;

        y += drawSubheader(transform, Translator.translateToLocal("for.gui.hydr.heat") + ':', xBody, y);
        y += 3;
        y += drawText(transform, StringUtil.floatAsPercent(delegate.getHydrationTempModifier()), xBody, y);
        y += 3;

        y += drawSubheader(transform, Translator.translateToLocal("for.gui.hydr.humid") + ':', xBody, y);
        y += 3;
        y += drawText(transform, StringUtil.floatAsPercent(delegate.getHydrationHumidModifier()), xBody, y);
        y += 3;

        y += drawSubheader(transform, Translator.translateToLocal("for.gui.hydr.rainfall") + ':', xBody, y);
        y += 3;
        y += drawText(transform, StringUtil.floatAsPercent(delegate.getHydrationRainfallModifier()) + " (" + delegate.getDrought() + " d)", xBody, y);
        y += 3;

        y += drawSubheader(transform, Translator.translateToLocal("for.gui.hydr.overall") + ':', xBody, y);
        y += 3;
        drawText(transform, StringUtil.floatAsPercent(delegate.getHydrationModifier()), xBody, y);
    }

    @Override
    public ITextComponent getTooltip() {
        float hydrationModifier = delegate.getHydrationModifier();
        return new StringTextComponent(StringUtil.floatAsPercent(hydrationModifier) + ' ')
                .append(new TranslationTextComponent("for.gui.hydration"));
    }
}