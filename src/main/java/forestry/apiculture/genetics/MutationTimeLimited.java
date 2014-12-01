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
package forestry.apiculture.genetics;

import java.util.Calendar;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;

public class MutationTimeLimited extends BeeMutation {

	/**
	 * Fed up with Date and Calendar and their shenanigans
	 */
	public static class DayMonth {

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
			if (equals(start) || equals(end))
				return true;
			if (start.month > end.month)
				return after(start) || before(end);
			return after(start) && before(end);
		}

		public boolean before(DayMonth other) {

			if (other.month > this.month)
				return true;

			if (other.month < this.month)
				return false;

			return this.day < other.day;
		}

		public boolean after(DayMonth other) {

			if (other.month < this.month)
				return true;

			if (other.month > this.month)
				return false;

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
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final DayMonth other = (DayMonth) obj;
			if (this.day != other.day)
				return false;
			if (this.month != other.month)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return day + "." + month;
		}
	}

	private final DayMonth start;
	private final DayMonth end;

	public MutationTimeLimited(IAllele allele0, IAllele allele1, IAllele[] template, int chance, DayMonth start) {
		this(allele0, allele1, template, chance, start, null);
	}

	public MutationTimeLimited(IAllele allele0, IAllele allele1, IAllele[] template, int chance, DayMonth start, DayMonth end) {
		super(allele0, allele1, template, chance);
		this.start = start;
		this.end = end;
	}

	@Override
	public float getChance(IBeeHousing housing, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		float chance = super.getChance(housing, allele0, allele1, genome0, genome1);

		if (chance == 0)
			return 0;

		if (start == null)
			return chance;

		DayMonth now = new DayMonth();

		// If we are equal to start day, return chance.
		if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == start.day && Calendar.getInstance().get(Calendar.MONTH) + 1 == start.month)
			return chance;

		// Still here but not time span? No mutation!
		if (end == null)
			return 0;

		// Equal to end date, return chance.
		if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == end.day && Calendar.getInstance().get(Calendar.MONTH) + 1 == end.month)
			return chance;

		// Still a chance we are in between
		if (now.between(start, end))
			return chance;

		// Now we finally failed.
		return 0;
	}
}
