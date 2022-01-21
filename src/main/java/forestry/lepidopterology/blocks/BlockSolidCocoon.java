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
package forestry.lepidopterology.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import forestry.apiculture.items.ItemScoop;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;
import forestry.lepidopterology.tiles.TileCocoon;

import org.jetbrains.annotations.Nullable;

public class BlockSolidCocoon extends Block implements EntityBlock {
	private static final PropertyCocoon COCOON = AlleleButterflyCocoon.COCOON;

	public BlockSolidCocoon() {
		super(Block.Properties.of(MaterialCocoon.INSTANCE)
				.harvestTool(ItemScoop.SCOOP)
				.harvestLevel(0)
				.strength(0.5F)
				.randomTicks()
				.sound(SoundType.GRAVEL));
		registerDefaultState(this.getStateDefinition().any().setValue(COCOON, ButterflyAlleles.cocoonDefault)
				.setValue(AlleleButterflyCocoon.AGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(COCOON, AlleleButterflyCocoon.AGE);
	}

	//TODO
	//	@OnlyIn(Dist.CLIENT)
	//	@Override
	//	public BlockState getActualState(BlockState state, IBlockReader world, BlockPos pos) {
	//		TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
	//		if (cocoon != null) {
	//			state = state.with(COCOON, cocoon.getCaterpillar().getGenome().getCocoon())
	//				.with(AlleleButterflyCocoon.AGE, cocoon.getAge());
	//		}
	//		return super.getActualState(state, world, pos);
	//	}
	//
	//	@OnlyIn(Dist.CLIENT)
	//	@Override
	//	public void registerStateMapper() {
	//		ModelLoader.setCustomStateMapper(this, new CocoonStateMapper());
	//	}
	//
	//	@OnlyIn(Dist.CLIENT)
	//	@Override
	//	public void registerModel(Item item, IModelManager manager) {
	//		// To delete the error message
	//		manager.registerItemModel(item, 0, "cocoon_late");
	//	}

	//	@Override
	//	public boolean isFullBlock(BlockState state) {
	//		return false;
	//	}
	//
	//	@Override
	//	public boolean isOpaqueCube(BlockState state) {
	//		return false;
	//	}

	@Override
	public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (canHarvestBlock(state, world, pos, player)) {
			TileUtil.actOnTile(world, pos, TileCocoon.class, cocoon -> {
				NonNullList<ItemStack> drops = cocoon.getCocoonDrops();
				for (ItemStack stack : drops) {
					ItemStackUtil.dropItemStackAsEntity(stack, world, pos);
				}
			});
		}

		return world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileCocoon(pos, state, true);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (facing != Direction.UP || !facingState.isAir(worldIn, facingPos)) {
			return state;
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return BlockCocoon.BOUNDING_BOX;
	}

}
