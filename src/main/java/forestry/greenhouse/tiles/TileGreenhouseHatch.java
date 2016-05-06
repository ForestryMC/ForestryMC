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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import forestry.api.core.EnumCamouflageType;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.greenhouse.GreenhouseEvents.CamouflageChangeEvent;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.MultiblockTileEntityBase;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.PlayerUtil;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;
import forestry.greenhouse.network.packets.PacketCamouflageUpdate;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public class TileGreenhouseHatch extends MultiblockTileEntityBase<MultiblockLogicGreenhouse> implements IGreenhouseComponent, IHintSource, IErrorLogicSource, IRestrictedAccess, ICamouflageHandler, ICamouflagedTile, IFluidHandler, IEnergyProvider, IEnergyReceiver {

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

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);


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
	public void setCamouflageBlock(EnumCamouflageType type, ItemStack camouflageBlock) {
		this.camouflageBlock = camouflageBlock;
		
		if (worldObj != null) {
			if (worldObj.isRemote) {
				worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
				Proxies.net.sendToServer(new PacketCamouflageUpdate(this, type));
			}
		}
		MinecraftForge.EVENT_BUS.post(new CamouflageChangeEvent(getMultiblockLogic().getController().createState(), this, this, type));
	}
	
	@Override
	public ItemStack getCamouflageBlock(EnumCamouflageType type) {
		return camouflageBlock;
	}
	
	@Override
	public ItemStack getDefaultCamouflageBlock(EnumCamouflageType type) {
		return null;
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
	public IAccessHandler getAccessHandler() {
		return getMultiblockLogic().getController().getAccessHandler();
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {
		getMultiblockLogic().getController().onSwitchAccess(oldAccess, newAccess);
	}
	
	@Override
	public EnumCamouflageType getCamouflageType() {
		if (getBlockType() instanceof BlockGreenhouse && ((BlockGreenhouse) getBlockType()).getGreenhouseType() == BlockGreenhouseType.GLASS) {
			return EnumCamouflageType.GLASS;
		}
		return EnumCamouflageType.DEFAULT;
	}
	
	private IInventory getOutwardsInventory() {
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
	
	private IFluidHandler getOutwardFluidHandler() {
		TileEntity tile = getOutwardsTile();
		if (!(tile instanceof IFluidHandler)) {
			return null;
		}
		return (IFluidHandler) tile;
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
		
		if (((BlockGreenhouse) getBlockType()).getGreenhouseType() != BlockGreenhouseType.HATCH_OUTPUT) {
			outwards = outwards.getOpposite();
		}
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getOutwardsTile() != null) {
			return getOutwardsTile().getCapability(capability, facing);
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getOutwardsTile() != null) {
			return getOutwardsTile().hasCapability(capability, facing);
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
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		IFluidHandler handler = getOutwardFluidHandler();
		if (handler == null) {
			return 0;
		}
		return handler.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		IFluidHandler handler = getOutwardFluidHandler();
		if (handler == null) {
			return null;
		}
		return handler.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		IFluidHandler handler = getOutwardFluidHandler();
		if (handler == null) {
			return null;
		}
		return handler.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		IFluidHandler handler = getOutwardFluidHandler();
		if (handler == null) {
			return false;
		}
		return handler.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		IFluidHandler handler = getOutwardFluidHandler();
		if (handler == null) {
			return false;
		}
		return handler.canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		IFluidHandler handler = getOutwardFluidHandler();
		if (handler == null) {
			return null;
		}
		return handler.getTankInfo(from);
	}
	
	
}
