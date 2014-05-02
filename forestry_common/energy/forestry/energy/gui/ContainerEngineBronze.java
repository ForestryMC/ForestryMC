/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotLiquidContainer;
import forestry.energy.gadgets.EngineBronze;

public class ContainerEngineBronze extends ContainerLiquidTanks {
	protected EngineBronze engine;

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

	@Override
	public void updateProgressBar(int i, int j) {
		engine.getGUINetworkData(i, j);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
			engine.sendGUINetworkData(this, (ICrafting) crafters.get(i));
	}

}
