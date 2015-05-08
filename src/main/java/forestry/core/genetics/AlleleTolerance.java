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
package forestry.core.genetics;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleTolerance;

public class AlleleTolerance extends Allele implements IAlleleTolerance {

	private final EnumTolerance value;

	public AlleleTolerance(String uid, EnumTolerance value) {
		this(uid, value, false);
	}

	public AlleleTolerance(String uid, EnumTolerance value, boolean isDominant) {
		super(uid, isDominant);
		this.value = value;
	}

	public EnumTolerance getValue() {
		return value;
	}

	public String getUnlocalizedName() {
		switch (value) {
			case BOTH_1:
				return "gui.beealyzer.tolerance.both1";
			case BOTH_2:
				return "gui.beealyzer.tolerance.both2";
			case BOTH_3:
				return "gui.beealyzer.tolerance.both3";
			case BOTH_4:
				return "gui.beealyzer.tolerance.both4";
			case BOTH_5:
				return "gui.beealyzer.tolerance.both5";
			case DOWN_1:
				return "gui.beealyzer.tolerance.down1";
			case DOWN_2:
				return "gui.beealyzer.tolerance.down2";
			case DOWN_3:
				return "gui.beealyzer.tolerance.down3";
			case DOWN_4:
				return "gui.beealyzer.tolerance.down4";
			case DOWN_5:
				return "gui.beealyzer.tolerance.down5";
			case NONE:
				return "gui.beealyzer.tolerance.none";
			case UP_1:
				return "gui.beealyzer.tolerance.up1";
			case UP_2:
				return "gui.beealyzer.tolerance.up2";
			case UP_3:
				return "gui.beealyzer.tolerance.up3";
			case UP_4:
				return "gui.beealyzer.tolerance.up4";
			case UP_5:
				return "gui.beealyzer.tolerance.up5";
			default:
				return "";
		}
	}

}
