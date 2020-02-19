/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IErrorState extends ISpriteRegister {

	short getID();

	String getUniqueName();

	String getUnlocalizedDescription();

	String getUnlocalizedHelp();

	/**
	 * Sprite registered to the Gui Texture Map at {@link ITextureManager}
	 */
	@OnlyIn(Dist.CLIENT)
	TextureAtlasSprite getSprite();

}
