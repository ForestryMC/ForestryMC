/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.utils;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

public class ItemStackMap<T> extends StackMap<ItemStack, T> {
	private static final long serialVersionUID = -8511966739130702305L;

	@Override
	protected boolean areEqual(ItemStack a, Object b) {
		if(b instanceof ItemStack) {
			ItemStack b2 = (ItemStack) b;
			return a.isItemEqual(b2) && ItemStack.areItemStackTagsEqual(a, b2);
		}
		if(b instanceof Item) {
			return a.getItem() == (Item) b;
		}
		if(b instanceof String) {
			for(ItemStack stack : OreDictionary.getOres((String) b)) {
				if(areEqual(a, stack)) return true;
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
		if(key instanceof ItemStack)
			return (ItemStack) key;
		return null;
	}

}
