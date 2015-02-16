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

public class POBoxInfo {
	public final int playerLetters;
	public final int tradeLetters;

	public POBoxInfo(int playerLetters, int tradeLetters) {
		this.playerLetters = playerLetters;
		this.tradeLetters = tradeLetters;
	}

	public boolean hasMail() {
		return playerLetters > 0 || tradeLetters > 0;
	}
}
