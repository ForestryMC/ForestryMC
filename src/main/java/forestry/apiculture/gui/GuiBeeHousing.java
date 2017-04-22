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
package forestry.apiculture.gui;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.render.EnumTankLevel;
import net.minecraft.inventory.Container;

public class GuiBeeHousing<C extends Container & IContainerBeeHousing> extends GuiForestryTitled<C> {
	private final IGuiBeeHousingDelegate delegate;

	public enum Icon {
		APIARY("/apiary.png"),
		BEE_HOUSE("/alveary.png");

		private final String path;

		Icon(String path) {
			this.path = path;
		}
	}

	public GuiBeeHousing(IGuiBeeHousingDelegate delegate, C container, Icon icon) {
		super(Constants.TEXTURE_PATH_GUI + icon.path, container, delegate);
		this.delegate = delegate;
		this.ySize = 190;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		drawHealthMeter(guiLeft + 20, guiTop + 37, delegate.getHealthScaled(46), EnumTankLevel.rateTankLevel(delegate.getHealthScaled(100)));
	}

	private void drawHealthMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176 + rated.getLevelScaled(16);
		int k = 0;

		this.drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(delegate);
		addClimateLedger(delegate);
		addHintLedger(delegate.getHintKey());
		addOwnerLedger(delegate);
	}
}
