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
package forestry.apiculture.blocks;

import forestry.apiculture.tiles.TileCandle;
import forestry.core.blocks.IColoredBlock;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.RenderUtil;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

public class BlockCandle extends TorchBlock implements IColoredBlock {
    public static final Set<Item> lightingItems;
    public static final String COLOUR_TAG_NAME = "colour";

    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);

    enum State implements IStringSerializable {
        ON("on"), OFF("off");

        private final String name;

        State(String name) {
            this.name = name;
        }

        @Override
        public String getString() {
            return name;
        }
    }

    static {
        lightingItems = new HashSet<>(Arrays.asList(
                Items.FLINT_AND_STEEL,
                Items.FLINT,
                Item.BLOCK_TO_ITEM.get(Blocks.TORCH)
        ));
    }

    public BlockCandle() {
        super(
                Block.Properties.from(Blocks.TORCH)
                                .hardnessAndResistance(0.0f)
                                .sound(SoundType.WOOD),
                ParticleTypes.FLAME
        );
        setDefaultState(this.getStateContainer().getBaseState().with(STATE, State.OFF));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(STATE);
    }

    @Override
    public BlockState updatePostPlacement(
            BlockState state,
            Direction facing,
            BlockState facingState,
            IWorld world,
            BlockPos pos,
            BlockPos facingPos
    ) {
        TileCandle tileCandle = TileUtil.getTile(world, pos, TileCandle.class);
        if (tileCandle != null && tileCandle.isLit()) {
            state = state.with(STATE, State.ON);
        }

        return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        TileCandle candle = TileUtil.getTile(world, pos, TileCandle.class);
        if (candle != null && candle.isLit()) {
            return 14;
        }
        return 0;
    }

    @Override
    public ActionResultType onBlockActivated(
            BlockState state,
            World worldIn,
            BlockPos pos,
            PlayerEntity playerIn,
            Hand hand,
            BlockRayTraceResult rayTraceResult
    ) {
        TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
        if (tileCandle == null) {
            return ActionResultType.FAIL;
        }

        final boolean isLit = tileCandle.isLit();

        ActionResultType flag = ActionResultType.PASS;
        boolean toggleLitState = true;

        ItemStack heldItem = playerIn.getHeldItem(hand);

        if (!isLit) {
            if (heldItem.isEmpty() || !lightingItems.contains(heldItem.getItem())) {
                toggleLitState = false;
            } else if (ItemStackUtil.equals(this, heldItem) && isLit(heldItem)) {
                toggleLitState = true;
            }
        }

        if (!heldItem.isEmpty()) {
            if (ItemStackUtil.equals(this, heldItem)) {
                if (!isLit(heldItem)) {
                    // Copy the colour of an unlit, coloured candle.
                    if (heldItem.getTag() != null && heldItem.getTag().contains(COLOUR_TAG_NAME)) {
                        tileCandle.setColour(heldItem.getTag().getInt(COLOUR_TAG_NAME));
                    } else {
                        // Reset to white if item has no
                        tileCandle.setColour(0xffffff);
                    }
                } else {
                    toggleLitState = true;
                }

                flag = ActionResultType.SUCCESS;
            } else {
                boolean dyed = tryDye(heldItem, isLit, tileCandle);
                if (dyed) {
                    toggleLitState = false;
                    flag = ActionResultType.SUCCESS;
                }
            }
        }

        if (toggleLitState) {
            tileCandle.setLit(!isLit);
            worldIn.getProfiler().startSection("checkLight");
            worldIn.getChunkProvider().getLightManager().checkBlock(pos);
            worldIn.getProfiler().endSection();
            flag = ActionResultType.SUCCESS;
        }

        worldIn.notifyBlockUpdate(pos, state, state, 18);

        return flag;
    }

    private static boolean tryDye(ItemStack held, boolean isLit, TileCandle tileCandle) {
        // Check for dye-able.
        DyeColor color = DyeColor.getColor(held);
        if (color != null) {
            if (isLit) {
                tileCandle.setColour(color.getColorValue());
            } else {
                tileCandle.addColour(color.getColorValue());
            }

            return true;
        }

        return false;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(getCandleDrop(builder.assertPresent(LootParameters.BLOCK_ENTITY)));

        return drops;
    }

    @Override
    public ItemStack getPickBlock(
            BlockState state,
            RayTraceResult target,
            IBlockReader world,
            BlockPos pos,
            PlayerEntity player
    ) {
        return getCandleDrop(world, pos);
    }

    private ItemStack getCandleDrop(IBlockReader world, BlockPos pos) {
        return getCandleDrop(world.getTileEntity(pos));
    }

    private ItemStack getCandleDrop(@Nullable TileEntity tileEntity) {
        if (!(tileEntity instanceof TileCandle)) {
            return new ItemStack(this);
        }

        TileCandle tileCandle = (TileCandle) tileEntity;
        int colour = tileCandle.getColour();

        ItemStack itemStack = new ItemStack(this);
        if (colour != DyeColor.WHITE.getColorValue()) {
            // When dropped, tag new item stack with colour. Unless it's white, then do no such thing for maximum stacking.
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(COLOUR_TAG_NAME, colour);
            itemStack.setTag(tag);
        }
        return itemStack;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileCandle tileCandle = TileUtil.getTile(world, pos, TileCandle.class);
        if (tileCandle != null) {
            int colour = getColourValueFromItemStack(stack);
            boolean isLit = isLit(stack);
            tileCandle.setColour(colour);
            tileCandle.setLit(isLit);
            if (tileCandle.isLit()) {
                world.getProfiler().startSection("checkLight");
                world.getChunkProvider().getLightManager().checkBlock(pos);
                world.getProfiler().endSection();
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
        if (tileCandle != null && tileCandle.isLit()) {
            super.animateTick(stateIn, worldIn, pos, rand);
        }
    }

    private static int getColourValueFromItemStack(ItemStack itemStack) {
        int value = DyeColor.WHITE.getColorValue();
        if (itemStack.getTag() != null) {
            CompoundNBT tag = itemStack.getTag();
            if (tag.contains(COLOUR_TAG_NAME)) {
                value = tag.getInt(COLOUR_TAG_NAME);
            }
        }

        return value;
    }

    public static boolean isLit(ItemStack itemStack) {
        return itemStack.getDamage() > 0;
    }

    public static void addItemToLightingList(Item item) {
        lightingItems.add(item);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int colorMultiplier(
            BlockState state,
            @Nullable IBlockReader worldIn,
            @Nullable BlockPos pos,
            int tintIndex
    ) {
        if (worldIn != null && pos != null) {
            TileCandle tileCandle = TileUtil.getTile(worldIn, pos, TileCandle.class);
            if (tileCandle != null) {
                return tileCandle.getColour();
            }
        }

        return DyeColor.WHITE.getColorValue();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileCandle();
    }
}
