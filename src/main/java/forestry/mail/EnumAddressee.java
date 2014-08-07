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
