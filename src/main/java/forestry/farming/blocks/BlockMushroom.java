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

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.config.Constants;

public class BlockMushroom extends BlockBush implements IItemModelRegister, IGrowable {

	public static final PropertyEnum<MushroomType> VARIANT = PropertyEnum.create("mushroom", MushroomType.class);
	public static final PropertyBool MATURE = PropertyBool.create("mature");

	public enum MushroomType implements IStringSerializable {
		BROWN {
			@Override
			public ItemStack getDrop() {
				return new ItemStack(Blocks.BROWN_MUSHROOM);
			}
		},
		RED {
			@Override
			public ItemStack getDrop() {
				return new ItemStack(Blocks.RED_MUSHROOM);
			}
		};

		public abstract ItemStack getDrop();

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	private final WorldGenerator[] generators;

	public BlockMushroom() {
		setHardness(0.0f);
		this.generators = new WorldGenerator[]{new WorldGenBigMushroom(Blocks.BROWN_MUSHROOM_BLOCK), new WorldGenBigMushroom(Blocks.RED_MUSHROOM_BLOCK)};
		setCreativeTab(null);
		setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, MushroomType.BROWN).withProperty(MATURE, false));
		setTickRandomly(true);
		setSoundType(SoundType.PLANT);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT, MATURE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal() | ((state.getValue(MATURE) ? 1 : 0) << 2);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, MushroomType.values()[meta % 2]).withProperty(MATURE, (meta >> 2) == 1);
	}

	@Override
	public boolean getTickRandomly() {
		return true;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		MushroomType type = state.getValue(VARIANT);
		drops.add(type.getDrop());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.isFullBlock();
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			IBlockState iblockstate = worldIn.getBlockState(pos.down());
			return iblockstate.getBlock() == Blocks.MYCELIUM || (iblockstate.getBlock() == Blocks.DIRT && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL || worldIn.getLight(pos) < 13 && iblockstate.getBlock().canSustainPlant(iblockstate, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this));
		} else {
			return false;
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.isRemote || rand.nextInt(2) != 0) {
			return;
		}

		IBlockState blockState = world.getBlockState(pos);
		if (!blockState.getValue(MATURE)) {
			world.setBlockState(pos, blockState.withProperty(MATURE, true), Constants.FLAG_BLOCK_SYNC);
		} else {
			int lightValue1 = world.getLightFromNeighbors(pos.up());
			if (lightValue1 <= 7) {
				generateGiantMushroom(world, pos, blockState, rand);
			}
		}
	}

	public void generateGiantMushroom(World world, BlockPos pos, IBlockState state, Random rand) {
		MushroomType type = state.getValue(VARIANT);

		world.setBlockToAir(pos);
		if (!generators[type.ordinal()].generate(world, rand, pos)) {
			world.setBlockState(pos, getStateFromMeta(type.ordinal()), 0);
		}
	}

	public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		this.generateGiantMushroom(worldIn, pos, state, rand);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "minecraft", "brown_mushroom");
		manager.registerItemModel(item, 1, "minecraft", "red_mushroom");
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

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return state.getValue(VARIANT).getDrop();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}
}
