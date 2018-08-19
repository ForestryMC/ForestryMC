/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

public class TransformerProperties {
	public final IClimateTransformer parent;
	public final IClimateState targetedState;
	public final IClimateState defaultState;
	public final IClimateState currentState;
	public final IClimateState startState;

	public TransformerProperties(IClimateTransformer parent, IClimateState targetedState, IClimateState defaultState, IClimateState currentState) {
		this.parent = parent;
		this.targetedState = targetedState;
		this.defaultState = defaultState;
		this.currentState = currentState.copy(true);
		this.startState = currentState.toImmutable();
	}
}
