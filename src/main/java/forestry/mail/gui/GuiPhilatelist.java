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
package forestry.mail.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.mail.tiles.TilePhilatelist;

public class GuiPhilatelist extends GuiForestry<ContainerPhilatelist, TilePhilatelist> {

	public GuiPhilatelist(InventoryPlayer player, TilePhilatelist tile) {
		super(Constants.TEXTURE_PATH_GUI + "/philatelist.png", new ContainerPhilatelist(player, tile), tile);
		this.xSize = 176;
		this.ySize = 193;
	}

}
