/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
import forestry.core.utils.Vect;

public class TileControl extends TileFarm implements IFarmListener {

	public TileControl() {
		fixedType = TYPE_CONTROL;
	}

	@Override
	protected void updateServerSide() {
		if (!isInited)
			registerWithMaster();
	}

	@Override
	public void setCentralTE(TileEntity tile) {
		super.setCentralTE(tile);
		registerWithMaster();
	}

	private boolean isInited = false;

	private void registerWithMaster() {

		isInited = true;
		if (!hasMaster())
			return;

		ITileStructure central = getCentralTE();
		if (!(central instanceof IFarmComponent))
			return;

		((IFarmComponent) central).registerListener(this);
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	@Override
	protected void createInventory() {
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
