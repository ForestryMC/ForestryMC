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

import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.greenhouse.tiles.TileGreenhouse;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.IFluidTank;

public class ContainerGreenhouse extends ContainerTile<TileGreenhouse> {

	public ContainerGreenhouse(InventoryPlayer playerInventory, TileGreenhouse tile) {
		super(tile, playerInventory, 8, 84);

		addSlotToContainer(new SlotLiquidIn(tile, 0, 125, 37));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		PacketGuiUpdate packet = new PacketGuiUpdate(tile);
		sendPacketToListeners(packet);
	}

	public IFluidTank getTank(int slot) {
		return tile.getMultiblockLogic().getController().getTankManager().getTank(slot);
	}

}
