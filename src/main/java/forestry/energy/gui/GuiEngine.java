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
/**
 *
 */
package forestry.energy.gui;

import net.minecraft.inventory.Container;

import forestry.core.gadgets.Engine;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.Ledger;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public abstract class GuiEngine<C extends Container, I extends Engine> extends GuiForestryTitled<C, I> {

	protected class EngineLedger extends Ledger {

		public EngineLedger() {
			super(ledgerManager);
			maxHeight = 94;
			overlayColor = fontColor.get("ledger.power.background");
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			drawIcon(TextureManager.getInstance().getDefault("misc/energy"), x + 3, y + 4);

			if (!isFullyOpened()) {
				return;
			}

			fontRendererObj.drawStringWithShadow(StringUtil.localize("gui.energy"), x + 22, y + 8, fontColor.get("ledger.power.header"));
			fontRendererObj.drawStringWithShadow(StringUtil.localize("gui.currentOutput") + ":", x + 22, y + 20, fontColor.get("ledger.power.subheader"));
			fontRendererObj.drawString(inventory.getCurrentOutput() + " RF/t", x + 22, y + 32, fontColor.get("ledger.power.text"));
			fontRendererObj.drawStringWithShadow(StringUtil.localize("gui.stored") + ":", x + 22, y + 44, fontColor.get("ledger.power.subheader"));
			fontRendererObj.drawString(inventory.getEnergyManager().getEnergyStored(inventory.getOrientation()) + " RF", x + 22, y + 56, fontColor.get("ledger.power.text"));
			fontRendererObj.drawStringWithShadow(StringUtil.localize("gui.heat") + ":", x + 22, y + 68, fontColor.get("ledger.power.subheader"));
			fontRendererObj.drawString((((double) inventory.getHeat() / (double) 10) + 20.0) + " C", x + 22, y + 80, fontColor.get("ledger.power.text"));
		}

		@Override
		public String getTooltip() {
			return inventory.getCurrentOutput() + " RF/t";
		}
	}

	public GuiEngine(String texture, C container, I tile) {
		super(texture, container, tile);
	}

	@Override
	protected void initLedgers() {
		super.initLedgers();
		ledgerManager.insert(new EngineLedger());
	}
}
