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
package forestry.farming.circuits;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.circuits.Circuit;

public class CircuitFarmLogic extends Circuit {

	private final Class<? extends IFarmLogic> logicClass;
	private boolean isManual = false;

	public CircuitFarmLogic(String uid, Class<? extends IFarmLogic> logicClass) {
		super(uid, false);
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

	IFarmHousing getCircuitable(TileEntity tile) {
		if (!isCircuitable(tile)) {
			return null;
		}
		return (IFarmHousing) tile;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void onInsertion(int slot, TileEntity tile) {
		IFarmHousing housing = getCircuitable(tile);
		if (housing == null) {
			return;
		}

		IFarmLogic logic;
		try {
			logic = logicClass.getConstructor(new Class[]{IFarmHousing.class}).newInstance(housing);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate logic of class " + logicClass.getName() + ": " + ex.getMessage());
		}

		try {
			logic.setManual(isManual);
		} catch (Throwable e) {
			// uses older version of the API that doesn't implement setManual
		}
		housing.setFarmLogic(EnumFacing.values()[slot + 2], logic);
	}

	@Override
	public void onLoad(int slot, TileEntity tile) {
		onInsertion(slot, tile);
	}

	@Override
	public void onRemoval(int slot, TileEntity tile) {
		if (!isCircuitable(tile)) {
			return;
		}

		((IFarmHousing) tile).resetFarmLogic(EnumFacing.values()[slot + 2]);
	}

	@Override
	public void onTick(int slot, TileEntity tile) {
	}

}
