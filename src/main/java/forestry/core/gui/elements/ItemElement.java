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

public class ItemElement extends AbstractItemElement {
	/* Attributes - Final */
	private final ItemStack itemStack;

	public ItemElement(int xPos, int yPos, ItemStack itemStack) {
		super(xPos, yPos);
		this.itemStack = itemStack;
	}

	protected ItemStack getItemStack() {
		return itemStack;
	}
}
