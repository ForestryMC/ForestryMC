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
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmHousing;

public class FarmLogicVegetable extends FarmLogicCrops {

	public FarmLogicVegetable(IFarmHousing housing) {
		super(housing, Farmables.farmables.get("farmVegetables"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return Items.carrot.getIconFromDamage(0);
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
