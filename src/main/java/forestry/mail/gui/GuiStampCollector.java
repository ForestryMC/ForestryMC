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

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.mail.tiles.TileStampCollector;

public class GuiStampCollector extends GuiForestry<ContainerStampCollector> {
	private final TileStampCollector tile;

	public GuiStampCollector(ContainerStampCollector container, Inventory inv, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/philatelist.png", container, inv, title);
		this.tile = container.getTile();
		this.imageWidth = 176;
		this.imageHeight = 193;
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("philatelist");
	}
}
