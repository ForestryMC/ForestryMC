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
package forestry.farming.logic;

import forestry.farming.FarmRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class FarmLogicVegetable extends FarmLogicCrops {

	public FarmLogicVegetable() {
		super(FarmRegistry.getInstance().getFarmables("farmVegetables"));
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Items.CARROT);
	}

	@Override
	public String getName() {
		if (isManual) {
			return "Manual Vegetable Farm";
		} else {
			return "Managed Vegetable Farm";
		}
	}

}
