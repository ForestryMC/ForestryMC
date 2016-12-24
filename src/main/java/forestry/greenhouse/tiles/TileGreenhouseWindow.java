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
package forestry.greenhouse.tiles;

import javax.annotation.Nullable;

import forestry.api.climate.EnumClimatiserModes;
import forestry.api.climate.EnumClimatiserTypes;
import forestry.api.climate.IClimatiserDefinition;
import forestry.core.climate.ClimatiserDefinition;
import forestry.greenhouse.GreenhouseClimateWindow;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileGreenhouseWindow extends TileGreenhouseClimatiser {
	@Nullable
	private WindowMode mode;
	private static final IClimatiserDefinition DEFINITION = new ClimatiserDefinition(0.001F, EnumClimatiserModes.NONE, 5F, EnumClimatiserTypes.NONE);

	public TileGreenhouseWindow() {
		super(DEFINITION, new GreenhouseClimateWindow(20));
	}

	public void onNeighborBlockChange() {
		WindowMode otherMode = isBlocked();
		if (getMode() != WindowMode.PLAYER && getMode() != WindowMode.CONTROL && otherMode != getMode()) {
			setMode(otherMode);
		}
	}

	public WindowMode isBlocked() {
		if (world == null) {
			return WindowMode.BLOCK;
		}
		if (!world.isBlockLoaded(pos)) {
			return WindowMode.BLOCK;
		}
		IBlockState state = world.getBlockState(pos);
		BlockPos blockedPos;
		if (state.getBlock() == PluginGreenhouse.blocks.getGreenhouseBlock(BlockGreenhouseType.WINDOW_UP)) {
			blockedPos = getCoordinates().offset(EnumFacing.UP);
		} else {
			blockedPos = getCoordinates().offset(state.getValue(BlockGreenhouse.FACING));
		}
		return world.isAirBlock(blockedPos) ? WindowMode.OPEN : WindowMode.BLOCK;
	}


	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		if (mode != null) {
			data.setShort("mode", (short) mode.ordinal());
		}
		return super.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if (data.hasKey("mode")) {
			setMode(WindowMode.values()[data.getShort("mode")]);
		}
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		if (packetData.hasKey("mode")) {
			setMode(WindowMode.values()[packetData.getShort("mode")]);
		}
	}

	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		if (mode != null) {
			packetData.setShort("mode", (short) mode.ordinal());
		}
		super.encodeDescriptionPacket(packetData);
	}

	@Override
	public boolean canWork() {
		return true;
	}

	public void setMode(WindowMode mode) {
		this.mode = mode;
		setActive(mode == WindowMode.OPEN);
	}

	@Nullable
	public WindowMode getMode() {
		return mode;
	}

	public static enum WindowMode {
		PLAYER, BLOCK, CONTROL, OPEN
	}

}
