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

import net.minecraft.entity.player.InventoryPlayer;

import net.minecraftforge.fluids.IFluidTank;

import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.greenhouse.tiles.TileGreenhouseButterflyHatch;

public class ContainerGreenhouseButterflyHatch extends ContainerTile<TileGreenhouseButterflyHatch> {

	public ContainerGreenhouseButterflyHatch(InventoryPlayer playerInventory, TileGreenhouseButterflyHatch tile) {
		super(tile, playerInventory, 8, 84);
		
		addSlotToContainer(new SlotOutput(tile, 0, 60, 40));
		addSlotToContainer(new SlotOutput(tile, 1, 80, 27));
		addSlotToContainer(new SlotOutput(tile, 2, 101, 40));
		addSlotToContainer(new SlotOutput(tile, 3, 80, 53));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToCrafters(packet);
	}
	
	public IFluidTank getTank(int slot) {
		return tile.getMultiblockLogic().getController().getTankManager().getTank(slot);
	}

}
