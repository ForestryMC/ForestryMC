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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.fluids.FluidHelper;
import forestry.core.render.TextureManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

public class SlotLiquidContainer extends SlotForestry {

	private final boolean allowEmpty;
	private final Fluid[] fluids;

	public SlotLiquidContainer(IInventory iinventory, int slotIndex, int xPos, int yPos) {
		this(iinventory, slotIndex, xPos, yPos, false);
	}

	public SlotLiquidContainer(IInventory iinventory, int slotIndex, int xPos, int yPos, boolean allowEmpty, Fluid... fluids) {
		super(iinventory, slotIndex, xPos, yPos);

		this.allowEmpty = allowEmpty;
		this.fluids = fluids;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (allowEmpty)
			return FluidHelper.isEmptyContainer(stack);
		if (fluids.length > 0) {
			for (Fluid fluid : fluids)
				if (FluidHelper.containsFluid(stack, fluid))
					return true;
			return false;
		}
		return FluidHelper.isFilledContainer(stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBackgroundIconIndex() {
		if (allowEmpty)
			return TextureManager.getInstance().getDefault("slots/container");
		else
			return TextureManager.getInstance().getDefault("slots/liquid");
	}

}
