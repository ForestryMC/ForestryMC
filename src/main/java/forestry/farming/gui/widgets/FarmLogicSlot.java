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
package forestry.farming.gui.widgets;

import net.minecraft.util.IIcon;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmLogic;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.farming.multiblock.IFarmControllerInternal;

public class FarmLogicSlot extends Widget {

	private final IFarmControllerInternal farmController;
	private final FarmDirection farmDirection;

	public FarmLogicSlot(IFarmControllerInternal farmController, WidgetManager manager, int xPos, int yPos, FarmDirection farmDirection) {
		super(manager, xPos, yPos);
		this.farmController = farmController;
		this.farmDirection = farmDirection;
	}

	private IFarmLogic getLogic() {
		return farmController.getFarmLogic(farmDirection);
	}

	private IIcon getIconIndex() {
		if (getLogic() == null) {
			return null;
		}
		return getLogic().getIcon();
	}

	@Override
	public void draw(int startX, int startY) {
		if (getIconIndex() != null) {
			Proxies.render.bindTexture(getLogic().getSpriteSheet());
			manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos, getIconIndex(), 16, 16);
		}
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		if (isMouseOver(mouseX, mouseY)) {
			return toolTip;
		} else {
			return null;
		}
	}

	protected final ToolTip toolTip = new ToolTip(250) {
		@Override
		public void refresh() {
			toolTip.clear();
			if (getLogic() == null) {
				return;
			}
			toolTip.add(getLogic().getName());
			toolTip.add("Fertilizer: " + getLogic().getFertilizerConsumption());
			toolTip.add("Water: " + getLogic().getWaterConsumption(farmController.getFarmLedgerDelegate().getHydrationModifier()));
		}
	};
}
