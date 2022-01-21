/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base logic class for Multiblock-connected tile entities.
 * Most multiblock components should derive from this.
 * Supply it an IMultiblockLogic from MultiblockManager.logicFactory
 */
public abstract class MultiblockTileEntityBase<T extends IMultiblockLogic> extends BlockEntity implements IMultiblockComponent {
	private final T multiblockLogic;

	public MultiblockTileEntityBase(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, T multiblockLogic) {
		super(tileEntityType, pos, state);
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
	public void load(BlockState state, CompoundTag data) {
		super.load(state, data);
		multiblockLogic.readFromNBT(data);
	}

	@Override
	public CompoundTag save(CompoundTag data) {
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
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return new ClientboundBlockEntityDataPacket(getBlockPos(), 0, getUpdateTag());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag updateTag = super.getUpdateTag();
		multiblockLogic.encodeDescriptionPacket(updateTag);
		this.encodeDescriptionPacket(updateTag);
		return updateTag;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public final void onDataPacket(Connection network, ClientboundBlockEntityDataPacket packet) {
		super.onDataPacket(network, packet);
		CompoundTag nbtData = packet.getTag();
		multiblockLogic.decodeDescriptionPacket(nbtData);
		this.decodeDescriptionPacket(nbtData);
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundTag tag) {
		super.handleUpdateTag(state, tag);
		multiblockLogic.decodeDescriptionPacket(tag);
		this.decodeDescriptionPacket(tag);
	}

	/**
	 * Used to write tileEntity-specific data to the descriptionPacket
	 */
	protected void encodeDescriptionPacket(CompoundTag packetData) {

	}

	/**
	 * Used to read tileEntity-specific data from the descriptionPacket (onDataPacket)
	 */
	protected void decodeDescriptionPacket(CompoundTag packetData) {

	}
}
