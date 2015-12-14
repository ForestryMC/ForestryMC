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
package forestry.farming.tiles;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.circuits.ISocketable;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.tiles.ITitled;
import forestry.farming.gui.ContainerFarm;
import forestry.farming.gui.GuiFarm;
import forestry.farming.multiblock.MultiblockLogicFarm;
import forestry.farming.render.EnumFarmBlockTexture;

public abstract class TileFarm extends MultiblockTileEntityForestry<MultiblockLogicFarm> implements IFarmComponent, IHintSource, ISocketable, IStreamableGui, IErrorLogicSource, IRestrictedAccess, ITitled {

	private EnumFarmBlockTexture farmBlockTexture = EnumFarmBlockTexture.BRICK_STONE;

	protected TileFarm() {
		super(new MultiblockLogicFarm());
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, ChunkCoordinates minCoord, ChunkCoordinates maxCoord) {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		markDirty();
	}

	@Override
	public void onMachineBroken() {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		markDirty();
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return getMultiblockLogic().getController().getInternalInventory();
	}

	@Override
	public boolean allowsAutomation() {
		return false;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		farmBlockTexture = EnumFarmBlockTexture.getFromCompound(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		farmBlockTexture.saveToCompound(nbttagcompound);
	}

	/* CONSTRUCTION MATERIAL */

	public void setFarmBlockTexture(EnumFarmBlockTexture farmBlockTexture) {
		if (this.farmBlockTexture != farmBlockTexture) {
			this.farmBlockTexture = farmBlockTexture;
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		}
	}

	public EnumFarmBlockTexture getFarmBlockTexture() {
		return farmBlockTexture;
	}

	/* TILEFORESTRY */

	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		farmBlockTexture.saveToCompound(packetData);
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		EnumFarmBlockTexture farmBlockTexture = EnumFarmBlockTexture.getFromCompound(packetData);
		setFarmBlockTexture(farmBlockTexture);
	}

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("farm");
	}

	/* ISocketable */
	@Override
	public int getSocketCount() {
		return getMultiblockLogic().getController().getSocketCount();
	}

	@Override
	public ItemStack getSocket(int slot) {
		return getMultiblockLogic().getController().getSocket(slot);
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {
		getMultiblockLogic().getController().setSocket(slot, stack);
	}

	@Override
	public ICircuitSocketType getSocketType() {
		return getMultiblockLogic().getController().getSocketType();
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
		return "for.gui.farm.title";
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiFarm(player, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerFarm(player.inventory, this);
	}
}
