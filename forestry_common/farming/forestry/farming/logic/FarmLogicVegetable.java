/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.logic;

import net.minecraft.init.Items;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;

public class FarmLogicVegetable extends FarmLogicCrops {

	public FarmLogicVegetable(IFarmHousing housing) {
		super(housing, Farmables.farmables.get("farmVegetables").toArray(new IFarmable[0]));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return Items.carrot.getIconFromDamage(0);
	}

	@Override
	public String getName() {
		if (isManual)
			return "Manual Vegetable Farm";
		else
			return "Managed Vegetable Farm";
	}

}
