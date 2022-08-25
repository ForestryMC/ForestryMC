/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

/**
 * Provides icons, needed in some interfaces, most notably for bees and trees.
 */
public interface IIconProvider {

    @SideOnly(Side.CLIENT)
    IIcon getIcon(short texUID);

    @SideOnly(Side.CLIENT)
    void registerIcons(IIconRegister register);
}
