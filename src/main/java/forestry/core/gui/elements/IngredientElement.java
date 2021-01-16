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
package forestry.core.gui.elements;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

public class IngredientElement extends AbstractItemElement {
	/* Attributes - Final */
	private final NonNullList<ItemStack> items;

	public IngredientElement(int xPos, int yPos, Ingredient ingredient) {
		super(xPos, yPos);
		items = NonNullList.from(ItemStack.EMPTY, ingredient.getMatchingStacks());
	}

	public IngredientElement(int xPos, int yPos, NonNullList<ItemStack> items) {
		super(xPos, yPos);
		this.items = items;
	}

	@Override
	protected ItemStack getStack() {
		if (items.isEmpty()) {
			return ItemStack.EMPTY;
		}
		int perm = (int) (System.currentTimeMillis() / 1000 % items.size());
		return items.get(perm);
	}
}
