package forestry.api.climate;

public interface IClimateManipulator {

	IClimateState addChange(boolean simulated);

	IClimateState removeChange(boolean simulated);

	boolean canAdd();

	void setAllowBackwards();

	void finish();
}
