/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.circuits;

import forestry.api.circuits.ICircuitLayout;
import forestry.core.utils.StringUtil;

public class CircuitLayout implements ICircuitLayout {

	String uid;

	public CircuitLayout(String uid) {
		this.uid = uid;
	}

	@Override
	public String getUID() {
		return "forestry." + this.uid;
	}

	@Override
	public String getName() {
		return StringUtil.localize("layout." + this.uid);
	}

	@Override
	public String getUsage() {
		return StringUtil.localize("usage." + this.uid);
	}
}
