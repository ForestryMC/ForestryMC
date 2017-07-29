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
import forestry.core.gui.widgets.SocketWidget;
import forestry.greenhouse.gui.widgets.WidgetCamouflageSlot;
import forestry.greenhouse.tiles.TileClimatiser;

public class GuiClimatiser extends GuiForestryTitled<ContainerClimatiser> {

	private final TileClimatiser tile;

	public GuiClimatiser(EntityPlayer player, TileClimatiser tile) {
		super(Constants.TEXTURE_PATH_GUI + "/climatiser.png", new ContainerClimatiser(player.inventory, tile), tile);

		this.tile = tile;

		//Add the camouflage slot
		widgetManager.add(new WidgetCamouflageSlot(widgetManager, guiLeft + 71, guiTop + 35, tile));
		widgetManager.add(new SocketWidget(widgetManager, guiLeft + 93, guiTop + 35, tile, 0));
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("climatiser");
	}
}
