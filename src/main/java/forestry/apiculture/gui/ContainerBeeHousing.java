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

import net.minecraft.entity.player.InventoryPlayer;

import forestry.apiculture.tiles.TileBeeHousingBase;
import forestry.core.gui.ContainerTile;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.packets.PacketGuiUpdate;

public class ContainerBeeHousing extends ContainerTile<TileBeeHousingBase> implements IContainerBeeHousing {

	public ContainerBeeHousing(InventoryPlayer player, TileBeeHousingBase tile, boolean hasFrames) {
		super(tile, player, 8, 108);
		ContainerBeeHelper.addSlots(this, tile, hasFrames);
	}

	private int beeProgress = 0;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		int beeProgress = tile.getBeekeepingLogic().getBeeProgressPercent();
		if (this.beeProgress != beeProgress) {
			this.beeProgress = beeProgress;
			IForestryPacketClient packet = new PacketGuiUpdate(tile);
			sendPacketToCrafters(packet);
		}
	}

}
