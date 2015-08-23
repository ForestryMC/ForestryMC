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

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.IStructureLogic;
import forestry.api.core.ITileStructure;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.TileForestry;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;

import buildcraft.api.statements.ITriggerExternal;

public abstract class TileAlveary extends TileForestry implements IAlvearyComponent {

	private final IStructureLogic structureLogic;
	private boolean isMaster;
	protected BlockPos masterPos = new BlockPos(0, -99, 0);
	protected final int componentBlockMeta;

	public TileAlveary(int componentBlockMeta) {
		this.structureLogic = new StructureLogicAlveary(this);
		this.componentBlockMeta = componentBlockMeta;
	}

	/* UPDATING */
	@Override
	public void initialize() {
		if (!ForestryBlock.alveary.isBlockEqual(worldObj, pos)) {
			Proxies.log.info("Updating alveary block at %s/%s/%s.", pos);
			worldObj.setBlockState(pos, ForestryBlock.alveary.block().getStateFromMeta(componentBlockMeta), Defaults.FLAG_BLOCK_SYNCH);
			validate();
			worldObj.setTileEntity(pos, this);
		}
	}

	@Override
	public void update() {
		super.update();

		if (!Proxies.common.isSimulating(worldObj)) {
			updateClientSide();
		} else {
			// Periodic validation if needed
			if (updateOnInterval(200) && (!isIntegratedIntoStructure() || isMaster())) {
				validateStructure();
			}

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
		this.isMaster = nbttagcompound.getBoolean("IsMaster");
		int masterX = nbttagcompound.getInteger("MasterX");
		int masterY = nbttagcompound.getInteger("MasterY");
		int masterZ = nbttagcompound.getInteger("MasterZ");
		masterPos = new BlockPos(masterX, masterY, masterZ);

		// Init for master state
		if (isMaster) {
			makeMaster();
		}
		
		super.readFromNBT(nbttagcompound);

		structureLogic.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("IsMaster", isMaster);
		nbttagcompound.setInteger("MasterX", masterPos.getX());
		nbttagcompound.setInteger("MasterY", masterPos.getY());
		nbttagcompound.setInteger("MasterZ", masterPos.getZ());

		structureLogic.writeToNBT(nbttagcompound);
	}

	protected void createInventory() {
	}

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(0, 2);
		payload.shortPayload[0] = (short) (isMaster() ? 1 : 0);

		// so the client can know if it is part of an integrated structure
		payload.shortPayload[1] = (short) pos.getY();

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		if (payload.shortPayload[0] > 0) {
			makeMaster();
		}

		// so the client can know if it is part of an integrated structure
		int masterY = payload.shortPayload[1];
		masterPos = new BlockPos(masterPos.getX(), masterY, masterPos.getZ());
	}

	/* ITILESTRUCTURE */
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

		if (getInternalInventory() instanceof FakeInventoryAdapter) {
			createInventory();
		}
	}

	@Override
	public void onStructureReset() {
		setCentralTE(null);
		IBlockState state = worldObj.getBlockState(pos);
		Block block = state.getBlock();
		if (block.getMetaFromState(state) == 1) {
			worldObj.setBlockState(pos, block.getStateFromMeta(0), 0);
		}
		isMaster = false;
		worldObj.markBlockForUpdate(pos);
	}

	@Override
	public ITileStructure getCentralTE() {

		if (!isIntegratedIntoStructure()) {
			return null;
		}

		if (!isMaster()) {
			TileEntity tile = worldObj.getTileEntity(masterPos);
			if (tile instanceof ITileStructure) {
				ITileStructure master = (ITileStructure) worldObj.getTileEntity(masterPos);
				if (master.isMaster()) {
					return master;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return this;
		}

	}

	private boolean isSameTile(TileEntity tile) {
		return pos.equals(tile.getPos());
	}

	@Override
	public void setCentralTE(TileEntity tile) {
		if (tile == null || tile == this || isSameTile(tile)) {
			this.masterPos = new BlockPos(0, -99, 0);
			return;
		}

		this.isMaster = false;
		this.masterPos = tile.getPos();
	}

	@Override
	public boolean isMaster() {
		return isMaster;
	}

	protected boolean hasMaster() {
		return masterPos.getY() >= 0;
	}

	@Override
	public boolean isIntegratedIntoStructure() {
		return isMaster || masterPos.getY() >= 0;
	}

	@Override
	public final IInventoryAdapter getInventory() {
		return getStructureInventory();
	}

	@Override
	public IInventoryAdapter getStructureInventory() {
		return getInternalInventory();
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
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<ITriggerExternal>();
		res.add(ApicultureTriggers.missingQueen);
		res.add(ApicultureTriggers.missingDrone);
		return res;
	}

}
