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

import javax.annotation.Nullable;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmCircuit;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.core.circuits.Circuit;

public class CircuitFarmLogic extends Circuit implements IFarmCircuit {

	private final IFarmLogic logic;

	public CircuitFarmLogic(String uid, IFarmProperties instance, boolean manual) {
		super(uid);
		this.logic = instance.getLogic(manual);
	}

	@SuppressWarnings("unused")
	public CircuitFarmLogic(String uid, IFarmLogic logic) {
		super(uid);
		this.logic = logic;
	}

	@Override
	public String getUnlocalizedName() {
		return logic.getUnlocalizedName();
	}

	@Override
	public String getLocalizedName() {
		return logic.getName();
	}

	@Override
	public IFarmLogic getFarmLogic() {
		return logic;
	}

	@Override
	public boolean isCircuitable(Object tile) {
		return tile instanceof IFarmHousing;
	}

	@Nullable
	private IFarmHousing getCircuitable(Object tile) {
		if (!isCircuitable(tile)) {
			return null;
		}
		return (IFarmHousing) tile;
	}

	@Override
	public void onInsertion(int slot, Object tile) {
		IFarmHousing housing = getCircuitable(tile);
		if (housing == null) {
			return;
		}

		housing.setFarmLogic(FarmDirection.values()[slot], logic);
	}

	@Override
	public void onLoad(int slot, Object tile) {
		onInsertion(slot, tile);
	}

	@Override
	public void onRemoval(int slot, Object tile) {
		IFarmHousing farmHousing = getCircuitable(tile);
		if (farmHousing == null) {
			return;
		}

		farmHousing.resetFarmLogic(FarmDirection.values()[slot]);
	}

	@Override
	public void onTick(int slot, Object tile) {
	}

}
