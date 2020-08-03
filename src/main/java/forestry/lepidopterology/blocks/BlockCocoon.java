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

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.tiles.TileUtil;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.tiles.TileCocoon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockCocoon extends Block {
    public static final VoxelShape BOUNDING_BOX = Block.makeCuboidShape(0.3125F, 0.3125F, 0.3125F, 0.6875F, 1F, 0.6875F);
    private static final PropertyCocoon COCOON = AlleleButterflyCocoon.COCOON;//TODO: Convert to ModelProperty and add Cocoon model

    public BlockCocoon() {
        super(Block.Properties.create(MaterialCocoon.INSTANCE)
                .tickRandomly()
                .sound(SoundType.GROUND));
        //		setCreativeTab(null);
        setDefaultState(this.getStateContainer().getBaseState().with(COCOON, ButterflyAlleles.cocoonDefault)
                .with(AlleleButterflyCocoon.AGE, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
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
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
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
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileCocoon(false);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing != Direction.UP || !facingState.isAir(worldIn, facingPos)) {
            return state;
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return BOUNDING_BOX;
    }

}
