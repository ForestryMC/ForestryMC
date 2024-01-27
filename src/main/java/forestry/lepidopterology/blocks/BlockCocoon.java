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

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.tiles.TileUtil;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.tiles.TileCocoon;

public class BlockCocoon extends Block implements EntityBlock {
	public static final VoxelShape BOUNDING_BOX = Block.box(0.3125F, 0.3125F, 0.3125F, 0.6875F, 1F, 0.6875F);
	private static final PropertyCocoon COCOON = AlleleButterflyCocoon.COCOON;//TODO: Convert to ModelProperty and add Cocoon model

	public BlockCocoon() {
		super(Block.Properties.of(MaterialCocoon.INSTANCE)
				.randomTicks()
				.sound(SoundType.GRAVEL));
		//		setCreativeTab(null);
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
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		TileCocoon tileCocoon = TileUtil.getTile(world, pos, TileCocoon.class);
		if (tileCocoon == null) {
			return;
		}

		if (tileCocoon.isRemoved()) {
			return;
		}

		tileCocoon.onBlockTick();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileCocoon(pos, state, false);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (facing != Direction.UP || !facingState.isAir()) {
			return state;
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		TileCocoon tile = TileUtil.getTile(world, pos, TileCocoon.class);
		if (tile == null) {
			return ItemStack.EMPTY;
		}

		IButterfly caterpillar = tile.getCaterpillar();
		int age = tile.getAge();

		ItemStack stack = ButterflyManager.butterflyRoot.getTypes().createStack(caterpillar, EnumFlutterType.COCOON);
		if (!stack.isEmpty() && stack.getTag() != null) {
			stack.getTag().putInt(ItemButterflyGE.NBT_AGE, age);
		}
		return stack;
	}

	//TODO automatically determined from shape?
	//	@Override
	//	public boolean isFullCube(BlockState state) {
	//		return false;
	//	}

	//other shapes (collision etc) defer to this in block I believe
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return BOUNDING_BOX;
	}

}
