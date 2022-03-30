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

import forestry.core.utils.ItemStackUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.Serial;

public class ItemStackMap<T> extends StackMap<ItemStack, T> {
	@Serial
	private static final long serialVersionUID = -8511966739130702305L;

	@Override
	protected boolean areEqual(ItemStack a, ItemStack b) {
		return ItemStackUtil.isCraftingEquivalent(b, a);
	}

	@Override
	protected boolean isValidKey(Object key) {
		return key instanceof ItemStack || key instanceof Item || key instanceof String || key instanceof ResourceLocation;
	}

	@Override
	protected ItemStack getStack(Object key) {
		if (key instanceof ItemStack) {
			return (ItemStack) key;
		}
		return ItemStack.EMPTY;
	}

}
