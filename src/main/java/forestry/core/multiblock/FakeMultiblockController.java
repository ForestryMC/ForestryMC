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
package forestry.core.multiblock;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.errors.FakeErrorLogic;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.FakeOwnerHandler;
import forestry.core.owner.IOwnerHandler;

public abstract class FakeMultiblockController implements IMultiblockControllerInternal {
	@Override
	public void attachBlock(IMultiblockComponent part) {

	}

	@Override
	public void detachBlock(IMultiblockComponent part, boolean chunkUnloading) {

	}

	@Override
	public void checkIfMachineIsWhole() {

	}

	@Override
	public void assimilate(IMultiblockControllerInternal other) {

	}

	@Override
	public void _onAssimilated(IMultiblockControllerInternal otherController) {

	}

	@Override
	public void onAssimilated(IMultiblockControllerInternal assimilator) {

	}

	@Override
	public void updateMultiblockEntity() {

	}

	@Override
	public BlockPos getReferenceCoord() {
		return BlockPos.ORIGIN;
	}

	@Override
	public void recalculateMinMaxCoords() {

	}

	@Override
	public void formatDescriptionPacket(NBTTagCompound data) {

	}

	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {

	}

	@Override
	public World getWorldObj() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean shouldConsume(IMultiblockControllerInternal otherController) {
		return false;
	}

	@Override
	public String getPartsListString() {
		return "";
	}

	@Override
	public void auditParts() {

	}


	@Override
	public Set<IMultiblockComponent> checkForDisconnections() {
		return Collections.emptySet();
	}


	@Override
	public Set<IMultiblockComponent> detachAllBlocks() {
		return Collections.emptySet();
	}

	@Override
	public boolean isAssembled() {
		return false;
	}

	@Override
	public void reassemble() {

	}

	@Override
	public String getLastValidationError() {
		return null;
	}

	@Override
	public BlockPos getLastValidationErrorPosition() {
		return null;
	}

	@Override

	public Collection<IMultiblockComponent> getComponents() {
		return Collections.emptyList();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		return nbttagcompound;
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return FakeOwnerHandler.getInstance();
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.NORMAL;
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.NORMAL;
	}

	@Override
	public float getExactTemperature() {
		return 0.5f;
	}

	@Override
	public float getExactHumidity() {
		return 0.5f;
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return FakeErrorLogic.instance;
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {

	}

	@Override
	public void readGuiData(PacketBufferForestry data) {

	}
}
