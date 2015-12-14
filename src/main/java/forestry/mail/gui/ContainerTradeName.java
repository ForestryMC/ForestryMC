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

import net.minecraft.entity.player.EntityPlayer;

import forestry.api.mail.IMailAddress;
import forestry.core.gui.ContainerTile;
import forestry.mail.tiles.TileTrader;

public class ContainerTradeName extends ContainerTile<TileTrader> {

	public ContainerTradeName(TileTrader tile) {
		super(tile);
	}

	public IMailAddress getAddress() {
		return tile.getAddress();
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (tile.isLinked()) {
			for (Object crafter : crafters) {
				if (crafter instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) crafter;
					tile.openGui(player);
				}
			}
		}
	}
}
