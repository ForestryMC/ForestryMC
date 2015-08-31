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
package forestry.farming.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import forestry.core.IItemTyped;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.farming.worldgen.WorldGenBigMushroom;

public class BlockMushroom extends BlockBush implements IItemTyped, IGrowable {

	public static final PropertyEnum MUSHROOM = PropertyEnum.create("mushroom", MushroomType.class);
	
	public enum MushroomType implements IStringSerializable {
		BROWN, RED;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}

	private final WorldGenerator[] generators;
	private final ItemStack[] drops;

	public BlockMushroom() {
		setHardness(0.0f);
		this.generators = new WorldGenerator[]{new WorldGenBigMushroom(Blocks.brown_mushroom_block), new WorldGenBigMushroom(Blocks.red_mushroom_block)};
		this.drops = new ItemStack[]{new ItemStack(Blocks.brown_mushroom), new ItemStack(Blocks.red_mushroom)};
		setCreativeTab(null);
		setTickRandomly(true);
		setDefaultState(this.blockState.getBaseState().withProperty(MUSHROOM, MushroomType.BROWN));
        float f = 0.4F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[]{MUSHROOM});
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((MushroomType)state.getValue(MUSHROOM)).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(MUSHROOM, MushroomType.values()[meta]);
	}

	@Override
	public boolean getTickRandomly() {
		return true;
	}

	// / DROPS
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		MushroomType type = getTypeFromMeta(getMetaFromState(state));

		ret.add(drops[type.ordinal()]);

		return ret;
	}

	@Override
	protected boolean canPlaceBlockOn(Block block) {
		return block == Blocks.mycelium;
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		int meta = getMetaFromState(state);
		MushroomType type = getTypeFromMeta(meta);
		int maturity = meta >> 2;

		tickGermling(world, pos, state, rand, type, maturity);
	}

	private void tickGermling(World world, BlockPos pos, IBlockState state, Random random, MushroomType type, int maturity) {

		int lightvalue = world.getLightFromNeighbors(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));

		if (random.nextInt(2) != 0) {
			return;
		}

		if (maturity != 3) {
			maturity = 3;
			int matX = maturity << 2;
			int meta = (matX | type.ordinal());
			world.setBlockState(pos, getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH);
		} else if (lightvalue <= 7) {
			generateTree(world, pos, state, random);
		}
	}
	
	public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
		MushroomType type = getTypeFromMeta(getMetaFromState(state));

		world.setBlockToAir(pos);
		if (!generators[type.ordinal()].generate(world, rand, pos)) {
			world.setBlockState(pos, getStateFromMeta(type.ordinal()), 0);
		}
	}
	
    public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
    	this.generateTree(worldIn, pos, state, rand);
    }

	@Override
	public MushroomType getTypeFromMeta(int meta) {
		meta %= MushroomType.values().length;
		return MushroomType.values()[meta];
	}

    @Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    @Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return worldIn.rand.nextFloat() < 0.45D;
    }

    @Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        this.grow(worldIn, pos, state, rand);
    }
}
