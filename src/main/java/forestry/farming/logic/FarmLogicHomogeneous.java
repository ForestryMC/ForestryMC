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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.StackUtils;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;
import forestry.farming.gadgets.StructureLogicFarm;

public abstract class FarmLogicHomogeneous extends FarmLogic {

	private final ItemStack resource;
	private final ItemStack soilBlock;
	protected final Iterable<IFarmable> germlings;

	ArrayList<ItemStack> produce = new ArrayList<ItemStack>();

	protected FarmLogicHomogeneous(IFarmHousing housing, ItemStack resource, ItemStack soilBlock, Iterable<IFarmable> germlings) {
		super(housing);
		this.resource = resource;
		this.soilBlock = soilBlock;
		this.germlings = germlings;
	}

	protected boolean isAcceptedSoil(ItemStack itemStack) {
		return StackUtils.isIdenticalItem(soilBlock, itemStack);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource.isItemEqual(itemstack);
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

	protected boolean isWindfall(ItemStack itemstack) {
		for (IFarmable germling : germlings) {
			if (germling.isWindfall(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean cultivate(BlockPos pos, FarmDirection direction, int extent) {

		if (maintainSoil(pos, direction, extent)) {
			return true;
		}

		if (maintainGermlings(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), direction, extent)) {
			return true;
		}

		return false;
	}

	private boolean maintainSoil(BlockPos pos, FarmDirection direction, int extent) {
		ItemStack[] resources = new ItemStack[]{resource};
		if (!housing.getFarmInventory().hasResources(resources)) {
			return false;
		}

		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(pos, direction, i);
			Block soil = VectUtil.getBlock(world, position);

			if (StructureLogicFarm.bricks.contains(soil)) {
				break;
			}

			ItemStack soilStack = VectUtil.getAsItemStack(world, position);
			if (isAcceptedSoil(soilStack)) {
				continue;
			}

			Vect platformPosition = position.add(0, -1, 0);
			Block platformBlock = VectUtil.getBlock(world, platformPosition);

			if (!StructureLogicFarm.bricks.contains(platformBlock)) {
				break;
			}

			produce.addAll(BlockUtil.getBlockDrops(world, position));

			setBlock(position, StackUtils.getBlock(soilBlock), soilBlock.getItemDamage());
			housing.getFarmInventory().removeResources(resources);
			return true;
		}

		return false;
	}

	protected abstract boolean maintainGermlings(BlockPos pos, FarmDirection direction, int extent);
}
