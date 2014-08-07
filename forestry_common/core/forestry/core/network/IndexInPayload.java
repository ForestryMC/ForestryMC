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
package forestry.core.network;

/**
 * Keeps track of the indices to use when writing data to payload arrays. Internal use only.
 */
public class IndexInPayload {
	public IndexInPayload(int intIndex, int floatIndex, int stringIndex) {
		this.intIndex = intIndex;
		this.floatIndex = floatIndex;
		this.stringIndex = stringIndex;
	}

	public int intIndex = 0;
	public int floatIndex = 0;
	public int stringIndex = 0;
}
