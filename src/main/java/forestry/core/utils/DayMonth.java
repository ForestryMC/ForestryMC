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
package forestry.core.utils;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Fed up with Date and Calendar and their shenanigans
 */
public class DayMonth {

	public final int day;
	public final int month;

	public DayMonth() {
		this.day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		this.month = Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public DayMonth(int day, int month) {
		this.day = day;
		this.month = month;
	}

	public boolean between(DayMonth start, DayMonth end) {
		if (equals(start) || equals(end)) {
			return true;
		}
		if (start.month > end.month) {
			return after(start) || before(end);
		}
		return after(start) && before(end);
	}

	public boolean before(DayMonth other) {

		if (other.month > this.month) {
			return true;
		}

		if (other.month < this.month) {
			return false;
		}

		return this.day < other.day;
	}

	public boolean after(DayMonth other) {

		if (other.month < this.month) {
			return true;
		}

		if (other.month > this.month) {
			return false;
		}

		return this.day > other.day;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + this.day;
		hash = 89 * hash + this.month;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DayMonth other = (DayMonth) obj;
		if (this.day != other.day) {
			return false;
		}
		if (this.month != other.month) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String monthName = new DateFormatSymbols().getMonths()[month - 1];
		return monthName + ' ' + day;
	}

}
