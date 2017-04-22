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

import forestry.api.farming.IFarmHousing;
import forestry.farming.FarmRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class FarmLogicMushroom extends FarmLogicArboreal {

	public FarmLogicMushroom() {
		super(new ItemStack(Blocks.MYCELIUM), Blocks.MYCELIUM.getDefaultState(), FarmRegistry.getInstance().getFarmables("farmShroom"));
		addSoil(new ItemStack(Blocks.DIRT, 1, 2), Blocks.DIRT.getStateFromMeta(2), true);
	}

	@Override
	public String getName() {
		if (isManual) {
			return "Manual Shroom Farm";
		} else {
			return "Managed Shroom Farm";
		}
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Blocks.RED_MUSHROOM);
	}

	@Override
	public int getFertilizerConsumption() {
		return 20;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (80 * hydrationModifier);
	}

	@Override
	public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
		NonNullList<ItemStack> products = produce;
		produce = NonNullList.create();
		return products;
	}

}
