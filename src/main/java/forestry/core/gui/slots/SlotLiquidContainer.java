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
package forestry.core.gui.slots;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;

import forestry.core.render.TextureManager;

public class SlotLiquidContainer extends SlotCustom {

	private final boolean isEmpty;

	public SlotLiquidContainer(IInventory iinventory, int i, int j, int k) {
		this(iinventory, i, j, k, false);
	}

	public SlotLiquidContainer(IInventory iinventory, int i, int j, int k, boolean isEmpty) {
		super(iinventory, i, j, k, false);

		this.isEmpty = isEmpty;
		List<ItemStack> container = new ArrayList<ItemStack>();

		for (FluidContainerData cont : FluidContainerRegistry.getRegisteredFluidContainerData())
			if (isEmpty)
				container.add(cont.emptyContainer);
			else
				container.add(cont.filledContainer);

		this.items = container.toArray();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBackgroundIconIndex() {
		if(isEmpty)
			return TextureManager.getInstance().getDefault("slots/container");
		else
			return TextureManager.getInstance().getDefault("slots/liquid");
	}

}
