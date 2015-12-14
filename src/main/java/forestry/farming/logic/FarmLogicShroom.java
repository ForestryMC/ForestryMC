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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmHousing;

public class FarmLogicShroom extends FarmLogicArboreal {

	public FarmLogicShroom(IFarmHousing housing) {
		super(housing, new ItemStack(Blocks.mycelium), new ItemStack(Blocks.mycelium), Farmables.farmables.get("farmShroom"));
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
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return Blocks.red_mushroom.getBlockTextureFromSide(0);
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
	public Collection<ItemStack> collect() {
		Collection<ItemStack> products = produce;
		produce = new ArrayList<>();
		return products;
	}

}
