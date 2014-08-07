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
package forestry.farming;

import forestry.core.utils.Vect;

public class FarmTarget {

	private Vect start;
	private int yOffset;
	private int extent;
	private int limit;

	public FarmTarget(Vect start) {
		this.start = start;
	}

	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setExtent(int extent) {
		this.extent = extent;
	}

	public Vect getStart() {
		return start;
	}

	public int getYOffset() {
		return this.yOffset;
	}

	public int getLimit() {
		return limit;
	}

	public int getExtent() {
		return extent;
	}
}
