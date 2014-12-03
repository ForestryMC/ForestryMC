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
package forestry.energy.gui;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotLiquidContainer;
import forestry.energy.gadgets.MachineGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerGenerator extends ContainerLiquidTanks {

	protected final MachineGenerator tile;

	public ContainerGenerator(InventoryPlayer player, MachineGenerator tile) {
		super(tile, tile);

		this.tile = tile;
		this.addSlot(new SlotLiquidContainer(tile, MachineGenerator.SLOT_CAN, 22, 38));

		for (int i = 0; i < 3; ++i)
			for (int var4 = 0; var4 < 9; ++var4)
				this.addSlot(new Slot(player, var4 + i * 9 + 9, 8 + var4 * 18, 84 + i * 18));

		for (int i = 0; i < 9; ++i)
			this.addSlot(new Slot(player, i, 8 + i * 18, 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tile.isUseableByPlayer(entityplayer);
	}

}
