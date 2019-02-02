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

import java.util.Locale;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.render.ColourProperties;

public enum EnumCircuitBoardType {
	BASIC(1),
	ENHANCED(2),
	REFINED(3),
	INTRICATE(4);

	private final int sockets;

	EnumCircuitBoardType(int sockets) {
		this.sockets = sockets;
	}

	public int getSockets() {
		return sockets;
	}

	@SideOnly(Side.CLIENT)
	public int getPrimaryColor() {
		return ColourProperties.INSTANCE.get("item.circuit." + name().toLowerCase(Locale.ENGLISH) + ".primary");
	}

	@SideOnly(Side.CLIENT)
	public int getSecondaryColor() {
		return ColourProperties.INSTANCE.get("item.circuit." + name().toLowerCase(Locale.ENGLISH) + ".secondary");
	}
}
