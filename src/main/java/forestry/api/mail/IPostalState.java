/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.mail;

import net.minecraft.network.chat.Component;

public interface IPostalState {
	/**
	 * Normal states are OK, error states are not OK
	 */
	boolean isOk();

	/**
	 * Localized description of the postal state
	 */
	Component getDescription();
}
