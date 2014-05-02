/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Provides icons, needed in some interfaces, most notably for bees and trees. 
 */
public interface IIconProvider {
	
	@SideOnly(Side.CLIENT)
	IIcon getIcon(short texUID);
	
	@SideOnly(Side.CLIENT)
	void registerIcons(IIconRegister register);

}
