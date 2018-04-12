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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableCollection;

import javax.annotation.Nullable;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import forestry.plugins.PluginIC2;
import forestry.plugins.PluginTechReborn;

public class CropRubber extends CropDestroy {

	public CropRubber(World world, IBlockState blockState, BlockPos position) {
		super(world, blockState, position, getReplantState(blockState));
	}

	/**
	 * Convert a "wet" rubber log blockstate into the dry version.
	 * Total hack since we don't have access to the blockstates.
	 */
	private static <T extends Comparable<T>> IBlockState getReplantState(IBlockState sappyState) {
		if (hasRubberToHarvest(sappyState)) {
			for (Map.Entry<IProperty<?>, Comparable<?>> wetPropertyEntry : sappyState.getProperties().entrySet()) {
				if (wetPropertyEntry.getKey() instanceof PropertyBool && wetPropertyEntry.getKey().getName().equals("hassap")) {
					return sappyState.withProperty(PropertyBool.create("hassap"), false);
				}
				String valueWetString = wetPropertyEntry.getValue().toString();
				String valueDryString = valueWetString.replace("wet", "dry");
				IProperty<?> property = wetPropertyEntry.getKey();

				IBlockState baseState = sappyState.getBlock().getBlockState().getBaseState();
				IBlockState dryState = getStateWithValue(baseState, property, valueDryString);
				if (dryState != null) {
					return dryState;
				}
			}
		}

		return sappyState.getBlock().getDefaultState();
	}

	public static boolean hasRubberToHarvest(IBlockState blockState) {
		Block block = blockState.getBlock();
		if (PluginIC2.rubberWood != null && ItemStackUtil.equals(block, PluginIC2.rubberWood)) {
			ImmutableCollection<Comparable<?>> propertyValues = blockState.getProperties().values();
			for (Comparable<?> propertyValue : propertyValues) {
				if (propertyValue.toString().contains("wet")) {
					return true;
				}
			}
		} else if (PluginTechReborn.RUBBER_WOOD != null && ItemStackUtil.equals(block, PluginTechReborn.RUBBER_WOOD)) {
			return blockState.getValue(PropertyBool.create("hassap"));
		}
		return false;
	}

	@Nullable
	private static <T extends Comparable<T>> IBlockState getStateWithValue(IBlockState
			baseState, IProperty<T> property, String valueString) {
		Optional<T> value = property.parseValue(valueString);
		if (value.isPresent()) {
			return baseState.withProperty(property, value.get());
		}
		return null;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		NonNullList<ItemStack> harvested = NonNullList.create();
		if (PluginIC2.rubberWood != null && ItemStackUtil.equals(world.getBlockState(pos).getBlock(), PluginIC2.rubberWood)) {
			harvested.add(PluginIC2.resin.copy());
		} else if (PluginTechReborn.RUBBER_WOOD != null && ItemStackUtil.equals(world.getBlockState(pos).getBlock(), PluginTechReborn.RUBBER_WOOD)) {
			harvested.add(PluginTechReborn.sap.copy());
		}
		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
		NetworkUtil.sendNetworkPacket(packet, pos, world);

		world.setBlockState(pos, replantState, Constants.FLAG_BLOCK_SYNC);
		return harvested;
	}

}
