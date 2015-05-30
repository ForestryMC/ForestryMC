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
package forestry.farming.gadgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IStructureLogic;
import forestry.api.core.ITileStructure;
import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmListener;
import forestry.core.gadgets.TileForestry;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.GuiId;

public abstract class TileFarm extends TileForestry implements IFarmComponent {

	public static final int TYPE_PLAIN = 0;
	public static final int TYPE_REVERSE = 1;
	public static final int TYPE_TOP = 2;
	public static final int TYPE_BAND = 3;
	public static final int TYPE_GEARS = 4;
	public static final int TYPE_HATCH = 5;
	public static final int TYPE_VALVE = 6;
	public static final int TYPE_CONTROL = 7;

	public TileFarm() {
		this.structureLogic = Farmables.farmInterface.createFarmStructureLogic(this);
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player) {
		if (this.isMaster()) {
			player.openGui(ForestryAPI.instance, GuiId.MultiFarmGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
		} else if (this.hasMaster()) {
			player.openGui(ForestryAPI.instance, GuiId.MultiFarmGUI.ordinal(), worldObj, masterX, masterY, masterZ);
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		this.isMaster = nbttagcompound.getBoolean("IsMaster");
		this.masterX = nbttagcompound.getInteger("MasterX");
		this.masterY = nbttagcompound.getInteger("MasterY");
		this.masterZ = nbttagcompound.getInteger("MasterZ");

		farmBlock = EnumFarmBlock.getFromCompound(nbttagcompound);

		// Init for master state
		if (isMaster) {
			makeMaster();
		}

		structureLogic.readFromNBT(nbttagcompound);

		super.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("IsMaster", isMaster);
		nbttagcompound.setInteger("MasterX", masterX);
		nbttagcompound.setInteger("MasterY", masterY);
		nbttagcompound.setInteger("MasterZ", masterZ);

		farmBlock.saveToCompound(nbttagcompound);

		structureLogic.writeToNBT(nbttagcompound);
	}

	/* UPDATING */
	@Override
	public void initialize() {
	}

	protected void updateServerSide() {
		// Periodic validation if needed
		if (updateOnInterval(200) && (!isIntegratedIntoStructure() || isMaster())) {
			validateStructure();
		}
	}

	/* CONSTRUCTION MATERIAL */
	EnumFarmBlock farmBlock = EnumFarmBlock.BRICK_STONE;
	protected int fixedType = -1;

	public void setFarmBlock(EnumFarmBlock block) {
		farmBlock = block;
		sendNetworkUpdate();
	}

	public EnumFarmBlock getFarmBlock() {
		return farmBlock;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		/*
		 * if(fixedType >= 0) return farmBlock.getIcon(fixedType);
		 * 
		 * int type = 0; if (meta == 1) type = TYPE_BAND;
		 * 
		 * if (meta == 0 && side == 2) type = TYPE_REVERSE; else if (meta == 0 && (side == 0 || side == 1)) type = TYPE_TOP;
		 * 
		 * return farmBlock.getIcon(type);
		 */
		return null;
	}

	/* TILEFORESTRY */

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeShort(farmBlock.ordinal());
		data.writeBoolean(isMaster());

		// so the client can know if it is part of an integrated structure
		data.writeShort(masterY);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		short farmBlockOrdinal = data.readShort();
		EnumFarmBlock farmType = EnumFarmBlock.values()[farmBlockOrdinal];
		if (data.readBoolean() && !isMaster()) {
			makeMaster();
		}

		// so the client can know if it is part of an integrated structure
		this.masterY = data.readShort();

		if (this.farmBlock != farmType) {
			this.farmBlock = farmType;
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		}
	}

	/* ITILESTRUCTURE */
	private final IStructureLogic structureLogic;
	private boolean isMaster;
	private int masterX, masterZ;
	private int masterY = -99;

	@Override
	public String getTypeUID() {
		return structureLogic.getTypeUID();
	}

	@Override
	public void validateStructure() {
		structureLogic.validateStructure();
	}

	@Override
	public void makeMaster() {
		setCentralTE(null);
		this.isMaster = true;

		if (worldObj != null && !worldObj.isRemote) {
			sendNetworkUpdate();
		}
	}

	@Override
	public void onStructureReset() {
		setCentralTE(null);
		if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 0);
		}
		isMaster = false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public ITileStructure getCentralTE() {

		if (!isIntegratedIntoStructure()) {
			return null;
		}

		if (isMaster) {
			return this;
		}

		TileEntity tile = worldObj.getTileEntity(masterX, masterY, masterZ);
		if (tile instanceof ITileStructure) {
			ITileStructure master = (ITileStructure) tile;
			if (master.isMaster()) {
				return master;
			}
		}
		return null;
	}

	private boolean isSameTile(TileEntity tile) {
		return tile.xCoord == xCoord && tile.yCoord == yCoord && tile.zCoord == zCoord;
	}

	@Override
	public void setCentralTE(TileEntity tile) {
		if (tile == null || tile == this || isSameTile(tile)) {
			this.masterX = this.masterZ = 0;
			this.masterY = -99;
			return;
		}

		this.isMaster = false;
		this.masterX = tile.xCoord;
		this.masterY = tile.yCoord;
		this.masterZ = tile.zCoord;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean isMaster() {
		return isMaster;
	}

	protected boolean hasMaster() {
		return this.masterY >= 0;
	}

	@Override
	public IInventoryAdapter getStructureInventory() {
		return getInternalInventory();
	}

	@Override
	public boolean isIntegratedIntoStructure() {
		return isMaster || this.masterY >= 0;
	}

	@Override
	public void registerListener(IFarmListener listener) {
	}

	@Override
	public void removeListener(IFarmListener listener) {
	}
}
