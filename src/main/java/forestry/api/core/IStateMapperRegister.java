/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @deprecated TODO Remove in 1.13: Not needed in the api
 */
@Deprecated
public interface IStateMapperRegister {

	@SideOnly(Side.CLIENT)
	void registerStateMapper();

}
