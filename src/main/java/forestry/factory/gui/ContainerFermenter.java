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
import net.minecraft.inventory.IContainerListener;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotEmptyLiquidContainerIn;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.factory.inventory.InventoryFermenter;
import forestry.factory.tiles.TileFermenter;

public class ContainerFermenter extends ContainerLiquidTanks<TileFermenter> {

	public ContainerFermenter(InventoryPlayer player, TileFermenter fermenter) {
		super(fermenter, player, 8, 84);

		this.addSlotToContainer(new SlotFiltered(fermenter, InventoryFermenter.SLOT_RESOURCE, 85, 23));
		this.addSlotToContainer(new SlotFiltered(fermenter, InventoryFermenter.SLOT_FUEL, 75, 57));
		this.addSlotToContainer(new SlotOutput(fermenter, InventoryFermenter.SLOT_CAN_OUTPUT, 150, 58));
		this.addSlotToContainer(new SlotEmptyLiquidContainerIn(fermenter, InventoryFermenter.SLOT_CAN_INPUT, 150, 22));
		this.addSlotToContainer(new SlotLiquidIn(fermenter, InventoryFermenter.SLOT_INPUT, 10, 40));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int messageId, int data) {
		super.updateProgressBar(messageId, data);

		tile.getGUINetworkData(messageId, data);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (IContainerListener crafter : listeners) {
			tile.sendGUINetworkData(this, crafter);
		}
	}
}
