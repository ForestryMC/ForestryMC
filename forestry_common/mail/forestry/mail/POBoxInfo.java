/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
