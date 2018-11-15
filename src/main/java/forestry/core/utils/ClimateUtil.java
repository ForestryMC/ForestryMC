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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorState;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IClimateHelper;
import forestry.core.errors.EnumErrorCode;

public class ClimateUtil implements IClimateHelper {

	@Override
	public boolean isWithinLimits(EnumTemperature temperature, EnumHumidity humidity,
		EnumTemperature baseTemp, EnumTolerance tolTemp,
		EnumHumidity baseHumid, EnumTolerance tolHumid) {
		return getToleratedTemperature(baseTemp, tolTemp).contains(temperature) &&
			getToleratedHumidity(baseHumid, tolHumid).contains(humidity);
	}

	@Override
	public boolean isWithinLimits(EnumTemperature temperature, EnumTemperature baseTemp, EnumTolerance tolTemp) {
		return getToleratedTemperature(baseTemp, tolTemp).contains(temperature);
	}

	@Override
	public boolean isWithinLimits(EnumHumidity humidity, EnumHumidity baseHumid, EnumTolerance tolHumid) {
		return getToleratedHumidity(baseHumid, tolHumid).contains(humidity);
	}

	@Override
	public ArrayList<EnumHumidity> getToleratedHumidity(EnumHumidity prefered, EnumTolerance tolerance) {
		ArrayList<EnumHumidity> tolerated = new ArrayList<>();
		tolerated.add(prefered);

		switch (tolerance) {

			case BOTH_5:
			case BOTH_4:
			case BOTH_3:
			case BOTH_2:
				if (prefered.ordinal() + 2 < EnumHumidity.VALUES.length) {
					tolerated.add(EnumHumidity.VALUES[prefered.ordinal() + 2]);
				}
				if (prefered.ordinal() - 2 >= 0) {
					tolerated.add(EnumHumidity.VALUES[prefered.ordinal() - 2]);
				}
			case BOTH_1:
				if (prefered.ordinal() + 1 < EnumHumidity.VALUES.length) {
					tolerated.add(EnumHumidity.VALUES[prefered.ordinal() + 1]);
				}
				if (prefered.ordinal() - 1 >= 0) {
					tolerated.add(EnumHumidity.VALUES[prefered.ordinal() - 1]);
				}
				return tolerated;

			case UP_5:
			case UP_4:
			case UP_3:
			case UP_2:
				if (prefered.ordinal() + 2 < EnumHumidity.VALUES.length) {
					tolerated.add(EnumHumidity.VALUES[prefered.ordinal() + 2]);
				}
			case UP_1:
				if (prefered.ordinal() + 1 < EnumHumidity.VALUES.length) {
					tolerated.add(EnumHumidity.VALUES[prefered.ordinal() + 1]);
				}
				return tolerated;

			case DOWN_5:
			case DOWN_4:
			case DOWN_3:
			case DOWN_2:
				if (prefered.ordinal() - 2 >= 0) {
					tolerated.add(EnumHumidity.VALUES[prefered.ordinal() - 2]);
				}
			case DOWN_1:
				if (prefered.ordinal() - 1 >= 0) {
					tolerated.add(EnumHumidity.VALUES[prefered.ordinal() - 1]);
				}
				return tolerated;

			default:
				return tolerated;
		}

	}

	@Override
	public ArrayList<EnumTemperature> getToleratedTemperature(EnumTemperature prefered, EnumTolerance tolerance) {
		ArrayList<EnumTemperature> tolerated = new ArrayList<>();
		tolerated.add(prefered);

		switch (tolerance) {

			case BOTH_5:
				if (prefered.ordinal() + 5 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 5]);
				}
				if (prefered.ordinal() - 5 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 5]);
				}
			case BOTH_4:
				if (prefered.ordinal() + 4 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 4]);
				}
				if (prefered.ordinal() - 4 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 4]);
				}
			case BOTH_3:
				if (prefered.ordinal() + 3 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 3]);
				}
				if (prefered.ordinal() - 3 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 3]);
				}
			case BOTH_2:
				if (prefered.ordinal() + 2 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 2]);
				}
				if (prefered.ordinal() - 2 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 2]);
				}
			case BOTH_1:
				if (prefered.ordinal() + 1 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 1]);
				}
				if (prefered.ordinal() - 1 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 1]);
				}
				return tolerated;

			case UP_5:
				if (prefered.ordinal() + 5 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 5]);
				}
			case UP_4:
				if (prefered.ordinal() + 4 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 4]);
				}
			case UP_3:
				if (prefered.ordinal() + 3 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 3]);
				}
			case UP_2:
				if (prefered.ordinal() + 2 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 2]);
				}
			case UP_1:
				if (prefered.ordinal() + 1 < EnumTemperature.VALUES.length) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() + 1]);
				}
				return tolerated;

			case DOWN_5:
				if (prefered.ordinal() - 5 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 5]);
				}
			case DOWN_4:
				if (prefered.ordinal() - 4 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 4]);
				}
			case DOWN_3:
				if (prefered.ordinal() - 3 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 3]);
				}
			case DOWN_2:
				if (prefered.ordinal() - 2 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 2]);
				}
			case DOWN_1:
				if (prefered.ordinal() - 1 >= 0) {
					tolerated.add(EnumTemperature.VALUES[prefered.ordinal() - 1]);
				}
				return tolerated;

			default:
				return tolerated;
		}
	}

	@Override
	public String toDisplay(EnumTemperature temperature) {
		return Translator.translateToLocal("for.gui." + temperature.toString().toLowerCase(Locale.ENGLISH));
	}

	@Override
	public String toDisplay(EnumHumidity humidity) {
		return Translator.translateToLocal("for.gui." + humidity.toString().toLowerCase(Locale.ENGLISH));
	}

	public static void addClimateErrorStates(EnumTemperature temperature, EnumHumidity humidity,
		EnumTemperature baseTemp, EnumTolerance tolTemp,
		EnumHumidity baseHumid, EnumTolerance tolHumid, Set<IErrorState> errorStates) {

		if (!AlleleManager.climateHelper.isWithinLimits(temperature, baseTemp, tolTemp)) {
			if (baseTemp.ordinal() > temperature.ordinal()) {
				errorStates.add(EnumErrorCode.TOO_COLD);
			} else {
				errorStates.add(EnumErrorCode.TOO_HOT);
			}
		}

		if (!AlleleManager.climateHelper.isWithinLimits(humidity, baseHumid, tolHumid)) {
			if (baseHumid.ordinal() > humidity.ordinal()) {
				errorStates.add(EnumErrorCode.TOO_ARID);
			} else {
				errorStates.add(EnumErrorCode.TOO_HUMID);
			}
		}
	}

	public static int getColor(EnumTemperature temperature) {
		switch (temperature) {
			case ICY:
				return 0xe6e6fa;
			case COLD:
				return 0x31698a;
			case NORMAL:
				return 0xf0e9cc;
			case WARM:
				return 0xcd9b1d;
			case HOT:
				return 0xdf512e;
			case HELLISH:
				return 0x9c433e;
			case NONE:
				return 0x011f4b;
			default:
				return 0xFFFFFF;
		}
	}
}
