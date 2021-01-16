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

//TODO GrowthCraft for 1.9
public class FarmableBasicGrowthCraft {
} /*implements IFarmable {

	private final Block block;
	private final int matureMeta;
	private final boolean isRice;
	private final boolean isGrape;

	public FarmableBasicGrowthCraft(Block block, int matureMeta, boolean isRice, boolean isGrape) {
		this.block = block;
		this.matureMeta = matureMeta;
		this.isRice = isRice;
		this.isGrape = isGrape;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block != this.block) {
			return null;
		}
		if (block.getMetaFromState(blockState) != matureMeta) {
			return null;
		}
		return new CropBasicGrowthCraft(world, this.block, matureMeta, pos, isRice, isGrape);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStackUtil.equals(block, itemstack);
	}

	@Override
	public boolean plantSaplingAt(PlayerEntity player, ItemStack germling, World world, BlockPos pos) {
		return world.setBlockState(pos, block.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
*/
