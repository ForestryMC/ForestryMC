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

import forestry.lepidopterology.render.RenderButterflyItem;

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

			return RenderButterflyItem.getIrregularWingYaw(flapping, flap);
		} else {
			return entity.ticksExisted + partialTicktime;
		}
	}
}
