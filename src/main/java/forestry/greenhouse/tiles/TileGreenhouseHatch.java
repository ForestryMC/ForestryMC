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

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.mojang.authlib.GameProfile;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.greenhouse.GreenhouseEvents.CamouflageChangeEvent;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.MultiblockTileEntityBase;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.PlayerUtil;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;
import forestry.greenhouse.network.packets.PacketCamouflageUpdate;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileGreenhouseHatch extends MultiblockTileEntityBase<MultiblockLogicGreenhouse> implements IGreenhouseComponent, IStreamableGui, IHintSource, IErrorLogicSource, IOwnedTile, ICamouflageHandler, ICamouflagedTile, IEnergyProvider, IEnergyReceiver {

	EnumFacing outwards;
	private ItemStack camouflageBlock;

	@Nullable
	private GameProfile owner;

	public TileGreenhouseHatch() {
		super(new MultiblockLogicGreenhouse());
		outwards = null;
		camouflageBlock = null;
	}

	public EnumFacing getOutwardsDir() {
		return outwards;
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		worldObj.notifyBlockOfStateChange(getPos(), worldObj.getBlockState(pos).getBlock());
		markDirty();

		recalculateOutwardsDirection(minCoord, maxCoord);
	}

	@Override
	public void onMachineBroken() {
		worldObj.notifyBlockOfStateChange(getPos(), worldObj.getBlockState(pos).getBlock());
		markDirty();
		outwards = null;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		if (data.hasKey("CamouflageBlock")) {
			camouflageBlock = ItemStack.loadItemStackFromNBT(data.getCompoundTag("CamouflageBlock"));
		}

		if (data.hasKey("owner")) {
			NBTTagCompound ownerNbt = data.getCompoundTag("owner");
			this.owner = PlayerUtil.readGameProfileFromNBT(ownerNbt);
		}
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);

		if (camouflageBlock != null) {
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

	public final void setOwner(@Nonnull GameProfile owner) {
		this.owner = owner;
	}

	/* CONSTRUCTION MATERIAL */
	@Override
	public void setCamouflageBlock(String type, ItemStack camouflageBlock) {
		if(!ItemStackUtil.isIdenticalItem(camouflageBlock, this.camouflageBlock)){
			this.camouflageBlock = camouflageBlock;

			if (worldObj != null) {
				if (worldObj.isRemote) {
					Proxies.net.sendToServer(new PacketCamouflageUpdate(this, type));
					worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
				}
			}
			MinecraftForge.EVENT_BUS.post(new CamouflageChangeEvent(getMultiblockLogic().getController(), this, this, type));
		}
	}

	@Override
	public ItemStack getCamouflageBlock(String type) {
		return camouflageBlock;
	}

	@Override
	public ItemStack getDefaultCamouflageBlock(String type) {
		return null;
	}

	@Override
	public boolean canHandleType(String type) {
		return type.equals(getCamouflageType());
	}

	/* IStreamableGui */
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().writeGuiData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().readGuiData(data);
	}

	/* TILEFORESTRY */
	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		if (camouflageBlock != null) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			packetData.setTag("CamouflageBlock", nbtTag);
		}
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		if (packetData.hasKey("CamouflageBlock")) {
			setCamouflageBlock(getCamouflageType(), ItemStack.loadItemStackFromNBT(packetData.getCompoundTag("CamouflageBlock")));
		}
	}

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("greenhouse");
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
		return CamouflageManager.DEFAULT;
	}

	private IItemHandler getOutwardsInventory() {
		if (getOutwardsTile() == null) {
			return null;
		}
		return TileUtil.getInventoryFromTile(getOutwardsTile(), outwards.getOpposite());
	}

	private TileEntity getOutwardsTile() {
		if (outwards == null || worldObj == null || pos == null) {
			return null;
		}
		return worldObj.getTileEntity(getPos().offset(outwards));
	}

	private IEnergyConnection getOutwardEnergyConnection() {
		TileEntity tile = getOutwardsTile();
		if (!(tile instanceof IEnergyConnection)) {
			return null;
		}
		return (IEnergyConnection) tile;
	}

	private IEnergyHandler getOutwardEnergyHandler() {
		TileEntity tile = getOutwardsTile();
		if (!(tile instanceof IEnergyHandler)) {
			return null;
		}
		return (IEnergyHandler) tile;
	}

	private IEnergyReceiver getOutwardEnergyReceiver() {
		TileEntity tile = getOutwardsTile();
		if (!(tile instanceof IEnergyReceiver)) {
			return null;
		}
		return (IEnergyReceiver) tile;
	}

	private IEnergyProvider getOutwardEnergyProvider() {
		TileEntity tile = getOutwardsTile();
		if (!(tile instanceof IEnergyProvider)) {
			return null;
		}
		return (IEnergyProvider) tile;
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

	@Nonnull
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
		if(getOutwardsTile() != null){
			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return getOutwardsTile().getCapability(capability, facing);
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing) {
		if(getOutwardsTile() != null){
			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
				return getOutwardsTile().hasCapability(capability, facing);
			}
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		IEnergyHandler handler = getOutwardEnergyHandler();
		if (handler == null) {
			return 0;
		}
		return handler.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		IEnergyHandler handler = getOutwardEnergyHandler();
		if (handler == null) {
			return 0;
		}
		return handler.getMaxEnergyStored(from);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		IEnergyConnection connection = getOutwardEnergyConnection();
		if (connection == null) {
			return false;
		}
		return connection.canConnectEnergy(from);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		IEnergyReceiver receiver = getOutwardEnergyReceiver();
		if (receiver == null) {
			return 0;
		}
		return receiver.receiveEnergy(from, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		IEnergyProvider provider = getOutwardEnergyProvider();
		if (provider == null) {
			return 0;
		}
		return provider.extractEnergy(from, maxExtract, simulate);
	}

	@Override
	public World getWorldObj() {
		return worldObj;
	}


}
