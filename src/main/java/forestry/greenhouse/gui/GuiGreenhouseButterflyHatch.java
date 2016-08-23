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
import forestry.core.gui.ledgers.ClimateLedger;
import forestry.core.gui.widgets.WidgetCamouflageSlot;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouseButterflyHatch;

public class GuiGreenhouseButterflyHatch extends GuiForestryTitled<ContainerGreenhouseButterflyHatch, TileGreenhouseButterflyHatch> {

	public GuiGreenhouseButterflyHatch(EntityPlayer player, TileGreenhouseButterflyHatch tile) {
		super(Constants.TEXTURE_PATH_GUI + "/greenhouse_butterfly_hatch.png", new ContainerGreenhouseButterflyHatch(player.inventory, tile), tile);
		
		//Add the tile camouflage slots
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 8, 17, inventory, tile.getCamouflageType()));
	}

	@Override
	protected void addLedgers() {
		IGreenhouseControllerInternal greenhouseController = inventory.getMultiblockLogic().getController();
		
		ledgerManager.add(new ClimateLedger(ledgerManager, greenhouseController));
		super.addLedgers();
	}
}
