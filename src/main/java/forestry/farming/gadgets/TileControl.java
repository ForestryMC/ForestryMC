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

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.core.ITileStructure;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.core.vect.Vect;

public class TileControl extends TileFarm implements IFarmListener {

	private boolean isRegistered = false;

	public TileControl() {
		fixedType = TYPE_CONTROL;
	}

	@Override
	protected void updateServerSide() {
		if (!isRegistered) {
			registerWithMaster();
			isRegistered = true;
		}
	}

	@Override
	public void setCentralTE(TileEntity tile) {
		super.setCentralTE(tile);
		registerWithMaster();
	}

	private void registerWithMaster() {

		if (!hasMaster()) {
			return;
		}

		ITileStructure central = getCentralTE();
		if (!(central instanceof IFarmComponent)) {
			return;
		}

		((IFarmComponent) central).registerListener(this);
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* IFARMLISTENER */
	@Override
	public boolean cancelTask(IFarmLogic logic, EnumFacing direction) {
		return hasRedstoneSignal(direction) || hasRedstoneSignal(EnumFacing.UP) || hasRedstoneSignal(EnumFacing.DOWN);
	}

	private boolean hasRedstoneSignal(EnumFacing direction) {
		Vect side = new Vect(pos.offset(direction));

		EnumFacing opp = direction.getOpposite();
		if(opp.getAxis() == EnumFacing.Axis.Y) {
			opp = EnumFacing.DOWN;
		}

		return worldObj.getRedstonePower(side.toBlockPos(), opp) > 0 || worldObj.getStrongPower(side.toBlockPos(), opp) > 0;
	}

	@Override
	public boolean beforeCropHarvest(ICrop crop) {
		return false;
	}

	@Override
	public void afterCropHarvest(Collection<ItemStack> harvested, ICrop crop) {
	}

	@Override
	public void hasCollected(Collection<ItemStack> collected, IFarmLogic logic) {
	}

	@Override
	public void hasCultivated(IFarmLogic logic, BlockPos pos, EnumFacing direction, int extent) {
	}

	@Override
	public void hasScheduledHarvest(Collection<ICrop> harvested, IFarmLogic logic, BlockPos pos, EnumFacing direction, int extent) {
	}

}
