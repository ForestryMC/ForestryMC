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
package forestry.greenhouse.gui;

import net.minecraft.entity.player.EntityPlayer;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.greenhouse.gui.widgets.WidgetCamouflageSlot;
import forestry.greenhouse.inventory.ItemInventoryCamouflageSprayCan;

public class GuiCamouflageSprayCan extends GuiForestryTitled<ContainerCamouflageSprayCan> {

	public GuiCamouflageSprayCan(EntityPlayer player, ItemInventoryCamouflageSprayCan inventory) {
		super(Constants.TEXTURE_PATH_GUI + "/camouflage_spray_can.png", new ContainerCamouflageSprayCan(inventory, player.inventory), inventory);

		//Add the camouflage slot
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 80, 39, inventory));
	}

	@Override
	protected void addLedgers() {
	}

}
