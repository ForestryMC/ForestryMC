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
package forestry.apiculture.blocks;

import com.google.common.collect.ImmutableMap;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.blocks.IColoredBlock;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.RenderUtil;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;

public class BlockCandle extends TorchBlock implements IColoredBlock {
    private static final ImmutableMap<String, Integer> colours;
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
        colours = ImmutableMap.<String, Integer>builder()
                .put("dyeWhite", new Color(255, 255, 255).getRGB())
                .put("dyeOrange", new Color(219, 125, 62).getRGB())
                .put("dyeMagenta", new Color(255, 20, 255).getRGB())
                .put("dyeLightBlue", new Color(107, 138, 201).getRGB())
                .put("dyeYellow", new Color(255, 255, 20).getRGB())
                .put("dyeLime", new Color(20, 255, 20).getRGB())
                .put("dyePink", new Color(208, 132, 153).getRGB())
                .put("dyeGray", new Color(74, 74, 74).getRGB())
                .put("dyeLightGray", new Color(154, 161, 161).getRGB())
                .put("dyeCyan", new Color(20, 255, 255).getRGB())
                .put("dyePurple", new Color(126, 61, 181).getRGB())
                .put("dyeBlue", new Color(20, 20, 255).getRGB())
                .put("dyeBrown", new Color(79, 50, 31).getRGB())
                .put("dyeGreen", new Color(53, 70, 27).getRGB())
                .put("dyeRed", new Color(150, 52, 48).getRGB())
                .put("dyeBlack", new Color(20, 20, 20).getRGB())
                .build();

        lightingItems = new HashSet<>(Arrays.asList(
                Items.FLINT_AND_STEEL,
                Items.FLINT,
                Item.getItemFromBlock(Blocks.TORCH)
        ));
    }

    public BlockCandle() {
        super(Block.Properties.from(Blocks.TORCH)
                .hardnessAndResistance(0.0f)
                .sound(SoundType.WOOD), ParticleTypes.FLAME);
        setDefaultState(this.getStateContainer().getBaseState().with(STATE, State.OFF));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(STATE);
    }

    //	@Override
    //	public BlockState getActualState(BlockState state, IBlockReader world, BlockPos pos) {
    //		TileCandle tileCandle = TileUtil.getTile(world, pos, TileCandle.class);
    //		if (tileCandle != null && tileCandle.isLit()) {
    //			state = state.with(STATE, State.ON);
    //		}
    //		return super.getActualState(state, world, pos);
    //	}

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
                    RenderUtil.markForUpdate(pos);
                    toggleLitState = false;
                    flag = ActionResultType.SUCCESS;
                }
            }
        }

        if (toggleLitState) {
            tileCandle.setLit(!isLit);
            RenderUtil.markForUpdate(pos);
            worldIn.getProfiler().startSection("checkLight");
            worldIn.getChunkProvider().getLightManager().checkBlock(pos);
            worldIn.getProfiler().endSection();
            flag = ActionResultType.SUCCESS;
        }

        return flag;
    }

    private static boolean tryDye(ItemStack held, boolean isLit, TileCandle tileCandle) {
        // Check for dye-able.
        for (Map.Entry<String, Integer> colour : colours.entrySet()) {
            String colourName = colour.getKey();
            for (ItemStack stack : new ItemStack[0]) {// TODO tags OreDictionary.getOres(colourName)) {
                if (false) {//OreDictionary.itemMatches(stack, held, true)) {
                    if (isLit) {
                        tileCandle.setColour(colour.getValue());
                    } else {
                        tileCandle.addColour(colour.getValue());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    //TODO - is this fixed?
    /* DROP HANDLING */
    // Hack: 	When harvesting we need to get the drops in onBlockHarvested,
    // 			because Mojang destroys the block and tile before calling getDrops.
    private final ThreadLocal<ItemStack> drop = new ThreadLocal<>();

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(world, pos, state, player);
        if (!world.isRemote) {
            ItemStack itemStack = getCandleDrop(world, pos);
            drop.set(itemStack);
        }
    }

//    @Override
//    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
//        ItemStack dropStack = drop.get();
//        drop.remove();
//        List<ItemStack> drops = new ArrayList<>();
//
//        // not harvested, get drops normally
//        if (dropStack == null) {
//            dropStack = getCandleDrop(builder.assertPresent(LootParameters.BLOCK_ENTITY));
//        }
//
//        drops.add(dropStack);
//        return drops;
//    }

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

        //int newMeta = tileCandle.isLit() ? 1 : 0;// todo: meta ?
        ItemStack itemStack = new ItemStack(this);
        if (colour != 0xffffff) {
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
        int value = 0xffffff; // default to white.
        if (itemStack.getTag() != null) {
            CompoundNBT tag = itemStack.getTag();
            if (tag.contains(COLOUR_TAG_NAME)) {
                value = tag.getInt(COLOUR_TAG_NAME);
            }
        }

        return value;
    }

    public static boolean isLit(ItemStack itemStack) {
        return false;//TODO properties or something itemStack.getItemDamage() > 0;
    }

    public static void addItemToLightingList(Item item) {
        lightingItems.add(item);
    }

    public ItemStack getUnlitCandle(int amount) {
        return new ItemStack(this, amount);//TODO flatten , 0);
    }

    public ItemStack getLitCandle(int amount) {
        return new ItemStack(this, amount);// TODO flatten , 1);
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

        return 0xffffff;
    }
}
