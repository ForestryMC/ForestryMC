/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.util.Set;

public interface IErrorStateRegistry {
	void registerErrorState(IErrorState state);
	void addAlias(IErrorState state, String name);

	IErrorState getErrorState(short id);
	IErrorState getErrorState(String name);
	Set<IErrorState> getErrorStates();

	IErrorLogic createErrorLogic();
}
