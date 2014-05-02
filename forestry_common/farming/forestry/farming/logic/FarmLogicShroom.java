/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
import forestry.api.farming.IFarmable;

public class FarmLogicShroom extends FarmLogicArboreal {

	public FarmLogicShroom(IFarmHousing housing) {
		super(housing, new ItemStack[] { new ItemStack(Blocks.mycelium) }, new ItemStack[] { new ItemStack(Blocks.mycelium) }, new ItemStack[] { new ItemStack(
				Blocks.dirt), new ItemStack(Blocks.grass) }, Farmables.farmables.get("farmShroom").toArray(new IFarmable[0]));
		yOffset = -1;
	}

	@Override
	public String getName() {
		if (isManual)
			return "Manual Shroom Farm";
		else
			return "Managed Shroom Farm";
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
		produce = new ArrayList<ItemStack>();
		return products;
	}

}
