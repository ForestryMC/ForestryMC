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

import net.minecraft.item.ItemStack;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;

public class CircuitRecipe {

	private final ICircuitLayout layout;
	private final ItemStack resource;
	private final ICircuit circuit;

	public CircuitRecipe(ICircuitLayout layout, ItemStack resource, ICircuit circuit) {
		this.resource = resource;
		this.layout = layout;
		this.circuit = circuit;
	}

	public boolean matches(ICircuitLayout layout, ItemStack itemstack) {
		if (!this.layout.getUID().equals(layout.getUID())) {
			return false;
		}

		return itemstack.isItemEqual(resource);
	}

	public ICircuit getCircuit() {
		return circuit;
	}

	public ItemStack getResource() {
		return resource;
	}
}
