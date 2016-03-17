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
package forestry.farming.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.blocks.IItemTyped;
import forestry.core.config.Constants;
import forestry.core.utils.BlockUtil;

public class BlockMushroom extends BlockBush implements IItemTyped, IItemModelRegister, IGrowable {

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
		super();
		setHardness(0.0f);
		this.generators = new WorldGenerator[]{new WorldGenBigMushroom(Blocks.brown_mushroom_block), new WorldGenBigMushroom(Blocks.red_mushroom_block)};
		this.drops = new ItemStack[]{new ItemStack(Blocks.brown_mushroom), new ItemStack(Blocks.red_mushroom)};
		setCreativeTab(null);
		setDefaultState(this.blockState.getBaseState().withProperty(MUSHROOM, MushroomType.BROWN));
		setTickRandomly(true);
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, MUSHROOM);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((MushroomType) state.getValue(MUSHROOM)).ordinal();
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
		ArrayList<ItemStack> ret = new ArrayList<>();

		MushroomType type = getTypeFromMeta(state.getBlock().getMetaFromState(state));

		ret.add(drops[type.ordinal()]);

		return ret;
	}

	@Override
	protected boolean canPlaceBlockOn(Block block) {
		return block == Blocks.mycelium;
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.isRemote) {
			return;
		}

		int meta = BlockUtil.getBlockMetadata(world, pos);
		MushroomType type = getTypeFromMeta(meta);
		int maturity = meta >> 2;

		tickGermling(world, pos.getX(), pos.getY(), pos.getZ(), rand, type, maturity);
	}

	private void tickGermling(World world, int i, int j, int k, Random random, MushroomType type, int maturity) {

		int lightvalue = world.getLight(new BlockPos(i, j + 1, k));

		if (random.nextInt(2) != 0) {
			return;
		}

		if (maturity != 3) {
			maturity = 3;
			int matX = maturity << 2;
			int meta = matX | type.ordinal();
			world.setBlockState(new BlockPos(i, j, k), world.getBlockState(new BlockPos(i, j, k)).getBlock().getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH);
		} else if (lightvalue <= 7) {
			generateTree(world, new BlockPos(i, j, k), world.getBlockState(new BlockPos(i, j, k)), random);
		}
	}
	
	public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
		MushroomType type = getTypeFromMeta(BlockUtil.getBlockMetadata(world, pos));

		world.setBlockToAir(pos);
		if (!generators[type.ordinal()].generate(world, rand, pos)) {
			world.setBlockState(pos, getStateFromMeta(type.ordinal()), 0);
		}
	}
	
	public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		this.generateTree(worldIn, pos, state, rand);
	}

	@Override
	public MushroomType getTypeFromMeta(int meta) {
		meta %= MushroomType.values().length;
		return MushroomType.values()[meta];
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "minecraft", "red_mushroom");
		manager.registerItemModel(item, 0, "minecraft", "brown_mushroom");
	}
	
	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return worldIn.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		this.grow(worldIn, pos, state, rand);
	}

}
