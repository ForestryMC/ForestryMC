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
package forestry.arboriculture.render;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import forestry.api.arboriculture.EnumLeafType;
import forestry.api.core.sprite.ISprite;
import forestry.core.render.TextureManager;

public class LeafTexture {
	private static final Map<EnumLeafType, LeafTexture> leafTextures = new EnumMap<EnumLeafType, LeafTexture>(
			EnumLeafType.class);

	static {
		for (EnumLeafType leafType : EnumLeafType.values()) {
			leafTextures.put(leafType, new LeafTexture(leafType));
		}
	}

	public static LeafTexture get(EnumLeafType leafType) {
		return leafTextures.get(leafType);
	}

	public static void registerAllIcons() {
		for (LeafTexture leafTexture : leafTextures.values()) {
			leafTexture.registerIcons();
		}
	}

	private final EnumLeafType leafType;

	private ISprite plain;
	private ISprite pollinated;
	private ISprite fancy;

	private LeafTexture(EnumLeafType enumLeafType) {
		this.leafType = enumLeafType;
	}

	private void registerIcons() {
		TextureManager textureManager = TextureManager.getInstance();

		String ident = leafType.toString().toLowerCase(Locale.ENGLISH);

		plain = textureManager.registerTex("blocks", "leaves/" + ident + ".plain");
		pollinated = textureManager.registerTex("blocks", "leaves/" + ident + ".changed");
		fancy = textureManager.registerTex("blocks", "leaves/" + ident + ".fancy");
	}

	public ISprite getPlain() {
		return plain;
	}

	public ISprite getPollinated() {
		return pollinated;
	}

	public ISprite getFancy() {
		return fancy;
	}
}