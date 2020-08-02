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

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;

//eg    public static final Block COCOA = register("cocoa", new CocoaBlock(Block.Properties.create(Material.PLANTS).tickRandomly().hardnessAndResistance(0.2F, 3.0F).sound(SoundType.WOOD)));
public class BlockFruitPod extends CocoaBlock {

    public static List<BlockFruitPod> create() {
        List<BlockFruitPod> blocks = new ArrayList<>();
        for (IAlleleFruit fruit : AlleleFruits.getFruitAllelesWithModels()) {
            BlockFruitPod block = new BlockFruitPod(fruit);
            blocks.add(block);
        }
        return blocks;
    }

    private final IAlleleFruit fruit;

    public BlockFruitPod(IAlleleFruit fruit) {
        super(BlockSapling.Properties.create(Material.PLANTS)
                .tickRandomly()
                .hardnessAndResistance(0.2f, 3.0f)
                .sound(SoundType.WOOD));
        this.fruit = fruit;
    }

    public IAlleleFruit getFruit() {
        return fruit;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileFruitPod tile = TileUtil.getTile(world, pos, TileFruitPod.class);
        if (tile == null) {
            return ItemStack.EMPTY;
        }
        return tile.getPickBlock();
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (!isValidPosition(state, world, pos)) {
            spawnDrops(state, world, pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return;
        }

        TileFruitPod tile = TileUtil.getTile(world, pos, TileFruitPod.class);
        if (tile == null) {
            return;
        }

        tile.onBlockTick(world, pos, state, rand);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        if (!world.isRemote) {
            TileFruitPod tile = TileUtil.getTile(world, pos, TileFruitPod.class);
            if (tile != null) {
                for (ItemStack drop : tile.getDrops()) {
                    ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
                }
            }
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        Direction facing = state.get(HORIZONTAL_FACING);
        return BlockUtil.isValidPodLocation(world, pos, facing);
    }

    //TODO onBlockHarvested??
    //	@Override
    //	public void breakBlock(World world, BlockPos pos, BlockState state) {
    //		world.removeTileEntity(pos);
    //		super.breakBlock(world, pos, state);
    //	}

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileFruitPod();
    }

    /* IGrowable */
    @Override
    public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
        TileFruitPod podTile = TileUtil.getTile(world, pos, TileFruitPod.class);
        return podTile != null && podTile.canMature();
    }

    @Override
    public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        TileFruitPod podTile = TileUtil.getTile(world, pos, TileFruitPod.class);
        if (podTile != null) {
            podTile.addRipeness(0.5f);
        }
    }
}
