/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IErrorState {

	short getID();

	String getUniqueName();

	String getUnlocalizedDescription();

	String getUnlocalizedHelp();

	@SideOnly(Side.CLIENT)
	void registerSprite();

	/**
	 * Sprite registered to the Gui Texture Map at {@link ITextureManager}
	 */
	@SideOnly(Side.CLIENT)
	TextureAtlasSprite getSprite();

}
