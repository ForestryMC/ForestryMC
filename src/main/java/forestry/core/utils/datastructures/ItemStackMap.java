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
package forestry.core.utils.datastructures;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

public class ItemStackMap<T> extends StackMap<ItemStack, T> {
	private static final long serialVersionUID = -8511966739130702305L;

	@Override
	protected boolean areEqual(ItemStack a, Object b) {
		if (b instanceof ItemStack) {
			ItemStack b2 = (ItemStack) b;
			return a.isItemEqual(b2) && ItemStack.areItemStackTagsEqual(a, b2);
		}
		if (b instanceof Item) {
			return a.getItem() == b;
		}
		if (b instanceof String) {
			for (ItemStack stack : OreDictionary.getOres((String) b)) {
				if (areEqual(a, stack)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean isValidKey(Object key) {
		return key instanceof ItemStack || key instanceof Item || key instanceof String;
	}

	@Override
	protected ItemStack getStack(Object key) {
		if (key instanceof ItemStack) {
			return (ItemStack) key;
		}
		return null;
	}

}
