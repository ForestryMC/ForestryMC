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
package forestry.core.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
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
import forestry.api.core.IModelObject;
import forestry.api.core.IVariantObject;
import forestry.core.CreativeTabForestry;
import forestry.core.IItemTyped;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

/**
 * Humus, bog earth, peat
 */
public class BlockSoil extends Block implements IItemTyped, IVariantObject, IModelObject {

	public static final PropertyEnum SOIL = PropertyEnum.create("soil", SoilType.class);
	
	public enum SoilType implements IStringSerializable {
		HUMUS, BOG_EARTH, PEAT;
		
		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}

	private static final int degradeDelimiter = 3;

	public BlockSoil() {
		super(Material.sand);
		setTickRandomly(true);
		setHardness(0.5f);
		setStepSound(soundTypeGrass);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public int tickRate(World world) {
		return 500;
	}
	
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((SoilType)state.getValue(SOIL)).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(SOIL,  SoilType.values()[meta]);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		SoilType type = getTypeFromMeta(getMetaFromState(state));

		if (type == SoilType.PEAT) {
			ret.add(ForestryItem.peat.getItemStack());
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

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		int meta = getMetaFromState(state);

		SoilType type = getTypeFromMeta(meta);

		if (type == SoilType.HUMUS) {
			updateTickHumus(world, pos, random);
		} else if (type == SoilType.BOG_EARTH) {
			updateTickBogEarth(world, pos, random);
		}
	}
	
	private void updateTickHumus(World world, BlockPos pos, Random random) {
		if (isEnrooted(world, pos)) {
			degradeSoil(world, pos);
		}
	}

	private void updateTickBogEarth(World world, BlockPos pos, Random random) {
		if (isMoistened(world, pos)) {
			matureBog(world, pos);
		}
	}

	private boolean isEnrooted(World world, BlockPos pos) {

		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				Block block = world.getBlockState(new BlockPos(pos.getX() + i, pos.getY() + 1, pos.getZ() + j)).getBlock();
				if (block == Blocks.log || block == Blocks.sapling || block == ForestryBlock.saplingGE.block())
				// We are not returning true if we are the base of a
				// sapling.
				{
					return !(i == 0 && j == 0);
				}
			}
		}

		return false;
	}

	/**
	 * If a tree or sapling is in the vicinity, there is a chance, that the soil will degrade.
	 */
	private void degradeSoil(World world, BlockPos pos) {

		if (world.rand.nextInt(140) != 0) {
			return;
		}

		IBlockState state = world.getBlockState(pos);
		int meta = getMetaFromState(state);

		// Unpack first
		int type = meta & 0x03;
		int grade = meta >> 2;

		// Increment (de)gradation
		grade++;

		// Repackage in format TTGG
		meta = (grade << 2 | type);

		if (grade >= degradeDelimiter) {
			world.setBlockState(pos, Blocks.sand.getStateFromMeta(0), Defaults.FLAG_BLOCK_SYNCH);
		} else {
			world.setBlockState(pos, world.getBlockState(pos).getBlock().getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH);
		}
		world.markBlockForUpdate(pos);
	}

	public static boolean isMoistened(World world, BlockPos pos) {

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

	private void matureBog(World world, BlockPos pos) {

		if (world.rand.nextInt(13) != 0) {
			return;
		}

		IBlockState state = world.getBlockState(pos);
		int meta = state.getBlock().getMetaFromState(state);

		// Unpack first

		int type = meta & 0x03;
		int maturity = meta >> 2;

		if (maturity >= degradeDelimiter) {
			return;
		}

		// Increment (de)gradation
		maturity++;

		meta = (maturity << 2 | type);
		world.setBlockState(pos, world.getBlockState(pos).getBlock().getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH);
		world.markBlockForUpdate(pos);
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plant) {
		EnumPlantType plantType = plant.getPlantType(world, pos);
		if (plantType != EnumPlantType.Crop && plantType != EnumPlantType.Plains) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		int meta = state.getBlock().getMetaFromState(state);
		SoilType type = getTypeFromMeta(meta);

		return type == SoilType.HUMUS;
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	public SoilType getTypeFromMeta(int meta) {
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

	// / CREATIVE INVENTORY
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
	}

	@Override
	public ModelType getModelType() {
		return ModelType.META;
	}

	@Override
	public String[] getVariants() {
		return new String[]{ "humus", "bog_earth", "peat" };
	}

}
