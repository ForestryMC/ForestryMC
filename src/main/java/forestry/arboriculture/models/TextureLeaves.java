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

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import forestry.api.arboriculture.EnumLeafType;
import forestry.core.render.TextureManager;

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
	private TextureAtlasSprite pollinated;
	private TextureAtlasSprite fancy;

	private TextureLeaves(EnumLeafType enumLeafType) {
		this.leafType = enumLeafType;
	}

	private void registerSprites() {
		String ident = leafType.toString().toLowerCase(Locale.ENGLISH);

		plain = TextureManager.registerSprite("blocks/leaves/" + ident + ".plain");
		pollinated = TextureManager.registerSprite("blocks/leaves/" + ident + ".changed");
		fancy = TextureManager.registerSprite("blocks/leaves/" + ident + ".fancy");
	}

	public TextureAtlasSprite getPlain() {
		return plain;
	}

	public TextureAtlasSprite getPollinated() {
		return pollinated;
	}

	public TextureAtlasSprite getFancy() {
		return fancy;
	}
}