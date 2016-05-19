/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public interface IWoodAccess {
	// ITEMS

	ItemStack getLog(EnumWoodType woodType, boolean fireproof);

	ItemStack getPlanks(EnumWoodType woodType, boolean fireproof);

	ItemStack getSlab(EnumWoodType woodType, boolean fireproof);

	ItemStack getFence(EnumWoodType woodType, boolean fireproof);
	
	ItemStack getFenceGate(EnumWoodType woodType, boolean fireproof);

	ItemStack getStairs(EnumWoodType woodType, boolean fireproof);

	/**
	 * All doors in Minecraft are fireproof.
	 */
	ItemStack getDoor(EnumWoodType woodType);

	// BLOCKS

	/**
	 * Forestry logs have their own properties for woodType and use {@link BlockLog.EnumAxis} for orientation.
	 */
	IBlockState getLogBlock(EnumWoodType woodType, boolean fireproof);

	/**
	 * Forestry slabs have no properties except their wood type.
	 */
	IBlockState getPlanksBlock(EnumWoodType woodType, boolean fireProof);

	/**
	 * Forestry slabs have the same properties as {@link BlockSlab}
	 */
	IBlockState getSlabBlock(EnumWoodType woodType, boolean fireproof);

	/**
	 * Forestry fences have the same properties as {@link BlockFence}
	 */
	IBlockState getFenceBlock(EnumWoodType woodType, boolean fireproof);

	/**
	 * Forestry fence gates have the same properties as {@link BlockFenceGate}
	 */
	IBlockState getFenceGateBlock(EnumWoodType woodType, boolean fireproof);

	/**
	 * Forestry stairs have the same properties as {@link BlockStairs}
	 */
	IBlockState getStairsBlock(EnumWoodType woodType, boolean fireproof);

	/**
	 * Forestry doors have the same properties as {@link BlockDoor}
	 */
	IBlockState getDoorBlock(EnumWoodType woodType);
}
