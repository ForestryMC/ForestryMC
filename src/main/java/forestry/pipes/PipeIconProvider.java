/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes;

import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.render.TextureManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class PipeIconProvider implements IIconProvider {

	private static IIcon[] icons;

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int iconIndex) {
		return icons[iconIndex];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		icons = new IIcon[7];
		icons[0] = TextureManager.getInstance().registerTex(iconRegister, "pipes/pipe_prop.empty");
		icons[1] = TextureManager.getInstance().registerTex(iconRegister, "pipes/pipe_prop.black");
		icons[2] = TextureManager.getInstance().registerTex(iconRegister, "pipes/pipe_prop.white");
		icons[3] = TextureManager.getInstance().registerTex(iconRegister, "pipes/pipe_prop.red");
		icons[4] = TextureManager.getInstance().registerTex(iconRegister, "pipes/pipe_prop.blue");
		icons[5] = TextureManager.getInstance().registerTex(iconRegister, "pipes/pipe_prop.green");
		icons[6] = TextureManager.getInstance().registerTex(iconRegister, "pipes/pipe_prop.yellow");
	}

}
