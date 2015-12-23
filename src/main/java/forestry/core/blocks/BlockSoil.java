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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Constants;
import forestry.core.utils.BlockPosUtil;
import forestry.plugins.PluginCore;

/**
 * Humus, bog earth, peat
 */
public class BlockSoil extends Block implements IItemTyped, IModelRegister {
	public static final PropertyEnum<SoilType> SOIL = PropertyEnum.create("soil", SoilType.class);
	
	public enum SoilType implements IStringSerializable {
		HUMUS, BOG_EARTH, PEAT;
		
		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	private static final int degradeDelimiter = 3;

	public BlockSoil() {
		super(Material.sand);
		setTickRandomly(true);
		setHardness(0.5f);
		setStepSound(soundTypeGrass);
		setCreativeTab(CreativeTabForestry.tabForestry);
		setDefaultState(this.blockState.getBaseState().withProperty(SOIL, SoilType.values()[0]));
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
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { SOIL });
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
			ret.add(new ItemStack(Blocks.dirt));
		} else if (type == SoilType.HUMUS) {
			ret.add(new ItemStack(Blocks.dirt));
		} else {
			ret.add(new ItemStack(this, 1, SoilType.BOG_EARTH.ordinal()));
		}

		return ret;
	}
	
	@Override
	public int getDamageValue(World world, BlockPos pos) {
		return (getMetaFromState(world.getBlockState(pos)) & 0x03);
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
				IBlockState state = world.getBlockState(new BlockPos(pos.getX() + i, pos.getY() + 1, pos.getZ() + j));
				Block block = state.getBlock();
				if (block == Blocks.log || block == Blocks.sapling || block instanceof IGrowable) {
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
	private static void degradeSoil(World world, BlockPos pos) {

		if (world.rand.nextInt(140) != 0) {
			return;
		}

		int meta = BlockPosUtil.getBlockMeta(world, pos);

		// Unpack first
		int type = meta & 0x03;
		int grade = meta >> 2;

		// Increment (de)gradation
		grade++;

		// Repackage in format TTGG
		meta = (grade << 2 | type);

		if (grade >= degradeDelimiter) {
			world.setBlockState(pos, Blocks.sand.getStateFromMeta(0), Constants.FLAG_BLOCK_SYNCH);
		} else {
			world.setBlockState(pos, BlockPosUtil.getBlock(world, pos).getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH);
		}
		world.markBlockForUpdate(pos);
	}

	private static boolean isMoistened(World world, BlockPos pos) {

		for (int i = -2; i < 3; i++) {
			for (int j = -2; j < 3; j++) {
				Block block = world.getBlockState(new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() + j)).getBlock();
				if (block == Blocks.water || block == Blocks.flowing_water) {
					return true;
				}
			}
		}

		return false;
	}

	private static void matureBog(World world, BlockPos pos) {

		if (world.rand.nextInt(13) != 0) {
			return;
		}

		int meta = BlockPosUtil.getBlockMeta(world, pos);

		// Unpack first

		int type = meta & 0x03;
		int maturity = meta >> 2;

		if (maturity >= degradeDelimiter) {
			return;
		}

		// Increment (de)gradation
		maturity++;

		meta = (maturity << 2 | type);
		world.setBlockState(pos, BlockPosUtil.getBlock(world, pos).getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH);
		world.markBlockForUpdate(pos);
	}
	
	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plant) {
		EnumPlantType plantType = plant.getPlantType(world, pos);
		if (plantType != EnumPlantType.Crop && plantType != EnumPlantType.Plains) {
			return false;
		}

		SoilType type = world.getBlockState(pos).getValue(SOIL);

		return type == SoilType.HUMUS;
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	// / CREATIVE INVENTORY
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
	}

	public ItemStack get(SoilType soilType, int amount) {
		return new ItemStack(this, amount, soilType.ordinal());
	}

	@Override
	public Enum getTypeFromMeta(int meta) {
		return SoilType.values()[meta];
	}
}
