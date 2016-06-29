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
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.greenhouse.GreenhouseEvents.CamouflageChangeEvent;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ITitled;
import forestry.core.utils.ItemStackUtil;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseDoor;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.gui.ContainerGreenhouse;
import forestry.greenhouse.gui.GuiGreenhouse;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;
import forestry.greenhouse.network.packets.PacketCamouflageUpdate;

public abstract class TileGreenhouse extends MultiblockTileEntityForestry<MultiblockLogicGreenhouse> implements IGreenhouseComponent, IHintSource, IErrorLogicSource, IRestrictedAccess, ITitled, ICamouflageHandler, ICamouflagedTile {

	protected ItemStack camouflageBlock;

	protected TileGreenhouse() {
		super(new MultiblockLogicGreenhouse());
		camouflageBlock = null;
	}
	
	@Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate){
    	if(newSate.getBlock() instanceof BlockGreenhouseDoor && oldState.getBlock() instanceof BlockGreenhouseDoor){
    		return false;
    	}
        return super.shouldRefresh(world, pos, oldState, newSate);
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

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		
		if (data.hasKey("CamouflageBlock")) {
			camouflageBlock = ItemStack.loadItemStackFromNBT(data.getCompoundTag("CamouflageBlock"));
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
		return data;
	}

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
	public boolean canHandleType(String type) {
		return type.equals(getCamouflageType());
	}
	
	@Override
	public ItemStack getCamouflageBlock(String type) {
		return camouflageBlock;
	}
	
	@Override
	public ItemStack getDefaultCamouflageBlock(String type) {
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
	public String getCamouflageType() {
		if (getBlockType() instanceof BlockGreenhouse && ((BlockGreenhouse) getBlockType()).getGreenhouseType() == BlockGreenhouseType.GLASS) {
			return CamouflageManager.GLASS;
		}else if (((BlockGreenhouse) getBlockType()).getGreenhouseType() == BlockGreenhouseType.DOOR) {
			return CamouflageManager.DOOR;
		}
		return CamouflageManager.DEFAULT;
	}
	
	@Override
	public IInventoryAdapter getInternalInventory() {
		return getMultiblockLogic().getController().getInternalInventory();
	}
	
}
