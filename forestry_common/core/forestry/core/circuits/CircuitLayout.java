/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
