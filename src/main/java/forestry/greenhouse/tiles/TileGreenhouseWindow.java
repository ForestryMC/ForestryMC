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

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.tiles.IActivatable;
import forestry.core.utils.NetworkUtil;
import forestry.greenhouse.api.climate.IClimateSource;
import forestry.greenhouse.api.climate.IClimateSourceOwner;
import forestry.greenhouse.blocks.BlockGreenhouseWindow;
import forestry.greenhouse.climate.ClimateSourceWindow;

public class TileGreenhouseWindow extends TileEntity implements IActivatable, IClimateSourceOwner {
	private final ClimateSourceWindow source;
	@Nullable
	private WindowMode mode;
	private String glass;
	private boolean active;

	public TileGreenhouseWindow() {
		source = new ClimateSourceWindow(0.05F, 5F);
		source.setOwner(this);
		glass = "glass";
	}

	@Override
	public IClimateSource getClimateSource() {
		return source;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getItemStack() {
		return ((BlockGreenhouseWindow) getBlockType()).getItem(glass);
	}

	@Override
	public boolean isCircuitable() {
		return false;
	}

	@Override
	public BlockPos getCoordinates() {
		return pos;
	}

	/* IActivatable */
	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}

		this.active = active;

		if (world != null) {
			if (world.isRemote) {
				world.markBlockRangeForRenderUpdate(getCoordinates(), getCoordinates());
			} else {
				NetworkUtil.sendNetworkPacket(new PacketActiveUpdate(this), getCoordinates(), world);
			}
		}
	}

	@Override
	public World getWorldObj() {
		return world;
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
		if (((BlockGreenhouseWindow) state.getBlock()).isRoofWindow()) {
			blockedPos = getCoordinates().offset(EnumFacing.UP);
		} else {
			blockedPos = getCoordinates().offset(state.getValue(BlockGreenhouseWindow.FACING));
		}
		return world.isAirBlock(blockedPos) ? WindowMode.OPEN : WindowMode.BLOCK;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if (data.hasKey("mode")) {
			setMode(WindowMode.values()[data.getShort("mode")]);
		}
		if (data.hasKey("Glass")) {
			glass = data.getString("Glass");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		if (mode != null) {
			data.setShort("mode", (short) mode.ordinal());
		}
		if (glass != null) {
			data.setString("Glass", glass);
		}
		return super.writeToNBT(data);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound data = super.getUpdateTag();
		if (mode != null) {
			data.setShort("mode", (short) mode.ordinal());
		}
		if (glass != null) {
			data.setString("Glass", glass);
		}
		return data;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	public String getGlass() {
		return glass;
	}

	public void setGlass(String glass) {
		this.glass = glass;
	}

	@Nullable
	public WindowMode getMode() {
		return mode;
	}

	public void setMode(WindowMode mode) {
		this.mode = mode;
		setActive(mode == WindowMode.OPEN);
	}

	public enum WindowMode {
		PLAYER, BLOCK, CONTROL, OPEN
	}

}
