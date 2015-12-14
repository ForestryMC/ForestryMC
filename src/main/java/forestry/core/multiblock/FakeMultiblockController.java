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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.access.EnumAccess;
import forestry.core.access.FakeAccessHandler;
import forestry.core.access.IAccessHandler;
import forestry.core.errors.FakeErrorLogic;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;

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
	public ChunkCoordinates getReferenceCoord() {
		return null;
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
	public World getWorld() {
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

	@Nonnull
	@Override
	public Set<IMultiblockComponent> checkForDisconnections() {
		return Collections.emptySet();
	}

	@Nonnull
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
	@Nonnull
	public Collection<IMultiblockComponent> getComponents() {
		return Collections.emptyList();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {

	}

	@Override
	public IAccessHandler getAccessHandler() {
		return FakeAccessHandler.getInstance();
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
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {

	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {

	}
}
