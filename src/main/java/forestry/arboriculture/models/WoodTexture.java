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

import com.google.common.collect.ImmutableMap;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class WoodTexture {

	private WoodTexture() {
	}

	abstract ImmutableMap<String, String> getLocations(String kindName);

	static class SimpleTexture extends WoodTexture {
		private final ImmutableMap<String, String> locations;

		public SimpleTexture(ImmutableMap<String, String> locations) {
			this.locations = locations;
		}

		@Override
		ImmutableMap<String, String> getLocations(String kindName) {
			return locations;
		}
	}

	static class TextureMap extends WoodTexture {
		private final ImmutableMap<String, SimpleTexture> textures;

		public TextureMap(ImmutableMap<String, SimpleTexture> textures) {
			this.textures = textures;
		}

		@Override
		ImmutableMap<String, String> getLocations(String kindName) {
			SimpleTexture texture = textures.get(kindName);
			if (texture == null) {
				return ImmutableMap.of();
			}
			return texture.getLocations(kindName);
		}
	}

}
