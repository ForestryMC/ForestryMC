/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base logic class for Multiblock-connected tile entities.
 * Most multiblock components should derive from this.
 * Supply it an IMultiblockLogic from MultiblockManager.logicFactory
 */
public abstract class MultiblockTileEntityBase<T extends IMultiblockLogic> extends TileEntity implements IMultiblockComponent {
	private final T multiblockLogic;

	public MultiblockTileEntityBase(TileEntityType<?> tileEntityType, T multiblockLogic) {
		super(tileEntityType);
		this.multiblockLogic = multiblockLogic;
	}

	@Override
	public BlockPos getCoordinates() {
		return getBlockPos();
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
	public void load(BlockState state, CompoundNBT data) {
		super.load(state, data);
		multiblockLogic.readFromNBT(data);
	}

	@Override
	public CompoundNBT save(CompoundNBT data) {
		data = super.save(data);
		multiblockLogic.write(data);
		return data;
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		multiblockLogic.invalidate(level, this);
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		multiblockLogic.onChunkUnload(level, this);
	}

	@Override
	public final void clearRemoved() {
		super.clearRemoved();
		multiblockLogic.validate(level, this);
	}

	/* Network Communication */

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT updateTag = super.getUpdateTag();
		multiblockLogic.encodeDescriptionPacket(updateTag);
		this.encodeDescriptionPacket(updateTag);
		return updateTag;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public final void onDataPacket(NetworkManager network, SUpdateTileEntityPacket packet) {
		super.onDataPacket(network, packet);
		CompoundNBT nbtData = packet.getTag();
		multiblockLogic.decodeDescriptionPacket(nbtData);
		this.decodeDescriptionPacket(nbtData);
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		multiblockLogic.decodeDescriptionPacket(tag);
		this.decodeDescriptionPacket(tag);
	}

	/**
	 * Used to write tileEntity-specific data to the descriptionPacket
	 */
	protected void encodeDescriptionPacket(CompoundNBT packetData) {

	}

	/**
	 * Used to read tileEntity-specific data from the descriptionPacket (onDataPacket)
	 */
	protected void decodeDescriptionPacket(CompoundNBT packetData) {

	}
}
