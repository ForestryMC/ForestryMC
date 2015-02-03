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

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.StackUtils;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;

public abstract class FarmLogicHomogeneous extends FarmLogic {

	protected final ItemStack[] resource;
	protected final ItemStack groundBlock;
	protected final IFarmable[] germlings;

	ArrayList<ItemStack> produce = new ArrayList<ItemStack>();

	public FarmLogicHomogeneous(IFarmHousing housing, ItemStack[] resource, ItemStack groundBlock, IFarmable[] germlings) {
		super(housing);
		this.resource = resource;
		this.groundBlock = groundBlock;
		this.germlings = germlings;
	}

	public boolean isAcceptedGround(ItemStack itemStack) {
		return StackUtils.isIdenticalItem(groundBlock, itemStack);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource[0].isItemEqual(itemstack);
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		for (IFarmable germling : germlings) {
			if (germling.isGermling(itemstack)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWindfall(ItemStack itemstack) {
		for (IFarmable germling : germlings) {
			if (germling.isWindfall(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean cultivate(BlockPos pos, ForgeDirection direction, int extent) {

		if (maintainSoil(pos, direction, extent)) {
			return true;
		}

		if (maintainGermlings(pos.up(), direction, extent)) {
			return true;
		}

		return false;
	}

	private boolean maintainSoil(BlockPos pos, ForgeDirection direction, int extent) {
		if (!housing.hasResources(resource)) {
			return false;
		}

		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(pos, direction, i);

			ItemStack stack = VectUtil.getAsItemStack(world, position);
			if (isAcceptedGround(stack) || !canBreakGround(world, position)) {
				continue;
			}

			produce.addAll(BlockUtil.getBlockDrops(world, position));

			setBlock(position, StackUtils.getBlock(groundBlock), groundBlock.getItemDamage());
			housing.removeResources(resource);
			return true;
		}

		return false;
	}

	protected abstract boolean maintainGermlings(BlockPos pos, ForgeDirection direction, int extent);
}
