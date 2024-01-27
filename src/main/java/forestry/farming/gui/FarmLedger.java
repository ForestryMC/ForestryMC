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

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.utils.ResourceUtil;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

public class FarmLedger extends Ledger {
	private final IFarmLedgerDelegate delegate;

	public FarmLedger(LedgerManager ledgerManager, IFarmLedgerDelegate delegate) {
		super(ledgerManager, "farm");
		this.delegate = delegate;

		int titleHeight = StringUtil.getLineHeight(maxTextWidth, getTooltip());
		this.maxHeight = titleHeight + 110;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void draw(PoseStack transform, int y, int x) {

		// Draw background
		drawBackground(transform, y, x);
		y += 4;

		int xIcon = x + 3;
		int xBody = x + 10;
		int xHeader = x + 22;

		// Draw icon
		TextureAtlasSprite textureAtlasSprite = ResourceUtil.getBlockSprite("item/water_bucket");
		drawSprite(transform, textureAtlasSprite, xIcon, y, TextureAtlas.LOCATION_BLOCKS);
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
	public Component getTooltip() {
		float hydrationModifier = delegate.getHydrationModifier();
		return Component.literal(StringUtil.floatAsPercent(hydrationModifier) + ' ')
			.append(Component.translatable("for.gui.hydration"));
	}
}