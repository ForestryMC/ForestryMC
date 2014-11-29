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

import java.util.Collection;
import java.util.Stack;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Vect;

public class FarmLogicPoale extends FarmLogic {

	IFarmable[] germlings;

	public FarmLogicPoale(IFarmHousing housing) {
		super(housing);
		Collection<IFarmable> farmables = Farmables.farmables.get("farmPoales");
		germlings = farmables.toArray(new IFarmable[farmables.size()]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return Items.reeds.getIconFromDamage(0);
	}

	@Override
	public String getName() {
		if (isManual)
			return "Manual Reed Farm";
		else
			return "Managed Reed Farm";
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
	public boolean isAcceptedResource(ItemStack itemstack) {
		if (isManual)
			return false;

		return StackUtils.equals(Blocks.sand, itemstack) || StackUtils.equals(Blocks.dirt, itemstack);
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		if (isManual)
			return false;

		return itemstack.getItem() == Items.reeds;
	}

	@Override
	public Collection<ItemStack> collect() {
		return null;
	}

	@Override
	public boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent) {
		return false;
	}

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {
		World world = getWorld();

		Stack<ICrop> crops = new Stack<ICrop>();
		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y + 1, z, direction, i);
			for (IFarmable seed : germlings) {
				ICrop crop = seed.getCropAt(world, position.x, position.y, position.z);
				if (crop != null)
					crops.push(crop);
			}
		}
		return crops;
	}

}
