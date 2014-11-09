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
package forestry.core.inventory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;

public class ItemStackSizeSorter implements Comparator<ItemStack> {

	private static ItemStackSizeSorter instance;

	private static ItemStackSizeSorter getInstance() {
		if (instance == null) {
			instance = new ItemStackSizeSorter();
		}
		return instance;
	}

	public static void sort(List<ItemStack> list) {
		Collections.sort(list, getInstance());
	}

	@Override
	public int compare(ItemStack o1, ItemStack o2) {
		return o1.stackSize - o2.stackSize;
	}

}
