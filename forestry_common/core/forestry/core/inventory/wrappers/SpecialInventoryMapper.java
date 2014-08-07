/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.inventory.wrappers;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.inventory.ISpecialInventory;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SpecialInventoryMapper extends InventoryMapper implements ISpecialInventory
{

	private final ISpecialInventory inv;
	protected final ForgeDirection side;

	public SpecialInventoryMapper(ISpecialInventory inv, ForgeDirection side)
	{
		super(inv, side);
		this.inv = inv;
		this.side = side;
	}

	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from)
	{
		return inv.addItem(stack, doAdd, side);
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount)
	{
		return inv.extractItem(doRemove, side, maxItemCount);
	}
}
