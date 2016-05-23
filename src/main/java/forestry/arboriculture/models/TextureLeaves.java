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

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import forestry.api.arboriculture.EnumLeafType;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;

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

	public static void registerAllSprites() {
		for (TextureLeaves leafTexture : leafTextures.values()) {
			leafTexture.registerSprites();
		}
	}

	private final EnumLeafType leafType;

	private TextureAtlasSprite plain;
	private TextureAtlasSprite fancy;
	private TextureAtlasSprite pollinatedPlain;
	private TextureAtlasSprite pollinatedFancy;

	private TextureLeaves(EnumLeafType enumLeafType) {
		this.leafType = enumLeafType;
	}

	private void registerSprites() {
		String ident = leafType.toString().toLowerCase(Locale.ENGLISH);

		TextureMap textureMapBlocks = Proxies.common.getClientInstance().getTextureMapBlocks();
		plain = textureMapBlocks.registerSprite(new ResourceLocation(Constants.RESOURCE_ID, "blocks/leaves/" + ident + ".plain"));
		fancy = textureMapBlocks.registerSprite(new ResourceLocation(Constants.RESOURCE_ID, "blocks/leaves/" + ident + ".fancy"));
		pollinatedPlain = textureMapBlocks.registerSprite(new ResourceLocation(Constants.RESOURCE_ID, "blocks/leaves/" + ident + ".changed.plain"));
		pollinatedFancy = textureMapBlocks.registerSprite(new ResourceLocation(Constants.RESOURCE_ID, "blocks/leaves/" + ident + ".changed"));
	}

	@Nonnull
	public TextureAtlasSprite getSprite(boolean pollinated, boolean fancy) {
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