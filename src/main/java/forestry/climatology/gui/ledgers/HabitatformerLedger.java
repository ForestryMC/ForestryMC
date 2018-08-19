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
package forestry.climatology.gui.ledgers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import forestry.api.climate.IClimateTransformer;
import forestry.climatology.ModuleClimatology;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.utils.Translator;

public class HabitatformerLedger extends Ledger {
	private final IClimateTransformer transformer;

	public HabitatformerLedger(LedgerManager manager, IClimateTransformer climateLogic) {
		super(manager, "habitatformer");
		maxHeight = 96;
		this.transformer = climateLogic;
	}

	@Override
	public void draw(int x, int y) {
		// Draw background
		drawBackground(x, y);
		y += 4;

		int xIcon = x + 3;
		int xBody = x + 10;
		int xHeader = x + 22;

		// Draw icon
		Minecraft minecraft = Minecraft.getMinecraft();
		GuiUtil.drawItemStack(minecraft.fontRenderer, new ItemStack(ModuleClimatology.getBlocks().habitatformer), xIcon, y);
		y += 4;

		if (!isFullyOpened()) {
			return;
		}

		y += drawHeader(Translator.translateToLocal("for.gui.habitatformer"), xHeader, y);
		y += 4;

		y += drawSubheader(Translator.translateToLocal("for.gui.habitatformer.range") + ':', xBody, y);
		y += 3;
		//y += drawText(StringUtil.floatAsPercent(transformer.getRangeModifier()), xBody, y);
		y += 3;

		y += drawSubheader(Translator.translateToLocal("for.gui.habitatformer.resources") + ':', xBody, y);
		y += 3;
		//y += drawText(StringUtil.floatAsPercent(transformer.getResourceModifier()), xBody, y);
		y += 3;

		y += drawSubheader(Translator.translateToLocal("for.gui.habitatformer.speed") + ':', xBody, y);
		y += 3;
		//drawText(StringUtil.floatAsPercent(transformer.getSpeedModifier()), xBody, y);
	}

	@Override
	public String getTooltip() {
		return "";
	}
}
