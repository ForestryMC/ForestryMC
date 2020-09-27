/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.logic.crops;

import com.google.common.collect.ImmutableCollection;
import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
//import forestry.plugins.PluginIC2;
//import forestry.plugins.PluginTechReborn;

public class CropRubber extends CropDestroy {

    public CropRubber(World world, BlockState blockState, BlockPos position) {
        super(world, blockState, position, getReplantState(blockState));
    }

    /**
     * Convert a "wet" rubber log blockstate into the dry version.
     * Total hack since we don't have access to the blockstates.
     */
    //TODO - will this hack still work for ic2 in 1.14? Or will they just have to expose blockstates in their API?
    private static <T extends Comparable<T>> BlockState getReplantState(BlockState sappyState) {
        if (hasRubberToHarvest(sappyState)) {
            for (Map.Entry<Property<?>, Comparable<?>> wetPropertyEntry : sappyState.getValues().entrySet()) {
                String valueWetString = wetPropertyEntry.getValue().toString();
                String valueDryString = valueWetString.replace("wet", "dry");
                Property<?> property = wetPropertyEntry.getKey();
                if (property instanceof BooleanProperty && property.getName().equals("hassap")) {
                    return sappyState.with(BooleanProperty.create("hassap"), false);
                }

                //TODO - I think this works
                BlockState dryState = getStateWithValue(sappyState, property, valueDryString);
                if (dryState != null) {
                    return dryState;
                }
            }
        }

        return sappyState.getBlock().getDefaultState();
    }

    public static boolean hasRubberToHarvest(BlockState blockState) {
        Block block = blockState.getBlock();
        if (false) {//PluginIC2.rubberWood != null && ItemStackUtil.equals(block, PluginIC2.rubberWood)) {
            ImmutableCollection<Comparable<?>> propertyValues = blockState.getValues().values();
            for (Comparable<?> propertyValue : propertyValues) {
                if (propertyValue.toString().contains("wet")) {
                    return true;
                }
            }
        } else if (false) {//PluginTechReborn.RUBBER_WOOD != null && ItemStackUtil.equals(block, PluginTechReborn.RUBBER_WOOD)) {
            return blockState.get(BooleanProperty.create("hassap"));
        }
        return false;
    }

    @Nullable
    private static <T extends Comparable<T>> BlockState getStateWithValue(
            BlockState baseState,
            Property<T> property,
            String valueString
    ) {
        Optional<T> value = property.parseValue(valueString);
        return value.map(t -> baseState.with(property, t)).orElse(null);
    }

    @Override
    protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
        NonNullList<ItemStack> harvested = NonNullList.create();
        Block harvestBlock = world.getBlockState(pos).getBlock();
        //TODO - when other mods exist, implement
        //		if (PluginIC2.rubberWood != null && ItemStackUtil.equals(harvestBlock, PluginIC2.rubberWood)) {
        //			harvested.add(PluginIC2.resin.copy());
        //		} else if (PluginTechReborn.RUBBER_WOOD != null && ItemStackUtil.equals(harvestBlock, PluginTechReborn.RUBBER_WOOD)) {
        //			harvested.add(PluginTechReborn.sap.copy());
        //		}
        PacketFXSignal packet = new PacketFXSignal(
                PacketFXSignal.VisualFXType.BLOCK_BREAK,
                PacketFXSignal.SoundFXType.BLOCK_BREAK,
                pos,
                blockState
        );
        NetworkUtil.sendNetworkPacket(packet, pos, world);

        world.setBlockState(pos, replantState, Constants.FLAG_BLOCK_SYNC);
        return harvested;
    }

}
