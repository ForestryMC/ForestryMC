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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;

import forestry.api.climate.IClimateListener;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.core.climate.ClimateRoot;
import forestry.core.gui.ContainerTile;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.packets.PacketGuiUpdate;

public class ContainerAlveary extends ContainerTile<TileAlveary> {

	public ContainerAlveary(InventoryPlayer player, TileAlveary tile) {
		super(tile, player, 8, 108);
		ContainerBeeHelper.addSlots(this, tile, false);

		tile.getBeekeepingLogic().clearCachedValues();
		IClimateListener listener = ClimateRoot.getInstance().getListener(tile.getWorld(), tile.getPos());
		if (listener != null && player.player instanceof EntityPlayerMP) {
			listener.syncToClient((EntityPlayerMP) player.player);
		}
	}

	private int beeProgress = -1;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		int beeProgress = tile.getBeekeepingLogic().getBeeProgressPercent();
		if (this.beeProgress != beeProgress) {
			this.beeProgress = beeProgress;
			IForestryPacketClient packet = new PacketGuiUpdate(tile);
			sendPacketToListeners(packet);
		}
	}
}
