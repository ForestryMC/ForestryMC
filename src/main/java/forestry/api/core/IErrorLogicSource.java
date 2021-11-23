/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

/**
 * An IErrorLogicSource uses an instance of IErrorLogic to deal with its errors.
 */
public interface IErrorLogicSource {
	IErrorLogic getErrorLogic();
}
