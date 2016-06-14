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
package forestry.factory.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import forestry.core.fluids.FluidHelper;
import forestry.core.inventory.InventoryAdapterRestricted;
import forestry.factory.multiblock.DistillVatController;

public class InventoryDistillVat extends InventoryAdapterRestricted {
	public static final short SLOT_PRODUCT = 0;
	public static final short SLOT_RESOURCE = 1;
	public static final short SLOT_CAN = 2;
	public static final int SLOT_COUNT = 3;
	private final DistillVatController distillVatcontroller;

	public InventoryDistillVat(DistillVatController distillVatcontroller) {
		super(SLOT_COUNT, "Items", distillVatcontroller.getAccessHandler());
		this.distillVatcontroller = distillVatcontroller;

	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_RESOURCE) {
			return FluidHelper.isFillableEmptyContainer(itemStack);
		} else if (slotIndex == SLOT_CAN) {
			FluidStack fluid = FluidUtil.getFluidContained(itemStack);
			return distillVatcontroller.getTankManager().canFillFluidType(fluid);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, EnumFacing side) {
		return slotIndex == SLOT_PRODUCT;
	}
}
