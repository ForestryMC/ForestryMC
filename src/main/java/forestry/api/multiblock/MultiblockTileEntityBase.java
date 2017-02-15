/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public BlockPos getCoordinates() {
		return getPos();
	}

	@Override
	public T getMultiblockLogic() {
		return multiblockLogic;
	}

	@Override
	public abstract void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord);

	@Override
	public abstract void onMachineBroken();

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		multiblockLogic.readFromNBT(data);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		multiblockLogic.writeToNBT(data);
		return data;
	}

	@Override
	public final void invalidate() {
		super.invalidate();
		multiblockLogic.invalidate(world, this);
	}

	@Override
	public final void onChunkUnload() {
		super.onChunkUnload();
		multiblockLogic.onChunkUnload(world, this);
	}

	@Override
	public final void validate() {
		super.validate();
		multiblockLogic.validate(world, this);
	}

	/* Network Communication */

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound updateTag = super.getUpdateTag();
		multiblockLogic.encodeDescriptionPacket(updateTag);
		this.encodeDescriptionPacket(updateTag);
		return updateTag;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void onDataPacket(NetworkManager network, SPacketUpdateTileEntity packet) {
		super.onDataPacket(network, packet);
		NBTTagCompound nbtData = packet.getNbtCompound();
		multiblockLogic.decodeDescriptionPacket(nbtData);
		this.decodeDescriptionPacket(nbtData);
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		multiblockLogic.decodeDescriptionPacket(tag);
		this.decodeDescriptionPacket(tag);
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
