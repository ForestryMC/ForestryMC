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
package forestry.arboriculture.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.arboriculture.blocks.property.PropertyTree;
import forestry.arboriculture.models.SaplingStateMapper;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockSapling extends BlockTreeContainer implements IGrowable, IStateMapperRegister, IItemModelRegister {
	
	/* PROPERTYS */
	public static final PropertyTree TREE = new PropertyTree("tree");
	
	public static TileSapling getSaplingTile(IBlockAccess world, BlockPos pos) {
		return TileUtil.getTile(world, pos, TileSapling.class);
	}

	public BlockSapling() {
		super(Material.plants);

		float factor = 0.4F;
		setBlockBounds(0.5F - factor, 0.0F, 0.5F - factor, 0.5F + factor, factor * 2.0F, 0.5F + factor);
		setStepSound(soundTypeGrass);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int meta) {
		return new TileSapling();
	}

	/* COLLISION BOX */
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		return null;
	}

	/* RENDERING */
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube() {
		return false;
	}
	
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}
	
	/* STATES */
	@Override
	public int getMetaFromState(IBlockState state) {
		return TREE.getAllowedValues().indexOf(state.getValue(TREE));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TREE, TREE.getAllowedValues().get(meta));
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return super.getActualState(state, world, pos);
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, TREE);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new SaplingStateMapper());
	}
	
	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		//To delete the error message
		manager.registerItemModel(item, 0, "germlings/sapling.treeLarch");
	}

	/* PLANTING */
	public boolean canBlockStay(IBlockAccess world, BlockPos pos) {
		TileSapling tile = getSaplingTile(world, pos);
		if (tile == null) {
			return false;
		}
		if (tile.getTree() == null) {
			return false;
		}

		return tile.getTree().canStay(world, pos);
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		super.onNeighborBlockChange(world, pos, state, neighborBlock);
		if (!world.isRemote && !canBlockStay(world, pos)) {
			dropAsSapling(world, pos);
			world.setBlockToAir(pos);
		}
	}

	/* REMOVING */
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<>();
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		TileSapling sapling = getSaplingTile(world, pos);
		if (sapling == null || sapling.getTree() == null) {
			return null;
		}
		return TreeManager.treeRoot.getMemberStack(sapling.getTree(), EnumGermlingType.SAPLING);
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!world.isRemote && canHarvestBlock(world, pos, player)) {
			if (!player.capabilities.isCreativeMode) {
				dropAsSapling(world, pos);
			}
		}

		return world.setBlockToAir(pos);
	}

	private static void dropAsSapling(World world, BlockPos pos) {
		if (world.isRemote) {
			return;
		}

		TileSapling sapling = getSaplingTile(world, pos);
		if (sapling != null && sapling.getTree() != null) {
			ItemStack saplingStack = TreeManager.treeRoot.getMemberStack(sapling.getTree(), EnumGermlingType.SAPLING);
			ItemStackUtil.dropItemStackAsEntity(saplingStack, world, pos);
		}

	}

	/* GROWNING */
	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		TileSapling saplingTile = getSaplingTile(world, pos);
		if (saplingTile != null) {
			return saplingTile.canAcceptBoneMeal();
		}
		return true;
	}
	
	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		return world.rand.nextFloat() < 0.45F;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileSapling saplingTile = getSaplingTile(world, pos);
		if (saplingTile != null) {
			saplingTile.tryGrow(true);
		}
	}
}
