package forestry.api.climate;

public class LogicInfo {
	public final IClimateLogic parent;
	public final IClimateState targetedState;
	public final IClimateState defaultState;
	public final IClimateState currentState;
	public final IClimateState startState;
	public final float resourceModifier;
	public final float changeModifier;

	public LogicInfo(IClimateLogic parent, IClimateState targetedState, IClimateState defaultState, IClimateState currentState, float resourceModifier, float changeModifier) {
		this.parent = parent;
		this.targetedState = targetedState;
		this.defaultState = defaultState;
		this.currentState = currentState.copy(true);
		this.startState = currentState.toImmutable();
		this.resourceModifier = resourceModifier;
		this.changeModifier = changeModifier;
	}

	/*public IClimateState addChange(ClimateType type, boolean backwards, boolean simulated){
		return applyChange(type, true, backwards, simulated);
	}

	public IClimateState removeChange(ClimateType type, boolean simulated){
		return applyChange(type, false, true, simulated);
	}

	/**
	 * Applies the current return value of 'change' to the current climate value of the
	 * given type. Negates the change if the machine had not enough resources to work
	 *
	 * @param type The type of climate that the change should be applied on.
	 * @param worked If the machine had enough resources to work and can hold the current climate state.
	 * @param backwards If the method can go into the opposite direction if the current state already has exceeded
	 *                          the targeted state.
	 * @param simulated If the action should only been simulated.
	 *
	 * @return The change that the method would have or has applied to the current state.
	 */
	/*private IClimateState applyChange(ClimateType type, boolean worked, boolean backwards, boolean simulated){
		IClimateState target = worked ? targetedState : defaultState;
		//Difference between the targeted state of this method and the current state.
		IClimateState difference = target.subtract(startState);
		//Do nothing if the current state already equals the targeted state of this method.
		if(ClimateStateHelper.isZero(type, difference)){
			return ClimateStateHelper.ZERO_STATE;
		}
		float change = changeSupplier.apply(type);
		//Create a mutable state that contains the current change.
		IClimateState changeState = ClimateStateHelper.INSTANCE.create(type, change).toMutable();
		boolean rightDirection = difference.getClimate(type) > 0.0F && change > 0.0F || difference.getClimate(type) < 0.0F && change < 0.0F;
		if(!rightDirection){
			IClimateState diffToDefault = startState.subtract(defaultState);
			//Check if 'bothDirections' is true or if the difference to the default state has the same direction like the change state.
			//The Second one allows to go back to the default state if the current target is above, if the change is negative, or below, if the change is positive, the last targeted state.
			if(backwards || (diffToDefault.getClimate(type) > 0.0F && change > 0.0F || diffToDefault.getClimate(type) < 0.0F && change < 0.0F)){
				//If so negate the current change so we can go back the targeted state.
				changeState.multiply(-1.0F);
			}else{
				//If not change nothing because we are not allowed to.
				return ClimateStateHelper.ZERO_STATE;
			}
		}
		IClimateState newState = startState.add(changeState);
		IClimateState newDifference = target.subtract(newState);
		float diff = newDifference.getClimate(type);
		//Round up or down to the targeted state if possible
		if(canRound(diff)){
			changeState.add(type, diff);
		}
		//Add the change state to the current state if this isn't simulated
		if(!simulated) {
			currentState.add(changeState);
		}
		return changeState;
	}


	private static boolean canRound(float diff){
		return BigDecimal.valueOf(MathHelper.abs(diff)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() <= ClimateStateHelper.CLIMATE_CHANGE;
	}*/
}
