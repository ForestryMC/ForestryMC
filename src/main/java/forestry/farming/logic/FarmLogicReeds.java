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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmProperties;

public class FarmLogicReeds extends FarmLogicSoil {
	public FarmLogicReeds(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Items.REEDS);
	}

	@Override
	public String getUnlocalizedName() {
		return "for.farm.reed";
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (20 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemStack) {
		if (isManual) {
			return false;
		}

		return super.isAcceptedResource(itemStack);

	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		if (isManual) {
			return false;
		}

		return itemstack.getItem() == Items.REEDS;
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack stack) {
		return false;
	}

}
