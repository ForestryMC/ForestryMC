/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraftforge.client.model.IModel;

public enum WoodBlockKind {
	LOG, PLANKS, SLAB, FENCE, FENCE_GATE, STAIRS, DOOR;
	
	public String getName(){
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String toString() {
		return getName();
	}

	public IModel retextureModel(IModel model, IWoodType type, ImmutableMap<String, String> customTextures) {
		Map<String, String> textures = new HashMap<>();
		switch (this) {
			case SLAB:
			case STAIRS:
				String textureLocation = type.getPlankTexture();
				textures.put("particle", textureLocation);
				textures.put("side", textureLocation);
				textures.put("top", textureLocation);
				textures.put("bottom", textureLocation);
				textures.put("all", textureLocation);
				break;
			case PLANKS:
				textures.put("particle", type.getPlankTexture());
				textures.put("all", type.getPlankTexture());
				break;
			case FENCE_GATE:
			case FENCE:
				textures.put("particle", type.getPlankTexture());
				textures.put("side", type.getPlankTexture());
				textures.put("texture", type.getPlankTexture());
				break;
			case DOOR:
				textures.put("particle", type.getDoorLowerTexture());
				textures.put("bottom", type.getDoorLowerTexture());
				textures.put("top", type.getDoorUpperTexture());
				break;
			case LOG:
				textures.put("particle", type.getBarkTexture());
				textures.put("side", type.getBarkTexture());
				textures.put("all", type.getBarkTexture());
				textures.put("end", type.getHeartTexture());
				break;
			default:
				break;
		}
		textures.putAll(customTextures);
		return model.retexture(ImmutableMap.copyOf(textures));
	}
}
