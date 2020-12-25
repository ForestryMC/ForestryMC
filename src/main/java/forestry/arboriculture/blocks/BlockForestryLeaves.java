/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.arboriculture.blocks;

import com.mojang.authlib.GameProfile;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.core.IToolScoop;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockForestryLeaves extends BlockAbstractLeaves implements IGrowable {

    public BlockForestryLeaves() {
        super(Block.Properties.create(Material.LEAVES)
                              .hardnessAndResistance(0.2f)
                              .sound(SoundType.PLANT)
                              .tickRandomly()
                              .notSolid());
    }

    @Override
    protected ITree getTree(IBlockReader world, BlockPos pos) {
        TileLeaves leaves = TileUtil.getTile(world, pos, TileLeaves.class);
        if (leaves != null) {
            ITree tree = leaves.getTree();
            return tree;
        }

        return null;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        super.tick(state, world, pos, rand);

        TileLeaves tileLeaves = TileUtil.getTile(world, pos, TileLeaves.class);

        // check leaves tile because they might have decayed
        if (tileLeaves != null && !tileLeaves.isRemoved() && rand.nextFloat() <= 0.1) {
            tileLeaves.onBlockTick(world, pos, state, rand);
        }
    }

    /* TILE ENTITY */
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileLeaves();
    }

    @Override
    protected void getLeafDrop(
            NonNullList<ItemStack> drops,
            World world,
            @Nullable GameProfile playerProfile,
            BlockPos pos,
            float saplingModifier,
            int fortune
    ) {
        TileLeaves tile = TileUtil.getTile(world, pos, TileLeaves.class);
        if (tile == null) {
            return;
        }

        ITree tree = tile.getTree();
        if (tree == null) {
            return;
        }

        // Add saplings	//TODO cast
        List<ITree> saplings = tree.getSaplings(world, playerProfile, pos, saplingModifier);

        for (ITree sapling : saplings) {
            if (sapling != null) {
                drops.add(TreeManager.treeRoot.getTypes().createStack(sapling, EnumGermlingType.SAPLING));
            }
        }

        // Add fruits
        if (tile.hasFruit()) {
            drops.addAll(tree.produceStacks(world, pos, tile.getRipeningTime()));
        }
    }

    @Override
    public ActionResultType onBlockActivated(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockRayTraceResult hit
    ) {
        TileLeaves leaves = TileUtil.getTile(world, pos, TileLeaves.class);
        if (leaves != null) {
            IButterfly caterpillar = leaves.getCaterpillar();
            ItemStack heldItem = player.getHeldItem(hand);
            ItemStack otherHand = player.getHeldItem(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
            if (heldItem.isEmpty() && otherHand.isEmpty()) {
                if (leaves.hasFruit() && leaves.getRipeness() >= 0.9F) {
                    PacketFXSignal packet = new PacketFXSignal(
                            PacketFXSignal.VisualFXType.BLOCK_BREAK,
                            PacketFXSignal.SoundFXType.BLOCK_BREAK,
                            pos,
                            state
                    );
                    NetworkUtil.sendNetworkPacket(packet, pos, world);
                    for (ItemStack fruit : leaves.pickFruit(ItemStack.EMPTY)) {
                        ItemHandlerHelper.giveItemToPlayer(player, fruit);
                    }
                    return ActionResultType.SUCCESS;
                }
            } else if (heldItem.getItem() instanceof IToolScoop && caterpillar != null) {
                ItemStack butterfly = ButterflyManager.butterflyRoot.getTypes().createStack(
                        caterpillar,
                        EnumFlutterType.CATERPILLAR
                );
                ItemStackUtil.dropItemStackAsEntity(butterfly, world, pos);
                leaves.setCaterpillar(null);
                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.PASS;
    }

    /* IGrowable */

    @Override
    public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
        TileLeaves leafTile = TileUtil.getTile(world, pos, TileLeaves.class);
        return leafTile != null && leafTile.hasFruit() && leafTile.getRipeness() < 1.0f;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        TileLeaves leafTile = TileUtil.getTile(world, pos, TileLeaves.class);
        if (leafTile != null) {
            leafTile.addRipeness(0.5f);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int colorMultiplier(
            BlockState state,
            @Nullable IBlockReader worldIn,
            @Nullable BlockPos pos,
            int tintIndex
    ) {
        if (worldIn != null && pos != null) {
            TileLeaves leaves = TileUtil.getTile(worldIn, pos, TileLeaves.class);
            if (leaves != null) {
                if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
                    return leaves.getFruitColour();
                } else {
                    PlayerEntity thePlayer = Minecraft.getInstance().player;
                    return leaves.getFoliageColour(thePlayer);
                }
            }
        }
        return ModuleArboriculture.proxy.getFoliageColorDefault();
    }
}
