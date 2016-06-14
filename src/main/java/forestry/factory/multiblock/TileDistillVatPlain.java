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
package forestry.factory.multiblock;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.core.fluids.ITankManager;

//@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public class TileDistillVatPlain extends TileDistillVat {//implements ITriggerProvider {

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	@Override
	public IErrorLogic getErrorLogic() {return getMultiblockLogic().getController().getErrorLogic();}

	@Override
	public int getEnergyStored(EnumFacing from) { return getMultiblockLogic().getController().getEnergyManager().getEnergyStored(from); }

	@Override
	public int getMaxEnergyStored(EnumFacing from) { return getMultiblockLogic().getController().getEnergyManager().getMaxEnergyStored(from); }

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return getMultiblockLogic().getController().getEnergyManager().canConnectEnergy(from);
	}

	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return getMultiblockLogic().getController().getEnergyManager().receiveEnergy(from, maxReceive, simulate);
	}

	@Override
	@Nonnull
	public ITankManager getTankManager() { return getMultiblockLogic().getController().getTankManager(); }

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return true;
		}
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return super.getCapability(capability, facing);
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getTankManager());
		}
		return null;
	}

	/* ITRIGGERPROVIDER */
	// TODO: buildcraft for 1.9
//	@Optional.Method(modid = "BuildCraftAPI|statements")
//	@Override
//	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
//		return null;
//	}
//
//	@Optional.Method(modid = "BuildCraftAPI|statements")
//	@Override
//	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
//		Collection<ITriggerExternal> res = new ArrayList<>();
//		res.add(ApicultureTriggers.missingQueen);
//		res.add(ApicultureTriggers.missingDrone);
//		return res;
//	}
}
