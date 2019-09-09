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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.models.IStateMapperRegister;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockSapling extends BlockTreeContainer implements IGrowable, IStateMapperRegister, IItemModelRegister {
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	/* PROPERTYS */
	public static final PropertyTree TREE = new PropertyTree("tree");

	public BlockSapling() {
		super(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.PLANT));
	}

	@Override
	public VoxelShape getShape(BlockState blockState, IBlockReader blockReader, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileSapling();
	}

	/* RENDERING */
	@Override
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	/* STATES */
	//	@Override
	//	public BlockState getActualState(BlockState state, IBlockReader world, BlockPos pos) {
	//		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
	//		if (sapling != null && sapling.getTree() != null) {
	//			state = state.with(TREE, sapling.getTree().getGenome().getPrimary());
	//		} else {
	//			state = state.with(TREE, TreeDefinition.Oak.getGenome().getPrimary());
	//		}
	//		return state;
	//	}

	@Override
	public BlockState getStateAtViewpoint(BlockState state, IBlockReader world, BlockPos pos, Vec3d viewpoint) {
		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling != null && sapling.getTree() != null) {
			state = state.with(TREE, sapling.getTree().getGenome().getActiveAllele(TreeChromosomes.SPECIES));
		} else {
			state = state.with(TREE, TreeDefinition.Oak.getGenome().getActiveAllele(TreeChromosomes.SPECIES));
		}
		return state;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(TREE);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerStateMapper() {
		//	ModelLoader.setCustomStateMapper(this, new SaplingStateMapper());
	}

	/* MODELS */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		//To delete the error message
		manager.registerItemModel(item, 0, "germlings/sapling.tree_larch");
	}

	/* PLANTING */
	public static boolean canBlockStay(IBlockReader world, BlockPos pos) {
		TileSapling tile = TileUtil.getTile(world, pos, TileSapling.class);
		if (tile == null) {
			return false;
		}

		ITree tree = tile.getTree();
		return tree != null && tree.canStay(world, pos);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, p_220069_6_);
		if (!worldIn.isRemote && !canBlockStay(worldIn, pos)) {
			dropAsSapling(worldIn, pos);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	/* REMOVING */
	//	@Override
	//	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
	//		ItemStack drop = getDrop(world, pos);
	//		if (!drop.isEmpty()) {
	//			drops.add(drop);
	//		}
	//	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling == null || sapling.getTree() == null) {
			return ItemStack.EMPTY;
		}
		return TreeManager.treeRoot.getTypes().createStack(sapling.getTree(), EnumGermlingType.SAPLING);
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
		if (!world.isRemote && canHarvestBlock(state, world, pos, player)) {
			if (!player.isCreative()) {
				dropAsSapling(world, pos);
			}
		}

		return world.setBlockState(pos, Blocks.AIR.getDefaultState());
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

	private static ItemStack getDrop(IBlockReader world, BlockPos pos) {
		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling != null) {
			ITree tree = sapling.getTree();
			if (tree != null) {
				return TreeManager.treeRoot.getTypes().createStack(tree, EnumGermlingType.SAPLING);
			}
		}
		return ItemStack.EMPTY;
	}

	/* GROWNING */
	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) {
		if (world.rand.nextFloat() >= 0.45F) {
			return false;
		}
		TileSapling saplingTile = TileUtil.getTile(world, pos, TileSapling.class);
		return saplingTile == null || saplingTile.canAcceptBoneMeal(rand);
	}

	@Override
	public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, BlockState state) {
		TileSapling saplingTile = TileUtil.getTile(world, pos, TileSapling.class);
		if (saplingTile != null) {
			saplingTile.tryGrow(rand, true);
		}
	}
}
