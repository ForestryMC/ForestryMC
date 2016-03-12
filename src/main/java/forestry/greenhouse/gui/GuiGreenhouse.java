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
import forestry.core.gui.widgets.TankWidget;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouse;

public class GuiGreenhouse extends GuiForestryTitled<ContainerGreenhouse, TileGreenhouse> {

	public GuiGreenhouse(EntityPlayer player, TileGreenhouse tile) {
		super(Constants.TEXTURE_PATH_GUI + "/greenhouse.png", new ContainerGreenhouse(player.inventory, tile), tile);

		widgetManager.add(new TankWidget(widgetManager, 80, 14, 0).setOverlayOrigin(176, 0));
	}

	@Override
	protected void addLedgers() {
		IGreenhouseControllerInternal greenhouseController = inventory.getMultiblockLogic().getController();
		ledgerManager.add(new ClimateLedger(ledgerManager, greenhouseController));
		super.addLedgers();
	}
}
