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

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;

public class FarmLogicMushroom extends FarmLogicArboreal {

	public FarmLogicMushroom(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public String getUnlocalizedName() {
		return "for.farm.shroom";
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
