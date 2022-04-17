/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

/**
 * Base logic class for Multiblock-connected tile entities.
 * Most multiblock components should derive from this.
 * Supply it an IMultiblockLogic from MultiblockManager.logicFactory
 */
public abstract class MultiblockTileEntityBase<T extends IMultiblockLogic> extends TileEntity implements IMultiblockComponent {
	private final T multiblockLogic;

	public MultiblockTileEntityBase(T multiblockLogic) {
		this.multiblockLogic = multiblockLogic;
	}

	@Override
	public ChunkCoordinates getCoordinates() {
		return new ChunkCoordinates(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public T getMultiblockLogic() {
		return multiblockLogic;
	}

	@Override
	public abstract void onMachineAssembled(IMultiblockController multiblockController, ChunkCoordinates minCoord, ChunkCoordinates maxCoord);

	@Override
	public abstract void onMachineBroken();

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		multiblockLogic.readFromNBT(data);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		multiblockLogic.writeToNBT(data);
	}

	/**
	 * Generally, TileEntities that are part of a multiblock should not subscribe to updates
	 * from the main game loop. Instead, you should have lists of TileEntities which need to
	 * be notified during an update() in your Controller and perform callbacks from there.
	 * @see net.minecraft.tileentity.TileEntity#canUpdate()
	 */
	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public final void invalidate() {
		super.invalidate();
		multiblockLogic.invalidate(worldObj, this);
	}

	@Override
	public final void onChunkUnload() {
		super.onChunkUnload();
		multiblockLogic.onChunkUnload(worldObj, this);
	}

	@Override
	public final void validate() {
		super.validate();
		multiblockLogic.validate(worldObj, this);
	}

	/* Network Communication */

	@Override
	public final Packet getDescriptionPacket() {
		NBTTagCompound packetData = new NBTTagCompound();
		multiblockLogic.encodeDescriptionPacket(packetData);
		this.encodeDescriptionPacket(packetData);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, packetData);
	}
	
	@Override
	public final void onDataPacket(NetworkManager network, S35PacketUpdateTileEntity packet) {
		NBTTagCompound nbtData = packet.func_148857_g();
		multiblockLogic.decodeDescriptionPacket(nbtData);
		this.decodeDescriptionPacket(nbtData);
	}

	/**
	 * Used to write tileEntity-specific data to the descriptionPacket
	 */
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {

	}

	/**
	 * Used to read tileEntity-specific data from the descriptionPacket (onDataPacket)
	 */
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {

	}
}
