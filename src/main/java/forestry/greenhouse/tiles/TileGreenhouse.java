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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import forestry.api.core.EnumCamouflageType;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedBlock;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.core.CamouflageEvents.CamouflageChangeEvent;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ITitled;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.gui.ContainerGreenhouse;
import forestry.greenhouse.gui.GuiGreenhouse;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;
import forestry.greenhouse.network.packets.PacketCamouflageUpdate;

public abstract class TileGreenhouse extends MultiblockTileEntityForestry<MultiblockLogicGreenhouse> implements IGreenhouseComponent, IHintSource, IStreamableGui, IErrorLogicSource, IRestrictedAccess, ITitled, ICamouflageHandler, ICamouflagedBlock {

	private ItemStack camouflageBlock;

	protected TileGreenhouse() {
		super(new MultiblockLogicGreenhouse());
		camouflageBlock = null;
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		worldObj.notifyBlockOfStateChange(getPos(), worldObj.getBlockState(pos).getBlock());
		markDirty();
	}

	@Override
	public void onMachineBroken() {
		worldObj.notifyBlockOfStateChange(getPos(), worldObj.getBlockState(pos).getBlock());
		markDirty();
	}

	@Override
	public boolean allowsAutomation() {
		return false;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		
		if(data.hasKey("CamouflageBlock")){
			camouflageBlock = ItemStack.loadItemStackFromNBT(data.getCompoundTag("CamouflageBlock"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);


		if(camouflageBlock != null){
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			data.setTag("CamouflageBlock", nbtTag);
		}
	}

	@Override
	public void setCamouflageBlock(EnumCamouflageType type, ItemStack camouflageBlock) {
		this.camouflageBlock = camouflageBlock;
		
		if (worldObj != null) {
			if (worldObj.isRemote) {
				worldObj.markBlockForUpdate(getPos());
				Proxies.net.sendToServer(new PacketCamouflageUpdate(this, type));
			}
		}
		MinecraftForge.EVENT_BUS.post(new CamouflageChangeEvent(this, this, type));
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
		if(camouflageBlock != null){
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			packetData.setTag("CamouflageBlock", nbtTag);
		}
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		if(packetData.hasKey("CamouflageBlock")){
			setCamouflageBlock(getCamouflageType(), ItemStack.loadItemStackFromNBT(packetData.getCompoundTag("CamouflageBlock")));
		}
	}

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("greenhouse");
	}

	/* IStreamableGui */
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().writeGuiData(data);
		ItemStack camouflageBlock = getCamouflageBlock(getCamouflageType());
		if(camouflageBlock != null){
			data.writeShort(1);
			data.writeItemStack(camouflageBlock);
		}else{
			data.writeShort(0);
		}
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().readGuiData(data);
		if(data.readShort() == 1){
			setCamouflageBlock(getCamouflageType(), data.readItemStack());
		}
		
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

	/* ITitled */
	@Override
	public String getUnlocalizedTitle() {
		return "for.gui.greenhouse.title";
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiGreenhouse(player, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerGreenhouse(player.inventory, this);
	}
	
	@Override
	public EnumCamouflageType getCamouflageType() {
		if(getBlockType() instanceof BlockGreenhouse && ((BlockGreenhouse)getBlockType()).getGreenhouseType() == BlockGreenhouseType.GLASS){
			return EnumCamouflageType.GLASS;
		}
		return EnumCamouflageType.DEFAULT;
	}
	
	@Override
	public IInventoryAdapter getInternalInventory() {
		return getMultiblockLogic().getController().getInternalInventory();
	}
}
