package forestry.api.climate;

import java.util.function.Function;

import net.minecraft.util.math.MathHelper;

public enum ClimateStateType {
	IMMUTABLE(0.0F, 2.0F),
	MUTABLE(0.0F, 2.0F),
	CHANGE(-3.0F, 3.0F);

	public final Function<Float, Float> bounds;

	ClimateStateType(Float boundDown, Float boundUp) {
		this.bounds = v -> MathHelper.clamp(v, boundDown, boundUp);
	}
}
