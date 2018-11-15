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

import forestry.api.gui.IItemElement;

public class ItemElement extends AbstractItemElement implements IItemElement {
	/* Attributes */
	private ItemStack stack;

	public ItemElement(int xPos, int yPos, ItemStack stack) {
		super(xPos, yPos);
		this.stack = stack;
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

	@Override
	public IItemElement setStack(ItemStack stack) {
		this.stack = stack;
		return this;
	}
}
