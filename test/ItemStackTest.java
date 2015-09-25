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

import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;

import forestry.apiculture.items.ItemHoneycomb;
import forestry.core.utils.ItemStackUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ItemStackTest {

	private ItemStack comb1;
	private ItemStack comb2;
	private ItemStack firework;

	@Before
	public void setup() {
		comb1 = new ItemStack(new ItemHoneycomb(), 32, 1);
		comb2 = new ItemStack(new ItemHoneycomb(), 32, 2);
		firework = new ItemStack(new ItemFirework(), 1, 0);
	}

	@Test
	public void testEquality() {
		Assert.assertTrue("Equal ItemStacks are identical.", ItemStackUtil.isIdenticalItem(comb1, comb1));
		Assert.assertTrue("Equal ItemStacks are identical.", ItemStackUtil.isIdenticalItem(comb2, comb2));
		Assert.assertFalse("Unequal ItemStacks are not identical.", ItemStackUtil.isIdenticalItem(comb1, comb2));

		// OreDict crashes when minecraft hasn't loaded
//		Assert.assertTrue("Different combs are crafting equivalent with Ore Dictionary.", ItemStackUtil.isCraftingEquivalent(comb1, comb2, true, false));
//		Assert.assertFalse("Different combs are not crafting equivalent without Ore Dictionary.", ItemStackUtil.isCraftingEquivalent(comb1, comb2, false, false));
//
//		Assert.assertFalse("Different items are not crafting equivalent with Ore Dictionary.", ItemStackUtil.isCraftingEquivalent(firework, comb1, true, false));
//		Assert.assertFalse("Different items are not crafting equivalent without Ore Dictionary.", ItemStackUtil.isCraftingEquivalent(firework, comb1, false, false));
	}
}
