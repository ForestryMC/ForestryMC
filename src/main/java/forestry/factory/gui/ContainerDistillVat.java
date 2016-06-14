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
package forestry.factory.gui;

import net.minecraft.entity.player.InventoryPlayer;

import net.minecraftforge.fluids.IFluidTank;

import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotEmptyLiquidContainerIn;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.factory.inventory.InventoryStill;
import forestry.factory.multiblock.TileDistillVat;

public class ContainerDistillVat extends ContainerTile<TileDistillVat> {

	public ContainerDistillVat(InventoryPlayer player, TileDistillVat tile) {
		super(tile, player, 8, 84);

		this.addSlotToContainer(new SlotOutput(tile, InventoryStill.SLOT_PRODUCT, 150, 54));
		this.addSlotToContainer(new SlotEmptyLiquidContainerIn(tile, InventoryStill.SLOT_RESOURCE, 150, 18));
		this.addSlotToContainer(new SlotLiquidIn(tile, InventoryStill.SLOT_CAN, 10, 36));
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
