/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
