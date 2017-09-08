/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.util.math.MathHelper;

public enum ClimateStateType {
	DEFAULT(0.0F, 2.0F),
	EXTENDED(-3.0F, 3.0F);

	public final float boundDown, boundUp;

	ClimateStateType(Float boundDown, Float boundUp) {
		this.boundDown = boundDown;
		this.boundUp = boundUp;
	}

	public float clamp(float value) {
		return MathHelper.clamp(value, boundDown, boundUp);
	}
}
