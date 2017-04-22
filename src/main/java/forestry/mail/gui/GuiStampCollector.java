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

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.mail.tiles.TileStampCollector;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiStampCollector extends GuiForestry<ContainerStampCollector> {
	private final TileStampCollector tile;

	public GuiStampCollector(InventoryPlayer player, TileStampCollector tile) {
		super(Constants.TEXTURE_PATH_GUI + "/philatelist.png", new ContainerStampCollector(player, tile));
		this.tile = tile;
		this.xSize = 176;
		this.ySize = 193;
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("philatelist");
	}
}
