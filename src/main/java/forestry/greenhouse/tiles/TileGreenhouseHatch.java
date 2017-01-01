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
import java.io.IOException;

import com.mojang.authlib.GameProfile;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.MultiblockTileEntityBase;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.CamouflageSelectionType;
import forestry.core.network.packets.PacketCamouflageSelectServer;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.PlayerUtil;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileGreenhouseHatch extends MultiblockTileEntityBase<MultiblockLogicGreenhouse> implements IGreenhouseComponent, IStreamableGui, IErrorLogicSource, IOwnedTile, ICamouflageHandler, ICamouflagedTile {

	@Nullable
	private EnumFacing outwards;
	private ItemStack camouflageBlock;
	@Nullable
	private GameProfile owner;

	public TileGreenhouseHatch() {
		super(new MultiblockLogicGreenhouse());
		outwards = null;
		camouflageBlock = ItemStack.EMPTY;
	}

	@Nullable
	public EnumFacing getOutwardsDir() {
		return outwards;
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		world.notifyNeighborsOfStateChange(getPos(), world.getBlockState(pos).getBlock(), false);
		markDirty();

		recalculateOutwardsDirection(minCoord, maxCoord);
	}

	@Override
	public void onMachineBroken() {
		world.notifyNeighborsOfStateChange(getPos(), world.getBlockState(pos).getBlock(), false);
		markDirty();
		outwards = null;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		if (data.hasKey("CamouflageBlock")) {
			camouflageBlock = new ItemStack(data.getCompoundTag("CamouflageBlock"));
		}

		if (data.hasKey("owner")) {
			NBTTagCompound ownerNbt = data.getCompoundTag("owner");
			this.owner = PlayerUtil.readGameProfileFromNBT(ownerNbt);
		}
	}


	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);

		if (!camouflageBlock.isEmpty()) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			data.setTag("CamouflageBlock", nbtTag);
		}

		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			PlayerUtil.writeGameProfile(nbt, owner);
			data.setTag("owner", nbt);
		}

		return data;
	}

	/* IMultiblockComponent */
	@Nullable
	@Override
	public final GameProfile getOwner() {
		return owner;
	}

	public final void setOwner(GameProfile owner) {
		this.owner = owner;
	}

	/* CONSTRUCTION MATERIAL */
	@Override
	public boolean setCamouflageBlock(String type, ItemStack camouflageBlock, boolean sendClientUpdate) {
		if (!ItemStackUtil.isIdenticalItem(camouflageBlock, this.camouflageBlock)) {
			this.camouflageBlock = camouflageBlock;

			if (sendClientUpdate && world != null && world.isRemote) {
				Proxies.net.sendToServer(new PacketCamouflageSelectServer(this, type, CamouflageSelectionType.TILE));
			}
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getCamouflageBlock(String type) {
		return camouflageBlock;
	}

	@Override
	public ItemStack getDefaultCamouflageBlock(String type) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canHandleType(String type) {
		return type.equals(getCamouflageType());
	}

	/* IStreamableGui */
	@Override
	public void writeGuiData(PacketBufferForestry data) {
		getMultiblockLogic().getController().writeGuiData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		getMultiblockLogic().getController().readGuiData(data);
	}

	/* TILEFORESTRY */
	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		if (!camouflageBlock.isEmpty()) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			packetData.setTag("CamouflageBlock", nbtTag);
		}
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		if (packetData.hasKey("CamouflageBlock")) {
			setCamouflageBlock(getCamouflageType(), new ItemStack(packetData.getCompoundTag("CamouflageBlock")), true);
		}
	}

	/* IErrorLogicSource */
	@Override
	public IErrorLogic getErrorLogic() {
		return getMultiblockLogic().getController().getErrorLogic();
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return getMultiblockLogic().getController().getOwnerHandler();
	}

	@Override
	public String getCamouflageType() {
		if (getBlockType() instanceof BlockGreenhouse && ((BlockGreenhouse) getBlockType()).getGreenhouseType() == BlockGreenhouseType.GLASS) {
			return CamouflageManager.GLASS;
		}
		return CamouflageManager.BLOCK;
	}

	@Nullable
	private TileEntity getOutwardsTile() {
		if (outwards == null || world == null || pos == null) {
			return null;
		}
		return world.getTileEntity(getPos().offset(outwards));
	}

	public void recalculateOutwardsDirection(BlockPos minCoord, BlockPos maxCoord) {
		outwards = null;

		int facesMatching = 0;
		if (maxCoord.getX() == getPos().getX() || minCoord.getX() == getPos().getX()) {
			facesMatching++;
		}
		if (maxCoord.getY() == getPos().getY() || minCoord.getY() == getPos().getY()) {
			facesMatching++;
		}
		if (maxCoord.getZ() == getPos().getZ() || minCoord.getZ() == getPos().getZ()) {
			facesMatching++;
		}
		if (facesMatching == 1) {
			if (maxCoord.getX() == getPos().getX()) {
				outwards = EnumFacing.EAST;
			} else if (minCoord.getX() == getPos().getX()) {
				outwards = EnumFacing.WEST;
			} else if (maxCoord.getZ() == getPos().getZ()) {
				outwards = EnumFacing.SOUTH;
			} else if (minCoord.getZ() == getPos().getZ()) {
				outwards = EnumFacing.NORTH;
			} else if (maxCoord.getY() == getPos().getY()) {
				outwards = EnumFacing.UP;
			} else {
				outwards = EnumFacing.DOWN;
			}
		}

		if (((BlockGreenhouse) getBlockType()).getGreenhouseType() != BlockGreenhouseType.HATCH_OUTPUT && outwards != null) {
			outwards = outwards.getOpposite();
		}
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (getOutwardsTile() != null) {
			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return getOutwardsTile().getCapability(capability, facing);
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (getOutwardsTile() != null) {
			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return getOutwardsTile().hasCapability(capability, facing);
			}
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public World getWorldObj() {
		return world;
	}
}
