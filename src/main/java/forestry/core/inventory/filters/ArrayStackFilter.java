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
package forestry.core.inventory.filters;

import net.minecraft.item.ItemStack;

import forestry.core.utils.InventoryUtil;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ArrayStackFilter implements IStackFilter {

	private final ItemStack[] stacks;

	public ArrayStackFilter(ItemStack... stacks) {
		this.stacks = stacks;
	}

	@Override
	public boolean matches(ItemStack stack) {
		if (stacks.length == 0 || !hasFilter()) {
			return true;
		}
		return InventoryUtil.isItemEqual(stack, stacks);
	}

	public ItemStack[] getStacks() {
		return stacks;
	}

	private boolean hasFilter() {
		for (ItemStack filter : stacks) {
			if (filter != null) {
				return true;
			}
		}
		return false;
	}
}
