/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
