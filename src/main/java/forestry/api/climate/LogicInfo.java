/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
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
}
