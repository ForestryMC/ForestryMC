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
package forestry.lepidopterology.entities;

public enum EnumButterflyState {

	FLYING(true), GLIDING(true), RISING(true), RESTING(false), HOVER(false);

	public static final EnumButterflyState[] VALUES = values();

	public final boolean doesMovement;

	EnumButterflyState(boolean doesMovement) {
		this.doesMovement = doesMovement;
	}

	public float getWingFlap(EntityButterfly entity, long offset, float partialTicktime) {
		if (this == RESTING || this == HOVER) {
			long systemTime = System.currentTimeMillis();
			long flapping = systemTime + offset;
			float flap = (float) (flapping % 1000) / 1000;   // 0 to 1

			return getIrregularWingYaw(flapping, flap);
		} else {
			return entity.ticksExisted + partialTicktime;
		}
	}

	public static float getIrregularWingYaw(long flapping, float flap) {
		long irregular = flapping / 1024;
		float wingYaw;

		if (irregular % 11 == 0) {
			wingYaw = 0.75f;
		} else {
			if (irregular % 7 == 0) {
				flap *= 4;
				flap = flap % 1;
			} else if (irregular % 19 == 0) {
				flap *= 6;
				flap = flap % 1;
			}
			wingYaw = getRegularWingYaw(flap);
		}

		return wingYaw;
	}

	private static float getRegularWingYaw(float flap) {
		return flap < 0.5 ? 0.75f + flap : 1.75f - flap;
	}
}
