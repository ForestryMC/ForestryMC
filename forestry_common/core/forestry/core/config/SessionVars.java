/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.config;

import java.util.HashMap;

public class SessionVars {
	
	private static HashMap<String, String> stringVars = new HashMap<String, String>();
	
	private static Class<?> openedLedger;

	public static void setOpenedLedger(Class<?> ledgerClass) {
		openedLedger = ledgerClass;
	}

	public static Class<?> getOpenedLedger() {
		return openedLedger;
	}
	
	public static void setStringVar(String ident, String val) {
		stringVars.put(ident, val);
	}
	
	public static String getStringVar(String ident) {
		if(stringVars.containsKey(ident))
			return stringVars.get(ident);
		
		return null;
	}
	
	public static void clearStringVar(String ident) {
		stringVars.remove(ident);
	}
}
