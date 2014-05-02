/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.circuits;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.IFarmHousing;
import forestry.core.circuits.Circuit;
import forestry.farming.logic.FarmLogic;

public class CircuitFarmLogic extends Circuit {

	private Class<? extends FarmLogic> logicClass;
	private boolean isManual = false;

	public CircuitFarmLogic(String uid, Class<? extends FarmLogic> logicClass, String[] descriptions) {
		super(uid, false, uid, descriptions);
		this.logicClass = logicClass;
		setLimit(4);
	}

	public CircuitFarmLogic setManual() {
		isManual = true;
		return this;
	}

	@Override
	public boolean isCircuitable(TileEntity tile) {
		return tile instanceof IFarmHousing;
	}

	@Override
	public void onInsertion(int slot, TileEntity tile) {
		if (!isCircuitable(tile))
			return;

		FarmLogic logic = null;
		try {
			logic = logicClass.getConstructor(new Class[] { IFarmHousing.class }).newInstance(new Object[] { (IFarmHousing) tile });
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate logic of class " + logicClass.getName() + ": " + ex.getMessage());
		}

		if (logic != null) {
			logic.setManual(isManual);
			((IFarmHousing) tile).setFarmLogic(ForgeDirection.values()[slot + 2], logic);
		}
	}

	@Override
	public void onLoad(int slot, TileEntity tile) {
		onInsertion(slot, tile);
	}

	@Override
	public void onRemoval(int slot, TileEntity tile) {
		if (!isCircuitable(tile))
			return;

		((IFarmHousing) tile).resetFarmLogic(ForgeDirection.values()[slot + 2]);
	}

	@Override
	public void onTick(int slot, TileEntity tile) {
	}

}
