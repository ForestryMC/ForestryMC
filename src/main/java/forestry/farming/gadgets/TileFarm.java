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

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IStructureLogic;
import forestry.api.core.ITileStructure;
import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmListener;
import forestry.core.gadgets.TileForestry;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;

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
			player.openGui(ForestryAPI.instance, GuiId.MultiFarmGUI.ordinal(), worldObj, pos.getX(), pos.getY(), pos.getZ());
		} else if (this.hasMaster()) {
			player.openGui(ForestryAPI.instance, GuiId.MultiFarmGUI.ordinal(), worldObj, masterPos.getX(), masterPos.getY(), masterPos.getZ());
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		this.isMaster = nbttagcompound.getBoolean("IsMaster");
		if(nbttagcompound.hasKey("MasterX"))
		{
			int masterX = nbttagcompound.getInteger("MasterX");
			int masterY = nbttagcompound.getInteger("MasterY");
			int masterZ = nbttagcompound.getInteger("MasterZ");
			masterPos = new BlockPos(masterX, masterY, masterZ);
		}

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
		if(masterPos != null)
		{
			nbttagcompound.setInteger("MasterX", masterPos.getX());
			nbttagcompound.setInteger("MasterY", masterPos.getY());
			nbttagcompound.setInteger("MasterZ", masterPos.getZ());
		}

		farmBlock.saveToCompound(nbttagcompound);

		structureLogic.writeToNBT(nbttagcompound);
	}

	/* UPDATING */
	@Override
	public void initialize() {
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

	/* TILEFORESTRY */
	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(0, 3);
		payload.shortPayload[0] = (short) farmBlock.ordinal();

		payload.shortPayload[1] = (short) (isMaster() ? 1 : 0);

		// so the client can know if it is part of an integrated structure
		payload.shortPayload[2] = (short) masterPos.getY();

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {
		EnumFarmBlock farmType = EnumFarmBlock.values()[payload.shortPayload[0]];
		if (payload.shortPayload[1] > 0 && !isMaster()) {
			makeMaster();
		}

		// so the client can know if it is part of an integrated structure
		masterPos = new BlockPos(masterPos.getX(), payload.shortPayload[2], masterPos.getZ());

		if (this.farmBlock != farmType) {
			this.farmBlock = farmType;
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	/* ITILESTRUCTURE */
	private final IStructureLogic structureLogic;
	private boolean isMaster;
	private BlockPos masterPos = new BlockPos(0, -99, 0);

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
		IBlockState state = worldObj.getBlockState(pos);
		if (state.getBlock().getMetaFromState(state) == 1) {
			worldObj.setBlockState(pos, state.getBlock().getStateFromMeta(0), 0);
		}
		isMaster = false;
		worldObj.markBlockForUpdate(pos);
	}

	@Override
	public ITileStructure getCentralTE() {

		if (!isIntegratedIntoStructure()) {
			return null;
		}

		if (isMaster) {
			return this;
		}

		TileEntity tile = worldObj.getTileEntity(masterPos);
		if (tile instanceof ITileStructure) {
			ITileStructure master = (ITileStructure) tile;
			if (master.isMaster()) {
				return master;
			}
		}
		return null;
	}

	private boolean isSameTile(TileEntity tile) {
		return pos.equals(tile.getPos());
	}

	@Override
	public void setCentralTE(TileEntity tile) {
		if (tile == null || tile == this || isSameTile(tile)) {
			masterPos = new BlockPos(0, -99, 0);
			return;
		}

		this.isMaster = false;
		masterPos = tile.getPos();

		worldObj.markBlockForUpdate(pos);
	}

	@Override
	public boolean isMaster() {
		return isMaster;
	}

	protected boolean hasMaster() {
		return masterPos.getY() >= 0;
	}

	@Override
	public final IInventory getInventory() {
		return getStructureInventory();
	}

	@Override
	public IInventoryAdapter getStructureInventory() {
		return getInternalInventory();
	}

	@Override
	public boolean isIntegratedIntoStructure() {
		return isMaster || masterPos.getY() >= 0;
	}

	@Override
	public void registerListener(IFarmListener listener) {
	}

	@Override
	public void removeListener(IFarmListener listener) {
	}
}
