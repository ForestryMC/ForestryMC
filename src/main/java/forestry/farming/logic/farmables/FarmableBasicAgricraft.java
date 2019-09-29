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
package forestry.farming.logic.farmables;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.farming.logic.crops.CropBasicAgriCraft;

public class FarmableBasicAgricraft implements IFarmable {
	private final Block cropBlock;
	@Nullable
	private final Method matureMethod;
	private final Item seedItem;

	public FarmableBasicAgricraft(Block cropBlock, Item seedItem) {
		this.cropBlock = cropBlock;
		this.seedItem = seedItem;
		Class<? extends Block> cropClass = cropBlock.getClass();
		Method method;
		try {
			method = ReflectionHelper.findMethod(cropClass, "isMature", null, World.class, BlockPos.class);
		} catch (ReflectionHelper.UnableToFindMethodException e) {
			method = null;
		}
		this.matureMethod = method;
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, IBlockState blockState) {
		return blockState.getBlock() == cropBlock;
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return itemstack.getItem() == seedItem;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		if (!isMature(world, pos)) {
			return null;
		}
		return new CropBasicAgriCraft(world, blockState, pos);
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		NonNullList<ItemStack> stacks = NonNullList.create();
		seedItem.getSubItems(CreativeTabs.SEARCH, stacks);
		info.addGermlings(stacks);
	}

	private boolean isMature(World world, BlockPos pos) {
		if (matureMethod == null) {
			return false;
		}
		try {
			return (boolean) matureMethod.invoke(cropBlock, world, pos);
		} catch (InvocationTargetException | IllegalAccessException e) {
			return false;
		}
	}
}
