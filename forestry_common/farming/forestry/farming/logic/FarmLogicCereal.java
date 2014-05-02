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

public class FarmLogicCereal extends FarmLogicCrops {

	public FarmLogicCereal(IFarmHousing housing) {
		super(housing, Farmables.farmables.get("farmWheat").toArray(new IFarmable[0]));
	}

	@Override
	public String getName() {
		if (isManual)
			return "Manual Farm";
		else
			return "Managed Farm";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return Items.wheat.getIconFromDamage(0);
	}

}
