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

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.core.ITileStructure;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.core.vect.Vect;

public class TileControl extends TileFarm implements IFarmListener {

	public TileControl() {
		fixedType = TYPE_CONTROL;
	}

	@Override
	protected void updateServerSide() {
		if (!isInited) {
			registerWithMaster();
		}
	}

	@Override
	public void setCentralTE(TileEntity tile) {
		super.setCentralTE(tile);
		registerWithMaster();
	}

	private boolean isInited = false;

	private void registerWithMaster() {

		isInited = true;
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
	public boolean cancelTask(IFarmLogic logic, ForgeDirection direction) {
		return hasRedstoneSignal(direction) || hasRedstoneSignal(ForgeDirection.UP) || hasRedstoneSignal(ForgeDirection.DOWN);
	}

	private boolean hasRedstoneSignal(ForgeDirection direction) {
		Vect side = new Vect(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);

		ForgeDirection opp = direction.getOpposite();
		int dir = opp.offsetZ < 0 ? 2 : opp.offsetZ > 0 ? 3 : opp.offsetX < 0 ? 4 : opp.offsetX > 0 ? 5 : 0;
		return worldObj.getIndirectPowerLevelTo(side.x, side.y, side.z, dir) > 0 || worldObj.isBlockProvidingPowerTo(side.x, side.y, side.z, dir) > 0;
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
	public void hasCultivated(IFarmLogic logic, int x, int y, int z, ForgeDirection direction, int extent) {
	}

	@Override
	public void hasScheduledHarvest(Collection<ICrop> harvested, IFarmLogic logic, int x, int y, int z, ForgeDirection direction, int extent) {
	}

}
