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

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.IPlantable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;

/**
 * bog earth, which becomes peat
 */
public class BlockBogEarth extends Block implements IItemModelRegister, IBlockWithMeta {
	private static final int maturityDelimiter = 3; //maturity at which bogEarth becomes peat
	public static final PropertyInteger MATURITY = PropertyInteger.create("maturity", 0, maturityDelimiter);

	public enum SoilType implements IStringSerializable {
		BOG_EARTH("bog_earth"),
		PEAT("peat");


		private final String name;

		SoilType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public static SoilType fromMaturity(int maturity) {
			if (maturity >= maturityDelimiter) {
				return PEAT;
			} else {
				return BOG_EARTH;
			}
		}
	}

	public BlockBogEarth() {
		super(Material.GROUND);
		setTickRandomly(true);
		setHardness(0.5f);
		setSoundType(SoundType.GROUND);
		setCreativeTab(CreativeTabForestry.tabForestry);

		setDefaultState(this.blockState.getBaseState().withProperty(MATURITY, 0));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(MATURITY);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(MATURITY, meta);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, MATURITY);
	}

	@Override
	public int tickRate(World world) {
		return 500;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		Integer maturity = state.getValue(MATURITY);
		SoilType type = SoilType.fromMaturity(maturity);

		if (type == SoilType.PEAT) {
			drops.add(ModuleCore.getItems().peat.getItemStack(2));
			drops.add(new ItemStack(Blocks.DIRT));
		} else {
			drops.add(new ItemStack(this, 1, SoilType.BOG_EARTH.ordinal()));
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (world.isRemote || world.rand.nextInt(13) != 0) {
			return;
		}

		Integer maturity = state.getValue(MATURITY);
		SoilType type = SoilType.fromMaturity(maturity);

		if (type == SoilType.BOG_EARTH) {
			if (isMoistened(world, pos)) {
				world.setBlockState(pos, state.withProperty(MATURITY, maturity + 1), Constants.FLAG_BLOCK_SYNC);
			}
		}
	}

	private static boolean isMoistened(World world, BlockPos pos) {
		for (BlockPos.MutableBlockPos waterPos : BlockPos.getAllInBoxMutable(pos.add(-2, -2, -2), pos.add(2, 2, 2))) {
			IBlockState blockState = world.getBlockState(waterPos);
			Block block = blockState.getBlock();
			if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		return false;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return false;
	}

	public static SoilType getTypeFromState(IBlockState state) {
		Integer maturity = state.getValue(MATURITY);
		return SoilType.fromMaturity(maturity);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "soil/bog");
		manager.registerItemModel(item, 1, "soil/bog");
		manager.registerItemModel(item, 2, "soil/bog");
		manager.registerItemModel(item, 3, "soil/peat");
	}

	@Override
	public String getNameFromMeta(int meta) {
		SoilType type = SoilType.fromMaturity(meta);
		return type.getName();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	public ItemStack get(SoilType soilType, int amount) {
		return new ItemStack(this, amount, soilType.ordinal());
	}
}
