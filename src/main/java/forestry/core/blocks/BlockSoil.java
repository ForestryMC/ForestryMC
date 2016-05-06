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
package forestry.core.blocks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.PluginCore;
import forestry.core.config.Constants;

/**
 * Humus, bog earth, peat
 */
public class BlockSoil extends Block implements IItemModelRegister, IBlockWithMeta {
	private static final PropertyEnum<SoilType> SOIL = PropertyEnum.create("soil", SoilType.class);
	
	public enum SoilType implements IStringSerializable {
		HUMUS("humus"),
		BOG_EARTH("bog_earth"),
		PEAT("peat");

		@Nonnull
		private final String name;

		SoilType(@Nonnull String name) {
			this.name = name;
		}
		
		@Nonnull
		@Override
		public String getName() {
			return name;
		}
	}

	private static final int degradeDelimiter = 3;

	public BlockSoil() {
		super(Material.GROUND);
		setTickRandomly(true);
		setHardness(0.5f);
		setSoundType(SoundType.GROUND);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(SOIL).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(SOIL, SoilType.values()[meta]);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, SOIL);
	}

	@Override
	public int tickRate(World world) {
		return 500;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<>();

		SoilType type = state.getValue(SOIL);

		if (type == SoilType.PEAT) {
			ret.add(PluginCore.items.peat.getItemStack());
			ret.add(new ItemStack(Blocks.DIRT));
		} else if (type == SoilType.HUMUS) {
			ret.add(new ItemStack(Blocks.DIRT));
		} else {
			ret.add(new ItemStack(this, 1, SoilType.BOG_EARTH.ordinal()));
		}

		return ret;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.isRemote) {
			return;
		}

		SoilType type = state.getValue(SOIL);

		if (type == SoilType.HUMUS) {
			updateTickHumus(world, pos);
		} else if (type == SoilType.BOG_EARTH) {
			updateTickBogEarth(world, pos);
		}
	}

	private static void updateTickHumus(World world, BlockPos pos) {
		if (isEnrooted(world, pos)) {
			degradeSoil(world, pos);
		}
	}

	private static void updateTickBogEarth(World world, BlockPos pos) {
		if (isMoistened(world, pos)) {
			matureBog(world, pos);
		}
	}

	private static boolean isEnrooted(World world, BlockPos pos) {
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				BlockPos blockPos = pos.add(i, 1, j);
				IBlockState state = world.getBlockState(blockPos);
				Block block = state.getBlock();
				if (block instanceof BlockLog || block instanceof IGrowable) {
					// We are not returning true if we are the base of a sapling.
					return !(i == 0 && j == 0);
				}
			}
		}

		return false;
	}

	/**
	 * If a tree or sapling is in the vicinity, there is a chance, that the soil will degrade.
	 */
	private static void degradeSoil(World world, final BlockPos pos) {

		if (world.rand.nextInt(140) != 0) {
			return;
		}

		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		int meta = block.getMetaFromState(blockState);

		// Unpack first
		int type = meta & 0x03;
		int grade = meta >> 2;

		// Increment (de)gradation
		grade++;

		// Repackage in format TTGG
		meta = grade << 2 | type;

		if (grade >= degradeDelimiter) {
			world.setBlockState(pos, Blocks.SAND.getDefaultState(), Constants.FLAG_BLOCK_SYNCH);
		} else {
			world.setBlockState(pos, block.getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH);
		}
		world.markBlockRangeForRenderUpdate(pos, pos);
	}

	private static boolean isMoistened(World world, BlockPos pos) {

		for (int i = -2; i < 3; i++) {
			for (int j = -2; j < 3; j++) {
				Block block = world.getBlockState(new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() + j)).getBlock();
				if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
					return true;
				}
			}
		}

		return false;
	}

	private static void matureBog(World world, final BlockPos pos) {

		if (world.rand.nextInt(13) != 0) {
			return;
		}

		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		int meta = block.getMetaFromState(blockState);

		// Unpack first

		int type = meta & 0x03;
		int maturity = meta >> 2;

		if (maturity >= degradeDelimiter) {
			return;
		}

		// Increment (de)gradation
		maturity++;

		meta = maturity << 2 | type;
		world.setBlockState(pos, block.getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH);
		world.markBlockRangeForRenderUpdate(pos, pos);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		EnumPlantType plantType = plantable.getPlantType(world, pos);
		if (plantType != EnumPlantType.Crop && plantType != EnumPlantType.Plains) {
			return false;
		}

		SoilType type = world.getBlockState(pos).getValue(SOIL);

		return type == SoilType.HUMUS;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return false;
	}

	public static SoilType getTypeFromState(IBlockState state) {
		return state.getValue(SOIL);
	}

	public static SoilType getTypeFromMeta(int meta) {
		int type = meta & 0x03;
		int maturity = meta >> 2;

		if (type == 1) {
			if (maturity < degradeDelimiter) {
				return SoilType.BOG_EARTH;
			} else {
				return SoilType.PEAT;
			}
		} else {
			return SoilType.HUMUS;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "soil/humus");
		manager.registerItemModel(item, 1, "soil/bog");
		manager.registerItemModel(item, 2, "soil/peat");
	}

	@Override
	public String getNameFromMeta(int meta) {
		SoilType type = getTypeFromMeta(meta);
		return type.getName();
	}

	public ItemStack get(SoilType soilType, int amount) {
		return new ItemStack(this, amount, soilType.ordinal());
	}
}
