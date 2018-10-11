package forestry.core.climate;

import com.google.common.base.MoreObjects;

import java.util.function.Function;

import forestry.api.climate.IClimateState;

class MutableClimateState implements IClimateState {
	protected float temperature;
	protected float humidity;
	protected float clamp;

	MutableClimateState(IClimateState climateState) {
		this(climateState.getTemperature(), climateState.getHumidity());
	}

	MutableClimateState(float temperature, float humidity) {
		this.temperature = temperature;
		this.humidity = humidity;
	}

	@Override
	public IClimateState copy(boolean mutable) {
		return ClimateStateHelper.INSTANCE.create(this, mutable);
	}

	@Override
	public IClimateState copy() {
		return copy(true);
	}

	@Override
	public IClimateState toMutable() {
		return this;
	}

	@Override
	public IClimateState toImmutable() {
		return copy(false);
	}

	@Override
	public IClimateState setTemperature(float temperature) {
		this.temperature = temperature;
		return ClimateStateHelper.INSTANCE.checkState(this);
	}

	@Override
	public IClimateState setHumidity(float humidity) {
		this.humidity = humidity;
		return ClimateStateHelper.INSTANCE.checkState(this);
	}

	@Override
	public IClimateState setClimate(float temperature, float humidity) {
		this.temperature = temperature;
		this.humidity = humidity;
		return ClimateStateHelper.INSTANCE.checkState(this);
	}

	@Override
	public IClimateState addTemperature(float temperature) {
		this.temperature += temperature;
		return ClimateStateHelper.INSTANCE.checkState(this);
	}

	@Override
	public IClimateState addHumidity(float humidity) {
		this.humidity += humidity;
		return ClimateStateHelper.INSTANCE.checkState(this);
	}

	@Override
	public IClimateState add(IClimateState state) {
		this.humidity += state.getHumidity();
		this.temperature += state.getTemperature();
		return ClimateStateHelper.INSTANCE.checkState(this);
	}

	@Override
	public IClimateState multiply(double factor) {
		this.humidity *= factor;
		this.temperature *= factor;
		return ClimateStateHelper.INSTANCE.checkState(this);
	}

	@Override
	public IClimateState subtract(IClimateState state) {
		this.humidity -= state.getHumidity();
		this.temperature -= state.getTemperature();
		return ClimateStateHelper.INSTANCE.checkState(this);
	}

	@Override
	public IClimateState map(Function<Float, Float> mapper) {
		temperature = mapper.apply(temperature);
		humidity = mapper.apply(humidity);
		return this;
	}

	@Override
	public boolean isPresent() {
		return !Float.isNaN(temperature) && !Float.isNaN(humidity);
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public boolean isClamped() {
		return temperature < 2.0F && temperature >= 0.0F && humidity < 2.0F && humidity >= 0.0F;
	}

	@Override
	public float getTemperature() {
		return temperature;
	}

	@Override
	public float getHumidity() {
		return humidity;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IClimateState)) {
			return false;
		}
		IClimateState otherState = (IClimateState) obj;
		return otherState.getTemperature() == temperature && otherState.getHumidity() == humidity;
	}

	@Override
	public int hashCode() {
		return Float.hashCode(temperature) * 31 + Float.hashCode(humidity);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("temperature", temperature).add("humidity", humidity).toString();
	}
}
