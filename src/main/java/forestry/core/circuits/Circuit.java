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
import forestry.core.utils.Translator;

public abstract class Circuit implements ICircuit {
	private final String uid;

	protected Circuit(String uid) {
		this.uid = uid;

		ChipsetManager.circuitRegistry.registerCircuit(this);
	}

	@Override
	public String getUID() {
		return "forestry." + this.uid;
	}

	@Override
	public String getUnlocalizedName() {
		return "for.circuit." + this.uid;
	}

	@Override
	public void addTooltip(List<String> list) {
		list.add(getLocalizedName());

		int i = 1;
		while (true) {
			String unlocalizedDescription = getUnlocalizedName() + ".description." + i;
			String description = Translator.translateToLocal(unlocalizedDescription);
			if (description.endsWith(unlocalizedDescription)) {
				break;
			}
			list.add(" - " + description);
			i++;
		}
	}
}
