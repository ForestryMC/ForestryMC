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
package forestry.core.config;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SessionVars {

	private static final Map<String, String> stringVars = new HashMap<>();

	@Nullable
	private static Class<?> openedLedger;

	public static void setOpenedLedger(@Nullable Class<?> ledgerClass) {
		openedLedger = ledgerClass;
	}

	@Nullable
	public static Class<?> getOpenedLedger() {
		return openedLedger;
	}

	public static void setStringVar(String ident, String val) {
		stringVars.put(ident, val);
	}

	@Nullable
	public static String getStringVar(String ident) {
		return stringVars.get(ident);
	}

	public static void clearStringVar(String ident) {
		stringVars.remove(ident);
	}
}
