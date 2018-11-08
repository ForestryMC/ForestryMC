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
package forestry.arboriculture.models;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumLeafType;
import forestry.core.config.Constants;

public class TextureLeaves {
	private static final Map<EnumLeafType, TextureLeaves> leafTextures = new EnumMap<>(EnumLeafType.class);

	static {
		for (EnumLeafType leafType : EnumLeafType.values()) {
			leafTextures.put(leafType, new TextureLeaves(leafType));
		}
	}

	public static TextureLeaves get(EnumLeafType leafType) {
		return leafTextures.get(leafType);
	}

	@SideOnly(Side.CLIENT)
	public static void registerAllSprites() {
		for (TextureLeaves leafTexture : leafTextures.values()) {
			leafTexture.registerSprites();
		}
	}

	private final ResourceLocation plain;
	private final ResourceLocation fancy;
	private final ResourceLocation pollinatedPlain;
	private final ResourceLocation pollinatedFancy;

	private TextureLeaves(EnumLeafType enumLeafType) {
		String ident = enumLeafType.toString().toLowerCase(Locale.ENGLISH);
		this.plain = new ResourceLocation(Constants.MOD_ID, "blocks/leaves/" + ident + ".plain");
		this.fancy = new ResourceLocation(Constants.MOD_ID, "blocks/leaves/" + ident + ".fancy");
		this.pollinatedPlain = new ResourceLocation(Constants.MOD_ID, "blocks/leaves/" + ident + ".changed.plain");
		this.pollinatedFancy = new ResourceLocation(Constants.MOD_ID, "blocks/leaves/" + ident + ".changed");
	}

	@SideOnly(Side.CLIENT)
	private void registerSprites() {
		TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
		textureMapBlocks.registerSprite(plain);
		textureMapBlocks.registerSprite(fancy);
		textureMapBlocks.registerSprite(pollinatedPlain);
		textureMapBlocks.registerSprite(pollinatedFancy);
	}

	public ResourceLocation getSprite(boolean pollinated, boolean fancy) {
		if (pollinated) {
			if (fancy) {
				return this.pollinatedFancy;
			} else {
				return this.pollinatedPlain;
			}
		} else {
			if (fancy) {
				return this.fancy;
			} else {
				return this.plain;
			}
		}
	}
}