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
package forestry.core.circuits;

import java.util.List;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.core.utils.StringUtil;

public abstract class Circuit implements ICircuit {

	private final String uid;
	private final boolean requiresDiscovery;
	private int limit = 1;

	protected Circuit(String uid, boolean requiresDiscovery) {
		this.uid = uid;
		this.requiresDiscovery = requiresDiscovery;

		ChipsetManager.circuitRegistry.registerCircuit(this);
	}

	protected void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public String getUID() {
		return "forestry." + this.uid;
	}

	@Override
	public int getLimit() {
		return this.limit;
	}

	@Override
	public boolean requiresDiscovery() {
		return this.requiresDiscovery;
	}

	@Override
	public String getName() {
		return "circuit." + this.uid;
	}

	@Override
	public void addTooltip(List<String> list) {
		list.add(StringUtil.localize(getName()));

		int i = 1;
		while (true) {
			String unlocalizedDescription = getName() + ".description." + i;
			String description = StringUtil.localize(unlocalizedDescription);
			if (description.endsWith(unlocalizedDescription)) {
				break;
			}
			list.add(" - " + description);
			i++;
		}
	}

	public static ICircuit energyElectricChoke1;
	public static ICircuit energyElectricEfficiency1;
	public static ICircuit energyElectricBoost1;
	public static ICircuit energyElectricBoost2;

	public static ICircuit farmArborealManaged;
	public static ICircuit farmShroomManaged;
	public static ICircuit farmCerealManaged;
	public static ICircuit farmVegetableManaged;
	public static ICircuit farmPeatManaged;
	public static ICircuit farmSucculentManaged;
	public static ICircuit farmPoaleManaged;
	public static ICircuit farmFungal;
	public static ICircuit farmInfernalManaged;
	public static ICircuit farmGourdManaged;

	public static ICircuit farmShroomManual;
	public static ICircuit farmCerealManual;
	public static ICircuit farmVegetableManual;
	public static ICircuit farmPeatManual;
	public static ICircuit farmSucculentManual;
	public static ICircuit farmPoalesManual;
	public static ICircuit farmGourdManual;
	public static ICircuit farmCocoaManual;

	public static ICircuit farmOrchardManual;
	public static ICircuit farmRubberManual;
	public static ICircuit farmEnderManaged;

	public static ICircuit machineSpeedUpgrade1;
	public static ICircuit machineSpeedUpgrade2;
	public static ICircuit machineEfficiencyUpgrade1;
}
