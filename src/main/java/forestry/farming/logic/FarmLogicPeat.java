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
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Vect;

public class FarmLogicPeat extends FarmLogicWatered {

	public FarmLogicPeat(IFarmHousing housing) {
		super(housing, new ItemStack[]{ForestryBlock.soil.getItemStack(1, 1)},
				new ItemStack[]{ForestryBlock.soil.getItemStack(1, 1)},
				new ItemStack[]{new ItemStack(Blocks.dirt), new ItemStack(Blocks.grass)});
	}

	@Override
	public int getFertilizerConsumption() {
		return 2;
	}

	@Override
	public String getName() {
		if (isManual)
			return "Manual Peat Bog";
		else
			return "Managed Peat Bog";
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public Collection<ICrop> harvest(int x, int y, int z, ForgeDirection direction, int extent) {
		World world = getWorld();

		Stack<ICrop> crops = new Stack<ICrop>();
		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			ItemStack occupant = getAsItemStack(position);

			if (!ForestryBlock.soil.isBlockEqual(StackUtils.getBlock(occupant)))
				continue;
			int type = occupant.getItemDamage() & 0x03;
			int maturity = occupant.getItemDamage() >> 2;

			if (type != 1)
				continue;

			if (maturity >= 3)
				crops.push(new CropPeat(world, position));

		}
		return crops;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return ForestryItem.peat.item().getIconFromDamage(0);
	}

}
