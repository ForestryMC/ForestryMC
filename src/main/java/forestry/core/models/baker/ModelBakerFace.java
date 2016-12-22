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
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

/**
 * A face of a {@link ModelBakerModel }
 */
@SideOnly(Side.CLIENT)
public class ModelBakerFace {
	public final EnumFacing face;

	public final Vector3f to;
	public final Vector3f from;

	public final float[] uv;

	public final TextureAtlasSprite spite;

	public final int colorIndex;

	public ModelBakerFace(EnumFacing face, int colorIndex, Vector3f to, Vector3f from, float[] defUVs2, TextureAtlasSprite sprite) {
		this.colorIndex = colorIndex;
		this.face = face;
		this.to = to;
		this.from = from;
		this.uv = defUVs2;
		this.spite = sprite;
	}

}
