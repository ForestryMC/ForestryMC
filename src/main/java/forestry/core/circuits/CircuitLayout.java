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

import javax.annotation.Nonnull;

import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ICircuitSocketType;
import forestry.core.utils.Translator;

public class CircuitLayout implements ICircuitLayout {

	@Nonnull
	private final String uid;
	@Nonnull
	private final ICircuitSocketType socketType;

	public CircuitLayout(@Nonnull String uid, @Nonnull ICircuitSocketType socketType) {
		this.uid = uid;
		this.socketType = socketType;
	}

	@Nonnull
	@Override
	public String getUID() {
		return "forestry." + this.uid;
	}

	@Nonnull
	@Override
	public String getName() {
		return Translator.translateToLocal("for.circuit.layout." + this.uid + ".name");
	}

	@Nonnull
	@Override
	public String getUsage() {
		return Translator.translateToLocal("for.circuit.layout." + this.uid + ".usage");
	}

	@Nonnull
	@Override
	public ICircuitSocketType getSocketType() {
		return socketType;
	}
}
