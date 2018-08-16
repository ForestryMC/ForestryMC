package forestry.api.climate;

import forestry.api.core.ILocatable;

public interface IClimateTransformer extends ILocatable {
	int getRange();

	IClimateState getTarget();

	IClimateState getCurrent();

	IClimateState getDefault();

	void setCircular(boolean circular);

	boolean isCircular();

	void setRange(int range);
}
