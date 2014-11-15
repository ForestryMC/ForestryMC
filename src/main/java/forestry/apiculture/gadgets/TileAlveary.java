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
package forestry.apiculture.gadgets;

import buildcraft.api.statements.ITriggerExternal;
import cpw.mods.fml.common.Optional;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.IStructureLogic;
import forestry.api.core.ITileStructure;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.TileForestry;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.triggers.ForestryTrigger;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Collection;
import java.util.LinkedList;

public abstract class TileAlveary extends TileForestry implements IAlvearyComponent {

	protected TileInventoryAdapter inventory;
	protected final int componentBlockMeta;

	public TileAlveary(int componentBlockMeta) {
		this.structureLogic = new StructureLogicAlveary(this);
		this.componentBlockMeta = componentBlockMeta;
	}

	/* UPDATING */
	@Override
	public void initialize() {
		if (!ForestryBlock.alveary.isBlockEqual(worldObj, xCoord, yCoord, zCoord)) {
			Proxies.log.info("Updating alveary block at %s/%s/%s.", xCoord, yCoord, zCoord);
			worldObj.setBlock(xCoord, yCoord, zCoord, ForestryBlock.alveary.block(), componentBlockMeta, Defaults.FLAG_BLOCK_SYNCH);
			validate();
			worldObj.setTileEntity(xCoord, yCoord, zCoord, this);
		}
	}

	@Override
	public void updateEntity() {
		if (!Proxies.common.isSimulating(worldObj))
			updateClientSide();
		else {
			if (!isInited) {
				initialize();
				isInited = true;
			}

			// Periodic validation if needed
			if (worldObj.getTotalWorldTime() % 200 == 0 && (!isIntegratedIntoStructure() || isMaster()))
				validateStructure();

			updateServerSide();
		}
	}

	protected void updateServerSide() {
	}

	protected void updateClientSide() {
	}

	/* TEXTURES */
	public int getIcon(int side, int metadata) {
		return BlockAlveary.PLAIN;
	}

	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		this.isMaster = nbttagcompound.getBoolean("IsMaster");
		this.masterX = nbttagcompound.getInteger("MasterX");
		this.masterY = nbttagcompound.getInteger("MasterY");
		this.masterZ = nbttagcompound.getInteger("MasterZ");

		// Init for master state
		if (isMaster)
			makeMaster();

		structureLogic.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("IsMaster", isMaster);
		nbttagcompound.setInteger("MasterX", masterX);
		nbttagcompound.setInteger("MasterY", masterY);
		nbttagcompound.setInteger("MasterZ", masterZ);

		structureLogic.writeToNBT(nbttagcompound);
	}

	protected void createInventory() {
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
	}

	@Override
	public PacketPayload getPacketPayload() {
		return null;
	}

	/* ITILESTRUCTURE */
	IStructureLogic structureLogic;

	private boolean isMaster;
	protected int masterX, masterZ;
	protected int masterY = -99;

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
		isMaster = true;

		if (inventory == null)
			createInventory();
	}

	@Override
	public void onStructureReset() {
		setCentralTE(null);
		if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 1)
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 0);
		isMaster = false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public ITileStructure getCentralTE() {

		if (!isIntegratedIntoStructure())
			return null;

		if (!isMaster()) {
			TileEntity tile = worldObj.getTileEntity(masterX, masterY, masterZ);
			if (tile instanceof ITileStructure) {
				ITileStructure master = (ITileStructure) worldObj.getTileEntity(masterX, masterY, masterZ);
				if (master.isMaster())
					return master;
				else
					return null;
			} else
				return null;
		} else
			return this;

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
	}

	@Override
	public boolean isMaster() {
		return isMaster;
	}

	protected boolean hasMaster() {
		return masterY >= 0;
	}

	@Override
	public boolean isIntegratedIntoStructure() {
		return isMaster || masterY >= 0;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	/* IALVEARY COMPONENT */
	@Override
	public boolean hasFunction() {
		return false;
	}

	@Override
	public void addTemperatureChange(float change, float boundaryDown, float boundaryUp) {
	}

	@Override
	public void addHumidityChange(float change, float boundaryDown, float boundaryUp) {
	}

	@Override
	public void registerBeeModifier(IBeeModifier modifier) {
	}

	@Override
	public void removeBeeModifier(IBeeModifier modifier) {
	}

	@Override
	public void registerBeeListener(IBeeListener event) {
	}

	@Override
	public void removeBeeListener(IBeeListener event) {
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraft|Core")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(ForestryTrigger.missingQueen);
		res.add(ForestryTrigger.missingDrone);
		return res;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return Utils.isUseableByPlayer(player, this, worldObj, xCoord, yCoord, zCoord);
	}

}
