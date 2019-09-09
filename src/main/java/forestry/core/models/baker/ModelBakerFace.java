/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.models.baker;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
/**
 * A face of a {@link ModelBakerModel }
 */
@OnlyIn(Dist.CLIENT)
public class ModelBakerFace {
	public final Direction face;

	public final TextureAtlasSprite spite;

	public final int colorIndex;

	public ModelBakerFace(Direction face, int colorIndex, TextureAtlasSprite sprite) {
		this.colorIndex = colorIndex;
		this.face = face;
		this.spite = sprite;
	}

}
