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
import forestry.energy.gadgets.EngineBronze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class ContainerEngineBronze extends ContainerLiquidTanks {
	protected final EngineBronze engine;

	public ContainerEngineBronze(InventoryPlayer player, EngineBronze engine) {
		super(engine, engine);

		this.engine = engine;
		this.addSlot(new SlotLiquidContainer(engine, EngineBronze.SLOT_CAN, 143, 40));

		int i;
		for (i = 0; i < 3; ++i)
			for (int var4 = 0; var4 < 9; ++var4)
				this.addSlot(new Slot(player, var4 + i * 9 + 9, 8 + var4 * 18, 84 + i * 18));

		for (i = 0; i < 9; ++i)
			this.addSlot(new Slot(player, i, 8 + i * 18, 142));

	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return engine.isUseableByPlayer(entityplayer);
	}

}
