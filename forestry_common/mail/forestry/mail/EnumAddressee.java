/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail;

public enum EnumAddressee {
	PLAYER, TRADER;
	
	public static EnumAddressee fromString(String ident) {
		for(EnumAddressee addr : values()) {
			if(addr.toString().equals(ident))
				return addr;
		}
		
		return null;
	}
}
