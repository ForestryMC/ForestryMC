/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IErrorState {
	
	short getID();
	
	String getUniqueName();

	String getDescription();

	String getHelp();

	@SideOnly(Side.CLIENT)
	void registerIcons(IIconRegister register);

	@SideOnly(value = Side.CLIENT)
	IIcon getIcon();

}
