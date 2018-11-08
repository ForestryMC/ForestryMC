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
package forestry.core.models;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {

	public static final Function<ResourceLocation, TextureAtlasSprite> INSTANCE = new DefaultTextureGetter();

	@Override
	public TextureAtlasSprite apply(@Nullable ResourceLocation location) {
		Preconditions.checkNotNull(location);
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
	}

}