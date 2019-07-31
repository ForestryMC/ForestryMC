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

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.render.SaplingStateMapper;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockSapling extends BlockTreeContainer implements IGrowable, IStateMapperRegister, IItemModelRegister {
	protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);
	/* PROPERTYS */
	public static final PropertyTree TREE = new PropertyTree("tree");

	public BlockSapling() {
		super(Material.PLANTS);
		setSoundType(SoundType.PLANT);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return SAPLING_AABB;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int meta) {
		return new TileSapling();
	}

	/* COLLISION BOX */

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return null;
	}

	/* RENDERING */

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	/* STATES */
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling != null && sapling.getTree() != null) {
			state = state.withProperty(TREE, sapling.getTree().getGenome().getPrimary());
		} else {
			state = state.withProperty(TREE, TreeDefinition.Oak.getGenome().getPrimary());
		}
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TREE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new SaplingStateMapper());
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		//To delete the error message
		manager.registerItemModel(item, 0, "germlings/sapling.tree_larch");
	}

	/* PLANTING */
	public static boolean canBlockStay(IBlockAccess world, BlockPos pos) {
		TileSapling tile = TileUtil.getTile(world, pos, TileSapling.class);
		if (tile == null) {
			return false;
		}

		ITree tree = tile.getTree();
		return tree != null && tree.canStay(world, pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		if (!worldIn.isRemote && !canBlockStay(worldIn, pos)) {
			dropAsSapling(worldIn, pos);
			worldIn.setBlockToAir(pos);
		}
	}

	/* REMOVING */
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack drop = getDrop(world, pos);
		if (!drop.isEmpty()) {
			drops.add(drop);
		}
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling == null || sapling.getTree() == null) {
			return ItemStack.EMPTY;
		}
		return TreeManager.treeRoot.getMemberStack(sapling.getTree(), EnumGermlingType.SAPLING);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
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
		ItemStack drop = getDrop(world, pos);
		if (!drop.isEmpty()) {
			ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
		}
	}

	private static ItemStack getDrop(IBlockAccess world, BlockPos pos) {
		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling != null) {
			ITree tree = sapling.getTree();
			if (tree != null) {
				return TreeManager.treeRoot.getMemberStack(tree, EnumGermlingType.SAPLING);
			}
		}
		return ItemStack.EMPTY;
	}

	/* GROWNING */
	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		if (world.rand.nextFloat() >= 0.45F) {
			return false;
		}
		TileSapling saplingTile = TileUtil.getTile(world, pos, TileSapling.class);
		return saplingTile == null || saplingTile.canAcceptBoneMeal(rand);
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileSapling saplingTile = TileUtil.getTile(world, pos, TileSapling.class);
		if (saplingTile != null) {
			saplingTile.tryGrow(rand, true);
		}
	}
}
